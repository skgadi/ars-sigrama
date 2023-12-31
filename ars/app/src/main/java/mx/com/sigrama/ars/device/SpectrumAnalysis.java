package mx.com.sigrama.ars.device;

import android.util.Log;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;

import mx.com.sigrama.ars.common.PowerQuality;

/**
 * Created by SKGadi on 9th November 2023
 * This class is used to perform spectrum analysis on the resampled data
 * It takes in the resampled data and performs FFT on it
 * It returns the FFT data
 * It also returns the frequency data
 */
public class SpectrumAnalysis {

    int RESAMPLE_SIZE;
    int FFT_INPUT_SIZE;
    double[] frequencyData;
    Complex [][] fftData;
    ResampledData resampledData;
    double FUNDAMENTAL_FREQUENCY = -1;
    int FUNDAMENTAL_FREQUENCY_INDEX = -1;
    double FUNDAMENTAL_FREQUENCY_MAX_LIMIT = 100;
    double FUNDAMENTAL_FREQUENCY_MIN_LIMIT = 20;
    Complex [][] fftDataForHarmonics;

    double[] THD; //Total Harmonic Distortion

    private PowerQuality powerQuality;

    private boolean PHASE_SEQUENCE_CLOCKWISE = true;

    private double[] crestFactor;

    /**
     * This is the constructor for the SpectrumAnalysis class
     * @param resampledData
     */
    public SpectrumAnalysis(ResampledData resampledData) {

        this.resampledData = resampledData;
        //Check if resampled data is null
        if (resampledData == null){
            Log.e("SKGadi", "SpectrumAnalysis: Resampled data is null");
            return;
        }
        if (resampledData.getData() == null){
            Log.e("SKGadi", "SpectrumAnalysis: Resampled data is null");
            return;
        }
        //Log.d("SKGadi", "Spectrum analisis started");
        //Get the resample size
        this.RESAMPLE_SIZE = resampledData.getRESAMPLE_SIZE();
        //Determine number of samples for FFT. This is the next power of 2 and greater than the resample size
        this.FFT_INPUT_SIZE = (int) Math.pow(2, Math.ceil(Math.log(this.RESAMPLE_SIZE)/Math.log(2)));

        //Generate frequency data
        generateFrequencyData();

        //Generate FFT data
        // fftData size is sum of number of channels in voltage and current
        // which is equal to the number of channels in resampled data
        fftData = new Complex[resampledData.getData().getNO_OF_CHANNELS()][];
        for (int i=0; i<resampledData.getData().getNO_OF_CHANNELS(); i++) {
            generateFFTData(i);
        }

        //Generate fundamental frequency
        generateFundamentalFrequency();

        //Extract harmonics data
        extractHarmonicsData();

        //Offset phase
        offsetPhase();

        //Prepare power quality object
        preparePowerQuality();

        //Calculate Phase Sequence
        calculatePhaseSequence();

        //Calculate crest factor
        calculateCrestFactor();

        //Shows summary of the spectrum analysis in logcat for debugging
        //showSummary();
    }

    /**
     * This function generates the frequency data for the FFT
     * @return void
     */
    private void generateFrequencyData() {

        //Frequency data depends on the sampling frequency and the number of samples
        //Frequency data is a linearly increasing array from 0 to sampling frequency
        //The array size should be FFT_INPUT_SIZE

        //Obtaining sampling frequency from the resampled data
        double samplingFrequency = resampledData.getResampleFrequency();

        //Initializing the variable for frequency data
        frequencyData = new double[this.FFT_INPUT_SIZE];

        //Generating the frequency data
        for (int i = 0; i < this.FFT_INPUT_SIZE; i++) {
            frequencyData[i] = i * samplingFrequency / this.FFT_INPUT_SIZE;
        }
    }


    /**
     * This function generates input data for the FFT from the resampled data
     * for a particular channel
     * @param channel int
     * @return double[]
     */
    private double[] generateFFTInputData(int channel) {

        //Make an array of doubles from the resampled data for the channel
        //The array size should be FFT_INPUT_SIZE
        // The array should be padded with zeros

        //Get the resampled data array
        ResampledData.DATA resampledDataArray = resampledData.getData();

        //Initializing the variable for Input data for FFT
        double[] fftInputData = new double[this.FFT_INPUT_SIZE];

        //Copy the resampled data to the FFT input data
        for (int i = 0; i < this.RESAMPLE_SIZE; i++) {
            fftInputData[i] = resampledDataArray.getDatum(i).getY(channel);
        }

        //Pad the FFT input data with zeros
        for (int i = this.RESAMPLE_SIZE; i < this.FFT_INPUT_SIZE; i++) {
            fftInputData[i] = 0;
        }


        return fftInputData;
    }

    /**
     * This function generates FFT data for a particular channel
     * the result is updated in the fftData variable
     * @param channel int
     *                The channel for which FFT data is to be generated
     * @return void
     */
    private void generateFFTData(int channel) {

        //Make an array of doubles from the resampled data for the channel
        //The array size should be FFT_INPUT_SIZE
        // The array should be padded with zeros

        //Perform FFT on the input data
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        fftData[channel] = fft.transform(generateFFTInputData(channel), org.apache.commons.math3.transform.TransformType.FORWARD);

        //Correct the FFT magnitude
        //The FFT magnitude is multiplied by 2 and divided by FFT_INPUT_SIZE
        //The FFT magnitude is multiplied by 2 to account for the negative frequencies
        //The FFT magnitude is divided by FFT_INPUT_SIZE to account for the number of samples
        for (int i = 0; i < this.FFT_INPUT_SIZE; i++) {
            fftData[channel][i] = fftData[channel][i].multiply(2.0d / this.FFT_INPUT_SIZE);
        }
    }

    /**
     * This function generates a summary of the spectrum analysis and displays it in logcat
     * @return void
     */
    private void showSummary() {

        //Display the summary in logcat
        Log.d("SKGadi", "SpectrumAnalysis: Summary");
        Log.d("SKGadi", "SpectrumAnalysis: Resample size: " + this.RESAMPLE_SIZE);
        Log.d("SKGadi", "SpectrumAnalysis: FFT input size: " + this.FFT_INPUT_SIZE);
        Log.d("SKGadi", "SpectrumAnalysis: Frequency data size: " + this.frequencyData.length);
        Log.d("SKGadi", "SpectrumAnalysis: FFT data size: " + this.fftData.length);
        Log.d("SKGadi", "SpectrumAnalysis: FFT data size: " + this.fftData[0].length);
        Log.d("SKGadi", "SpectrumAnalysis: Fundamental frequency: " + this.FUNDAMENTAL_FREQUENCY);
        //Showing first three frequency data
        Log.d("SKGadi", "SpectrumAnalysis: frequency[0]: " + this.frequencyData[0]);
        Log.d("SKGadi", "SpectrumAnalysis: frequency[1]: " + this.frequencyData[1]);
        Log.d("SKGadi", "SpectrumAnalysis: frequency[2]: " + this.frequencyData[2]);
        //Showing first three FFT data of channel 0
        Log.d("SKGadi", "SpectrumAnalysis: fftData[0][0]: " + this.fftData[0][0]);
        Log.d("SKGadi", "SpectrumAnalysis: fftData[0][1]: " + this.fftData[0][1]);
        Log.d("SKGadi", "SpectrumAnalysis: fftData[0][2]: " + this.fftData[0][2]);

        //Showing last three frequency data
        Log.d("SKGadi", "SpectrumAnalysis: frequency[" + (this.frequencyData.length-1) + "]: " + this.frequencyData[this.frequencyData.length-1]);
        Log.d("SKGadi", "SpectrumAnalysis: frequency[" + (this.frequencyData.length-2) + "]: " + this.frequencyData[this.frequencyData.length-2]);
        Log.d("SKGadi", "SpectrumAnalysis: frequency[" + (this.frequencyData.length-3) + "]: " + this.frequencyData[this.frequencyData.length-3]);
        //Showing last three FFT data of channel 0
        Log.d("SKGadi", "SpectrumAnalysis: fftData[0][" + (this.fftData[0].length-1) + "]: " + this.fftData[0][this.fftData[0].length-1]);
        Log.d("SKGadi", "SpectrumAnalysis: fftData[0][" + (this.fftData[0].length-2) + "]: " + this.fftData[0][this.fftData[0].length-2]);
        Log.d("SKGadi", "SpectrumAnalysis: fftData[0][" + (this.fftData[0].length-3) + "]: " + this.fftData[0][this.fftData[0].length-3]);

    }

    /**
     * This function generates the fundamental frequency for the channel 0 of the resampled data in Hz
     * The result is updated in the FUNDAMENTAL_FREQUENCY variable
     * @return void
     */
    public void  generateFundamentalFrequency() {
        //Identify the fundamental frequency from the FFT data
        //The fundamental frequency is the frequency with the maximum magnitude
        //The frequency data is a linearly increasing array from 0 to sampling frequency
        //The FFT data is a complex array
        //The magnitude of the FFT data is the square root of the sum of squares of real and imaginary parts
        //The fundamental frequency is the frequency corresponding to the maximum magnitude
        //The fundamental frequency is returned in Hz

        //Initializing the variable for fundamental frequency
        FUNDAMENTAL_FREQUENCY = 0;

        //Initializing the variable for maximum magnitude
        double maxMagnitude = 0;

        //Iterating through the FFT data
        for (int i=0; i<this.FFT_INPUT_SIZE/2; i++) {
            //Calculating the magnitude
            double magnitude = Math.sqrt(Math.pow(this.fftData[0][i].getReal(), 2) + Math.pow(this.fftData[0][i].getImaginary(), 2));
            //Checking if the magnitude is greater than the maximum magnitude
            if (magnitude > maxMagnitude) {
                //Updating the maximum magnitude
                maxMagnitude = magnitude;
                //Updating the fundamental frequency
                FUNDAMENTAL_FREQUENCY = this.frequencyData[i];
                //Updating the fundamental frequency index
                FUNDAMENTAL_FREQUENCY_INDEX = i;
            }
        }
        //For debugging purpose show DC component and first three harmonics to see FUNDAMENTAL_FREQUENCY_INDEX is correct
        //Log.d("SKGadi", "SpectrumAnalysis: FUNDAMENTAL_FREQUENCY_INDEX: " + FUNDAMENTAL_FREQUENCY_INDEX);
        //Log.d("SKGadi", "SpectrumAnalysis: DC component: " + frequencyData[0]);
        //Log.d("SKGadi", "SpectrumAnalysis: FUNDAMENTAL_FREQUENCY: " + frequencyData[FUNDAMENTAL_FREQUENCY_INDEX]);
        //Log.d("SKGadi", "SpectrumAnalysis: 2*FUNDAMENTAL_FREQUENCY: " + frequencyData[2*FUNDAMENTAL_FREQUENCY_INDEX]);
        //Log.d("SKGadi", "SpectrumAnalysis: 3*FUNDAMENTAL_FREQUENCY: " + frequencyData[3*FUNDAMENTAL_FREQUENCY_INDEX]);

    }

    /**
     * This function extracts harmonics data from the FFT data
     * The result is updated in the fftDataForHarmonics variable
     * The harmonics data is a complex array
     * The harmonics data is a subset of the FFT data
     * It contains the fundamental frequency and its harmonics
     * Ignoring frequencies other than the fundamental frequency and its harmonics
     *
     * The fundamental frequency is the frequency with the maximum magnitude
     * The multiples of the fundamental frequency are the harmonics
     *
     * The frequency data is a linearly increasing array from 0 to sampling frequency
     *
     * In fftData, the harmonics may not be at the exact multiples of the fundamental frequency
     * The harmonics are identified by finding the frequency closest to the multiples of the fundamental frequency
     *
     * The array size of the harmonics data is the number of harmonics plus 1
     * The number of harmonics is the number of multiples of the fundamental frequency
     * The DC component is also included in the harmonics data
     *
     * @return void
     */
    public void extractHarmonicsData() {

        //Calculate the FUNDAMENTAL_FREQUENCY if it is not calculated already
        if (this.FUNDAMENTAL_FREQUENCY == -1) {
            generateFundamentalFrequency();
        }
        // Check if the FUNDAMENTAL_FREQUENCY is within the limits
        if (!isFundamentalFrequencyWithinLimits()) {
            Log.e("SKGadi", "SpectrumAnalysis: Fundamental frequency is not within the limits");
            return;
        }

        //Calculate the number of harmonics
        int numberOfHarmonics = (int) Math.floor(this.frequencyData[this.frequencyData.length-1]/this.FUNDAMENTAL_FREQUENCY/2f);
        //numberOfHarmonics may be negative in that case keep the fftDataForHarmonics null

        if (numberOfHarmonics < 0) {
            Log.e("SKGadi", "SpectrumAnalysis: Number of harmonics is negative");
            return;
        }


        //Initializing the variable for harmonics data
        fftDataForHarmonics = new Complex[fftData.length][];




        //Iterating through the FFT data
        for (int i=0; i<this.fftData.length; i++) {
            //Initializing the variable for harmonics data
            fftDataForHarmonics[i] = new Complex[numberOfHarmonics];
            //Initializing the variable for harmonics data index
            int harmonicsDataIndex = 0;
            //Iterating through fftData[i] with step size of FUNDAMENTAL_FREQUENCY_INDEX
            for (int j=0; j<numberOfHarmonics; j++) {
                //Updating the harmonics data
                fftDataForHarmonics[i][harmonicsDataIndex++] = this.fftData[i][j*this.FUNDAMENTAL_FREQUENCY_INDEX];
            }
            //Log.d("SKGadi", "SpecturmAnalysis: Harmonics data size: " + harmonicsDataIndex);
        }

        //Calculate THD
        calculateTHD();
    }

    /**
     * This function calculates the total harmonic distortion
     * The result is updated in the THD variable
     */
    public void calculateTHD() {
        //Check if fftDataForHarmonics is null
        if (this.fftDataForHarmonics == null) {
            return;
        }
        //Initialize total harmonic distortion
        THD = new double[fftData.length];

        //Iterate through fftDataForHarmonics
        for (int i = 0; i < this.fftDataForHarmonics.length; i++) {
            //Check if fftDataForHarmonics[i] is null
            if (this.fftDataForHarmonics[i] == null) {
                return;
            }
            //Check if fftDataForHarmonics[i].length is within the limits
            if (this.fftDataForHarmonics[i].length < 1) {
                return;
            }
            //Calculate THD
            double numerator = 0;
            double denominator = 0;
            for (int j = 2; j < this.fftDataForHarmonics[i].length; j++) {
                numerator += Math.pow(this.fftDataForHarmonics[i][j].abs(), 2);
            }
            denominator = numerator + Math.pow(this.fftDataForHarmonics[i][1].abs(), 2);

            this.THD[i] = Math.sqrt(numerator / denominator) * 100;
        }
    }

    /**
     * This function tests if the fundamental frequency is within the limits
     * @return boolean
     */
    public boolean isFundamentalFrequencyWithinLimits() {
        if (this.FUNDAMENTAL_FREQUENCY < this.FUNDAMENTAL_FREQUENCY_MIN_LIMIT || this.FUNDAMENTAL_FREQUENCY > this.FUNDAMENTAL_FREQUENCY_MAX_LIMIT) {
            return false;
        }
        return true;
    }
    public double getFundamentalFrequency() {
        return FUNDAMENTAL_FREQUENCY;
    }

    public Complex[][] getFftDataForHarmonics() {
        return fftDataForHarmonics;
    }

    public Complex[][] getFftData() {
        return fftData;
    }

    /**
     * This function returns fundamental value for the channel
     * @param channel int
     * @return Complex
     */
    public Complex getFundamentalValue(int channel) {
        //Check if fftDataForHarmonics is null
        if (this.fftDataForHarmonics == null) {
            return null;
        }
        //Check if channel is within the limits
        if (channel < 0 || channel >= this.fftDataForHarmonics.length) {
            return null;
        }
        //Check if fftDataForHarmonics[channel] is null
        if (this.fftDataForHarmonics[channel] == null) {
            return null;
        }
        //Check if fftDataForHarmonics[channel].length is within the limits
        if (this.fftDataForHarmonics[channel].length < 1) {
            return null;
        }
        //Return the fundamental value
        return this.fftDataForHarmonics[channel][1];
    }
    /**
     * This function offsets all the values in the fftDataForHarmonics such that
     * the phase of the fundamental frequency of channel 0 is zero
     */
    public void offsetPhase() {
        //Check if fftDataForHarmonics is null
        if (this.fftDataForHarmonics == null) {
            return;
        }
        //Check if fftDataForHarmonics[0] is null
        if (this.fftDataForHarmonics[0] == null) {
            return;
        }
        //Check if fftDataForHarmonics[0].length is within the limits
        if (this.fftDataForHarmonics[0].length < 1) {
            return;
        }
        //Get the phase of the fundamental frequency of channel 0
        double phase = this.fftDataForHarmonics[0][1].getArgument();
        //Iterate through fftDataForHarmonics
        for (int i = 0; i < this.fftDataForHarmonics.length; i++) {
            //Iterate through fftDataForHarmonics[i]
            for (int j = 0; j < this.fftDataForHarmonics[i].length; j++) {
                //Offset the phase of fftDataForHarmonics[i][j]
                this.fftDataForHarmonics[i][j] = this.fftDataForHarmonics[i][j].multiply(new Complex(Math.cos(-phase), Math.sin(-phase)));
            }
        }
    }

    /**
     * This function returns harmonics as percentage of fundamental value
     * @param channel int
     *                The channel for which harmonics is to be calculated
     * @param harmonic int
     *                 The harmonic for which harmonics is to be calculated
     * @return double
     *        The harmonics as percentage of fundamental value
     */
    public double getHarmonicsPercentage(int channel, int harmonic) {
        //Check if fftDataForHarmonics is null
        if (this.fftDataForHarmonics == null) {
            return 0;
        }
        //Check if channel is within the limits
        if (channel < 0 || channel >= this.fftDataForHarmonics.length) {
            return 0;
        }
        //Check if fftDataForHarmonics[channel] is null
        if (this.fftDataForHarmonics[channel] == null) {
            return 0;
        }
        //Check if fftDataForHarmonics[channel].length is within the limits
        if (this.fftDataForHarmonics[channel].length < 1) {
            return 0;
        }
        //Check if harmonic is within the limits
        if (harmonic < 0 || harmonic >= this.fftDataForHarmonics[channel].length) {
            return 0;
        }
        //Return the harmonics as percentage of fundamental value
        return this.fftDataForHarmonics[channel][harmonic].abs() / this.fftDataForHarmonics[channel][1].abs() * 100;

    }

    /**
     * This function returns the THD for the channel
     * @param channel int
     *                The channel for which THD is to be calculated
     * @return double
     *       The THD for the channel
     */
    public double getTHD(int channel) {
        //Check if THD is null
        if (this.THD == null) {
            return 0;
        }
        //Check if channel is within the limits
        if (channel < 0 || channel >= this.THD.length) {
            return 0;
        }
        //Return the THD for the channel
        return this.THD[channel];
    }

    /**
     * This function prepares powerQuality object
     * @return void
     */
    public void preparePowerQuality() {
        //Initialize powerQuality
        int NO_OF_PHASES = 3;
        powerQuality = new PowerQuality(NO_OF_PHASES);

        //Check if fftDataForHarmonics is null
        if (this.fftDataForHarmonics == null) {
            return;
        }
        //Check if fftDataForHarmonics[0] is null
        if (this.fftDataForHarmonics[0] == null) {
            return;
        }
        //Check if fftDataForHarmonics[0].length is within the limits
        if (this.fftDataForHarmonics[0].length < 1) {
            return;
        }

        //Preparing data to call setVoltageRMS, setCurrentRMS, setCosPhi of powerQuality
        double[][] voltageRMS = new double[NO_OF_PHASES][];
        double[][] currentRMS = new double[NO_OF_PHASES][];
        double[][] cosPhi = new double[NO_OF_PHASES][];
        for (int i = 0; i < NO_OF_PHASES; i++) {
            voltageRMS[i] = new double[fftDataForHarmonics[0].length];
            currentRMS[i] = new double[fftDataForHarmonics[0].length];
            cosPhi[i] = new double[fftDataForHarmonics[0].length];
        }
        //Iterate through fftDataForHarmonics
        for (int i = 0; i < NO_OF_PHASES; i++) {
            //Iterate through fftDataForHarmonics[i]
            for (int j = 0; j < fftDataForHarmonics[i].length; j++) {
                //Calculate voltageRMS
                voltageRMS[i][j] = fftDataForHarmonics[i][j].abs()/Math.sqrt(2);
                //Calculate currentRMS
                currentRMS[i][j] = fftDataForHarmonics[NO_OF_PHASES+i][j].abs()/Math.sqrt(2);
                //Calculate cosPhi
                cosPhi[i][j] = Math.cos(fftDataForHarmonics[i][j].getArgument() - fftDataForHarmonics[NO_OF_PHASES+i][j].getArgument());
            }
            powerQuality.setVoltageRMS(i, voltageRMS[i]);
            powerQuality.setCurrentRMS(i, currentRMS[i]);
            powerQuality.setPowerFactor(i, cosPhi[i]);
        }
        //Calculate power quality parameters
        powerQuality.calculatePowerQualityParameters();
    }


    /**
     * This function returns the power quality
     * @return PowerQuality
     *       The power quality
     */
    public PowerQuality getPowerQuality() {
        return powerQuality;
    }

    /**
     * This function returns the phase sequence
     * The value is calculated from the phase angle of the fundamental frequency
     * It updates PHASE_SEQUENCE_CLOCKWISE variable
     */
    public void calculatePhaseSequence() {
        //Check if fftDataForHarmonics is null
        if (this.fftDataForHarmonics == null) {
            return;
        }
        //Check if fftDataForHarmonics[0] is null
        if (this.fftDataForHarmonics[0] == null) {
            return;
        }
        //Check if fftDataForHarmonics[0].length is within the limits
        if (this.fftDataForHarmonics[0].length < 1) {
            return;
        }

        //Angle of fundamental frequency phase 2 relative to phase 1
        double phaseAngle2 = getFundamentalValue(1).getArgument();

        //Angle of fundamental frequency phase 3 relative to phase 1
        double phaseAngle3 = getFundamentalValue(2).getArgument();

        if (phaseAngle2 < 0) {
            phaseAngle2 += 2*Math.PI;
        }
        if (phaseAngle3 < 0) {
            phaseAngle3 += 2*Math.PI;
        }

        if (phaseAngle2 > 2*Math.PI) {
            phaseAngle2 -= 2*Math.PI;
        }
        if (phaseAngle3 > 2*Math.PI) {
            phaseAngle3 -= 2*Math.PI;
        }

        //Check if phaseAngle2 is greater than phaseAngle3
        if (phaseAngle2 > phaseAngle3) {
            //Update PHASE_SEQUENCE_CLOCKWISE
            PHASE_SEQUENCE_CLOCKWISE = true;
        } else {
            //Update PHASE_SEQUENCE_CLOCKWISE
            PHASE_SEQUENCE_CLOCKWISE = false;
        }
    }

    /**
     * This function returns the phase sequence
     * @return boolean
     *       The phase sequence
     */
    public boolean getPhaseSequenceClockwise() {
        return PHASE_SEQUENCE_CLOCKWISE;
    }

    /**
     * This function calculates the crest factor
     */
    private void calculateCrestFactor() {
        double[] maxValue = resampledData.getPeakValue();
        double[] rmsValue = resampledData.getRmsValue();
        crestFactor = new double[maxValue.length];

        for (int i=0; i<maxValue.length; i++) {
            crestFactor[i] = maxValue[i] / rmsValue[i];
            //Log.d("SKGadi", "Crest factor for channel " + i + ": " + crestFactor[i]);
        }
    }

    /**
     * This function returns the crest factor
     * @return double[]
     *       The crest factor
     */
    public double[] getCrestFactor() {
        return crestFactor;
    }

    /**
     * This function returns crest factor for the channel
     */
    public double getCrestFactor(int channel) {
        if (crestFactor == null) {
            return 0;
        }
        if (channel < 0 || channel >= crestFactor.length) {
            return 0;
        }
        return crestFactor[channel];
    }

}
