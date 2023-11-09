package mx.com.sigrama.ars.device;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.com.sigrama.ars.common.LinearInterpolator;
import mx.com.sigrama.ars.common.SplineInterpolator;

public class ResampledData {
    public class DATUM {
        double t;
        double[] y;
        public DATUM (double t, double[] y) {
            this.t = t;
            this.y = y;
        }
        public double getT() {
            return t;
        }
        public double[] getY() {
            return y;
        }
        public double getY(int index) {
            return y[index];
        }
    }
    public class DATA {
        DATUM[] data;
        int RESAMPLE_SIZE;
        int NO_OF_CHANNELS;
        String [] channelNames;
        int[] channelColors;
        public DATA(DATUM[] data, String [] channelNames) {
            this.data = data;
            RESAMPLE_SIZE = data.length;
            NO_OF_CHANNELS = data[0].y.length;
            this.channelNames = channelNames;
            channelColors = new int[]{
                    Color.BLACK,
                    Color.BLUE,
                    Color.CYAN,
                    Color.DKGRAY,
                    Color.GRAY,
                    Color.GREEN,
                    Color.LTGRAY,
                    Color.MAGENTA,
                    Color.RED,
                    Color.YELLOW
            };

        }
        public DATUM[] getData() {
            return data;
        }
        public int getRESAMPLE_SIZE() {
            return RESAMPLE_SIZE;
        }
        public int getNO_OF_CHANNELS() {
            return NO_OF_CHANNELS;
        }
        public String [] getChannelNames() {
            return channelNames;
        }
        public DATUM getDatum(int index) {
            return data[index];
        }
        public String getChannelName(int index) {
            return channelNames[index];
        }
        public int getChannelColor(int index) {
            return channelColors[index];
        }
    }
    DATA data;
    private double RESAMPLE_STEP_SIZE = 0.00005d;
    private int RESAMPLE_SIZE;
    private int NO_OF_CHANNELS;
    private int SAMPLE_SIZE;
    private SignalConditioningAndProcessing.DATAPOINT[][] voltages;
    private SignalConditioningAndProcessing.DATAPOINT[][] currents;
    public ResampledData(SignalConditioningAndProcessing.DATAPOINT[][] voltages, SignalConditioningAndProcessing.DATAPOINT[][] currents) {
        this.voltages = voltages;
        this.currents = currents;
        this.NO_OF_CHANNELS = voltages[0].length + currents[0].length;
        this.SAMPLE_SIZE = voltages.length;

        DATUM [] tempData = prepareDataVariableWithUniformStepSize();
        resampleAllChannelsUsingSplineInterpolator(tempData);
        //resampleAllChannelsUsingLinearInterpolator(tempData);
        offsetTimeToStartFromZero(tempData);
        this.data = new DATA(tempData, generateChannelNames());

        /*

        // Preparing data variable with the correct size
        Log.d("SKGadi", "Original Sample size: " + SAMPLE_SIZE);
        Log.d("SKGadi", "Original Sampling time: "+ voltages[1][0].t);


        //Obtaining startTime which is maximum time of first sample of voltage and current
        double startTime = voltages[0][0].t;
        for (int i=0; i<voltages[0].length; i++) {
            if (voltages[0][i].t>startTime) {
                startTime = voltages[0][i].t;
            }
        }
        for (int i=0; i<currents[0].length; i++) {
            if (currents[0][i].t>startTime) {
                startTime = currents[0][i].t;
            }
        }


        //Obtaining endTime which is minimum time of last sample of voltage and current
        double endTime = voltages[SAMPLE_SIZE-1][0].t;
        for (int i=0; i<voltages[0].length; i++) {
            if (voltages[SAMPLE_SIZE-1][i].t<endTime) {
                endTime = voltages[SAMPLE_SIZE-1][i].t;
            }
        }
        for (int i=0; i<currents[0].length; i++) {
            if (currents[SAMPLE_SIZE-1][i].t<endTime) {
                endTime = currents[SAMPLE_SIZE-1][i].t;
            }
        }
        RESAMPLE_SIZE = (int)((endTime-startTime)/RESAMPLE_STEP_SIZE);

        DATUM [] tempData = new DATUM[RESAMPLE_SIZE];
        for (int i=0; i<RESAMPLE_SIZE; i++) {
            tempData[i] = new DATUM(startTime+i*RESAMPLE_STEP_SIZE, new double[NO_OF_CHANNELS]);
        }

        // Resampling voltages
        for (int i=0; i<voltages[0].length; i++) {
            // Preparing List<Float> x and List<Float> y for SplineInterpolator
            List<Float> x = new ArrayList<Float>();
            List<Float> y = new ArrayList<Float>();
            for (int j=0; j<SAMPLE_SIZE; j++) {
                x.add((float)voltages[j][i].t);
                y.add((float)voltages[j][i].y);
            }
            SplineInterpolator spline = SplineInterpolator.createMonotoneCubicSpline(x, y);
            for (int j=0; j<RESAMPLE_SIZE; j++) {
                tempData[j].y[i] = spline.interpolate((float) tempData[j].t);
            }
        }

        // Resampling currents
        for (int i=0; i<currents[0].length; i++) {
            // Preparing List<Float> x and List<Float> y for SplineInterpolator
            List<Float> x = new ArrayList<Float>();
            List<Float> y = new ArrayList<Float>();
            for (int j=0; j<SAMPLE_SIZE; j++) {
                x.add((float)currents[j][i].t);
                y.add((float)currents[j][i].y);
            }
            SplineInterpolator spline = SplineInterpolator.createMonotoneCubicSpline(x, y);
            for (int j=0; j<RESAMPLE_SIZE; j++) {
                tempData[j].y[i+voltages[0].length] = spline.interpolate((float) tempData[j].t);
            }
        }
        //Offsetting the time to start from zero
        for (int i=0; i<RESAMPLE_SIZE; i++) {
            tempData[i].t = tempData[i].t - startTime;
        }

        String [] channelNames = new String[NO_OF_CHANNELS];
        for (int i=0; i<voltages[0].length; i++) {
            //channelNames[i] = "Voltage Line " + (i+1) + " - " + ((i+1)%3 + 1);
            channelNames[i] = "Voltage L " + (i+1) + " - E";
        }
        for (int i=0; i<currents[0].length; i++) {
            //channelNames[i+voltages[0].length] = "Current Line " + (i+1) + " - " + ((i+1)%3 + 1);
            channelNames[i+voltages[0].length] = "Current L " + (i+1) + " - E";
        }

        data = new DATA(tempData, channelNames);

         */

    }

    /**
     * Prepare DATUM[] data with uniform step size
     * Takes no parameters
     * Returns DATUM[] data variable with uniform step size and correct size
     */
    private DATUM[] prepareDataVariableWithUniformStepSize() {
        //Obtaining startTime which is maximum time of first sample of voltage and current
        double startTime = voltages[0][0].t;
        for (int i=0; i<voltages[0].length; i++) {
            if (voltages[0][i].t>startTime) {
                startTime = voltages[0][i].t;
            }
        }
        for (int i=0; i<currents[0].length; i++) {
            if (currents[0][i].t>startTime) {
                startTime = currents[0][i].t;
            }
        }

        //Obtaining endTime which is minimum time of last sample of voltage and current
        double endTime = voltages[SAMPLE_SIZE-1][0].t;
        for (int i=0; i<voltages[0].length; i++) {
            if (voltages[SAMPLE_SIZE-1][i].t<endTime) {
                endTime = voltages[SAMPLE_SIZE-1][i].t;
            }
        }
        for (int i=0; i<currents[0].length; i++) {
            if (currents[SAMPLE_SIZE-1][i].t<endTime) {
                endTime = currents[SAMPLE_SIZE-1][i].t;
            }
        }
        RESAMPLE_SIZE = (int)((endTime-startTime)/RESAMPLE_STEP_SIZE);

        DATUM [] tempData = new DATUM[RESAMPLE_SIZE];
        for (int i=0; i<RESAMPLE_SIZE; i++) {
            tempData[i] = new DATUM(startTime+i*RESAMPLE_STEP_SIZE, new double[NO_OF_CHANNELS]);
        }
        return tempData;
    }


    /**
     * Resamples a SignalConditioningAndProcessing.DATAPOINT[][] for a given chanel for DATUM[]
     * It updates the inputData variable with the resampled data for the given channel
     * Uses spline interpolation
     * @param inputData data prepared with t with uniform step size
     * @param inputDataChannel channel number
     * @param dataPoints unsampled data from SignalConditioningAndProcessing.DATAPOINT[][]
     * @param dataPointChannel channel number
     * @return void
     */
    private void resampleDataForChannelUsingSplineInterpolator(DATUM[] inputData, int inputDataChannel, SignalConditioningAndProcessing.DATAPOINT[][] dataPoints, int dataPointChannel) {
        // Preparing List<Float> x and List<Float> y for SplineInterpolator
        List<Float> x = new ArrayList<Float>();
        List<Float> y = new ArrayList<Float>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            x.add((float) dataPoints[i][dataPointChannel].t);
            y.add((float) dataPoints[i][dataPointChannel].y);
        }
        SplineInterpolator spline = SplineInterpolator.createMonotoneCubicSpline(x, y);
        for (int i = 0; i < RESAMPLE_SIZE; i++) {
            inputData[i].y[inputDataChannel] = spline.interpolate((float) inputData[i].t);
        }
    }

    /**
     * Resamples a SignalConditioningAndProcessing.DATAPOINT[][] for a given chanel for DATUM[]
     * It updates the inputData variable with the resampled data for the given channel
     * Uses linear interpolation
     * @param inputData data prepared with t with uniform step size
     * @param inputDataChannel channel number
     * @param dataPoints unsampled data from SignalConditioningAndProcessing.DATAPOINT[][]
     * @param dataPointChannel channel number
     * @return void
     */
    private void resampleDataForChannelUsingLinearInterpolator(DATUM[] inputData, int inputDataChannel, SignalConditioningAndProcessing.DATAPOINT[][] dataPoints, int dataPointChannel) {
        // Preparing List<Float> x and List<Float> y for SplineInterpolator
        float[] x = new float[SAMPLE_SIZE];
        float[] y = new float[SAMPLE_SIZE];
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            x[i] = (float) dataPoints[i][dataPointChannel].t;
            y[i] = (float) dataPoints[i][dataPointChannel].y;
        }
        LinearInterpolator linearInterpolator = new LinearInterpolator(x, y);
        for (int i = 0; i < RESAMPLE_SIZE; i++) {
            inputData[i].y[inputDataChannel] = linearInterpolator.interpolate((float) inputData[i].t);
        }
    }

    /**
     * Resamples all channels of voltages and currents
     * Uses spline interpolation
     * @param inputData data prepared with t with uniform step size
     * @return void
     */
    private void resampleAllChannelsUsingSplineInterpolator(DATUM[] inputData) {
        // Resampling voltages
        for (int i = 0; i < voltages[0].length; i++) {
            resampleDataForChannelUsingSplineInterpolator(inputData, i, voltages, i);
        }

        // Resampling currents
        for (int i = 0; i < currents[0].length; i++) {
            resampleDataForChannelUsingSplineInterpolator(inputData, i + voltages[0].length, currents, i);
        }
    }

    /**
     * Resamples all channels of voltages and currents
     * Uses linear interpolation
     * @param inputData data prepared with t with uniform step size
     * @return void
     */
    private void resampleAllChannelsUsingLinearInterpolator(DATUM[] inputData) {
        // Resampling voltages
        for (int i = 0; i < voltages[0].length; i++) {
            resampleDataForChannelUsingLinearInterpolator(inputData, i, voltages, i);
        }

        // Resampling currents
        for (int i = 0; i < currents[0].length; i++) {
            resampleDataForChannelUsingLinearInterpolator(inputData, i + voltages[0].length, currents, i);
        }
    }

    /**
     * Offsets the time of DATUM[] data to start from zero
     * Updates the inputData variable with the offsetted data
     * @param inputData data prepared with t with uniform step size
     * @return void
     */
    private void offsetTimeToStartFromZero(DATUM[] inputData) {
        //Offsetting the time to start from zero
        double startTime = inputData[0].t;
        for (int i = 0; i < RESAMPLE_SIZE; i++) {
            inputData[i].t = inputData[i].t - startTime;
        }
    }

    /**
     * Generates names for all channels
     * @return String[] channelNames
     */
    private String[] generateChannelNames() {
        String[] channelNames = new String[NO_OF_CHANNELS];
        for (int i = 0; i < voltages[0].length; i++) {
            //channelNames[i] = "Voltage Line " + (i+1) + " - " + ((i+1)%3 + 1);
            channelNames[i] = "Voltage L " + (i + 1) + " - E";
        }
        for (int i = 0; i < currents[0].length; i++) {
            //channelNames[i+voltages[0].length] = "Current Line " + (i+1) + " - " + ((i+1)%3 + 1);
            channelNames[i + voltages[0].length] = "Current L " + (i + 1) + " - E";
        }
        return channelNames;
    }


    public DATA getData() {
        return data;
    }
    public int getRESAMPLE_SIZE() {
        return RESAMPLE_SIZE;
    }
    public int getNO_OF_CHANNELS() {
        return NO_OF_CHANNELS;
    }
    public int getSAMPLE_SIZE() {
        return SAMPLE_SIZE;
    }

    /**
     * Created by SKGadi on 7th November 2023
     * prepare data for displaying on oscilloscope
     * It processes the data variable and returns a new data variable
     *
     * The new data variable is prepared by implementing the following steps:
     * 1. removing the first few resampled data points such that the first channel value is
     * increasing and has a zero crossing
     * 2. remove the last few resampled data points such that the time-period is 50ms
     * 3. offset the time to start from zero
     *
     *
     * @return DATA data for displaying on oscilloscope
     */
    public DATA obtainDataForOscilloscope() {
        // 1. removing the first few resampled data points such that the first channel value is
        // increasing and has a zero crossing
        int startIndex = -1;
        for (int i = 0; i < data.getRESAMPLE_SIZE() - 1; i++) {
            if (
                    data.getDatum(i).getY(0) < data.getDatum(i + 1).getY(0) &&
                            data.getDatum(i).getY(0) < 0 &&
                            data.getDatum(i + 1).getY(0) > 0
            ) {
                startIndex = i;
                break;
            }
        }
        if (startIndex == -1) {
            Log.e("SKGadi", "zero crossing towards positive not found");
            return null;
        }

        // 2. remove the last few resampled data points such that the time-period is 100ms
        int endIndex = -1;
        for (int i = startIndex; i < data.getRESAMPLE_SIZE() - 1; i++) {
            if (data.getDatum(i).getT() - data.getDatum(startIndex).getT() >= 0.05) {
                endIndex = i;
                break;
            }
        }
        if (endIndex == -1) {
            Log.e("SKGadi", "not enough data for 50ms");
            return null;
        }

        // 3. offset the time to start from zero
        DATUM[] tempData = new DATUM[endIndex - startIndex];
        for (int i = 0; i < endIndex - startIndex; i++) {
            tempData[i] = new DATUM(
                    data.getDatum(i + startIndex).getT() - data.getDatum(startIndex).getT(),
                    new double[data.getNO_OF_CHANNELS()]
            );
            for (int j = 0; j < data.getNO_OF_CHANNELS(); j++) {
                tempData[i].y[j] = data.getDatum(i + startIndex).getY(j);
            }
        }
        return new DATA(tempData, data.getChannelNames());
    }

}
