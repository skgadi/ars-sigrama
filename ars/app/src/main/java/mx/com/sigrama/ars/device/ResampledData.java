package mx.com.sigrama.ars.device;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public ResampledData(SignalConditioningAndProcessing.DATAPOINT[][] voltages, SignalConditioningAndProcessing.DATAPOINT[][] currents) {
        int NO_OF_CHANNELS = voltages[0].length + currents[0].length;

        // Preparing data variable with the correct size
        int SAMPLE_SIZE = voltages.length;

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
            channelNames[i] = "Voltage Line " + (i+1) + " - " + ((i+1)%3 + 1);
        }
        for (int i=0; i<currents[0].length; i++) {
            channelNames[i+voltages[0].length] = "Current Line " + (i+1) + " - " + ((i+1)%3 + 1);
        }

        data = new DATA(tempData, channelNames);


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

}
