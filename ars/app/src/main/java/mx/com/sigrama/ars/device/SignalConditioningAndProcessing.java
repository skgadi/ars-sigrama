package mx.com.sigrama.ars.device;

import static java.lang.Double.NaN;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import mx.com.sigrama.ars.MainActivity;

public class SignalConditioningAndProcessing {

    //The following changes as per the firmware update. Now are we preparing as per the old firmware
    private int READING_SIZE = 13;
    public int SAMPLE_SIZE=2500;


    private MainActivity mainActivity;
    private MutableLiveData<PhasorData> phasorData = new MutableLiveData<>();
    private MutableLiveData<HarmonicsData> harmonicsData = new MutableLiveData<>();
    private MutableLiveData<OscilloscopeData> oscilloscopeData = new MutableLiveData<>();

    class BATTERY_STATE {
        int percentage;
        boolean isCharging;
    }
    private BATTERY_STATE batteryState;
    private class Datapoint {
        double t; //t is time
        double y;
    }
    class CALIBRATION_DATA {
        double[] gains;
        double[] offsets;
        long time=0;
    }
    private CALIBRATION_DATA calibrationData;
    private double calibrationGainLowerLimit = 0.98f;
    private double calibrationGainUpperLimit = 1.02f;
    private double calibrationOffsetLimitForV = 5;
    private double calibrationOffsetLimitForA = 5;
    private Datapoint[][] voltages;
    private Datapoint[][] currents;
    private Datapoint[][] voltagesResampled;
    private Datapoint[][] currentsResampled;
    private double RESAMPLE_STEP_SIZE = 0.0001d;
    private double boardTemperature;
    private double boardHumidity;
    private MutableLiveData<Boolean> isDataProcessingSuccessful;

    public SignalConditioningAndProcessing (MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.harmonicsData.postValue(null);
        this.oscilloscopeData.postValue(null);
        this.phasorData.postValue(null);
        this.isDataProcessingSuccessful = new MutableLiveData<Boolean>();

        //Observer for received data
        Observer<byte[]> receivedDataObserver = new Observer<byte[]>() {
            @Override
            public void onChanged(byte[] bytes) {
                try {
                    Log.d("SKGadi", "Received data: " + bytes);
                    processSamplesToDataPoints(bytes);
                    calibrateData();
                    preparePhasorData();
                    prepareHarmonicsData();
                    prepareOscilloscopeData();
                    isDataProcessingSuccessful.postValue(true);
                } catch (Exception e) {
                    isDataProcessingSuccessful.postValue(false);
                    e.printStackTrace();
                }
            }
        };
        mainActivity.managingWebSocket.getReceivedData().observe(mainActivity, receivedDataObserver);
    }
    public MutableLiveData<PhasorData> getPhasorData() {
        return phasorData;
    }

    public MutableLiveData<HarmonicsData> getHarmonicsData() {
        return harmonicsData;
    }

    public MutableLiveData<OscilloscopeData> getOscilloscopeData() {
        return oscilloscopeData;
    }

    public MutableLiveData<Boolean> getIsDataProcessingSuccessful() {
        return isDataProcessingSuccessful;
    }

    private void processSamplesToDataPoints(byte[] data) {
        voltages = new Datapoint[SAMPLE_SIZE][3];
        currents = new Datapoint[SAMPLE_SIZE][3];

        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        int j=0;
        boolean tempCableConnectionSecured = true;
        for (int i = 0; i< SAMPLE_SIZE; i++) {
            j=0;
            //Reading voltage
            for (int k = 0; k < 3; k++) {
                voltages[i][k] = new Datapoint();
                bb.clear();
                bb.put(data[i * READING_SIZE + j++]);
                bb.put(data[i * READING_SIZE + j++]);
                voltages[i][k].y = (bb.getShort(0)) * 1.0f;
            }
            //Reading current This part should be updated with the new firmware
            bb.clear();
            bb.put(data[i * READING_SIZE + j++]);
            bb.put(data[i * READING_SIZE + j++]);
            double tempCurrent = (bb.getShort(0)) * 1.0f;
            for (int k = 0; k < 3; k++) {
                currents[i][k] = new Datapoint();
                currents[i][k].y = tempCurrent;
            }
            //Reading digital inputs Which may be ignored in the new firmware
            byte tempDigitalInputs = data[i * READING_SIZE + j++];

            //Reading time. This part should be updated with the new firmware
            bb.clear();
            bb.put(data[i * READING_SIZE + j++]);
            bb.put(data[i * READING_SIZE + j++]);
            bb.put(data[i * READING_SIZE + j++]);
            bb.put(data[i * READING_SIZE + j++]);
            double tempTime = (bb.getFloat(0));

            for (int k = 0; k < 3; k++) {
                voltages[i][k].t = tempTime;
                currents[i][k].t = tempTime;
            }
        }

        j=SAMPLE_SIZE * READING_SIZE;
        //Reading fault type which is not used in the new firmware
        j++;


        //Reading board temperature
        bb.clear();
        bb.put(data[j++]);
        bb.put(data[j++]);
        bb.put(data[j++]);
        bb.put(data[j++]);
        boardTemperature = (bb.getFloat(0));

        //Reading board humidity
        bb.clear();
        bb.put(data[j++]);
        bb.put(data[j++]);
        bb.put(data[j++]);
        bb.put(data[j++]);
        boardHumidity = (bb.getFloat(0));

        //Reading calibration data which will change in the new firmware

        calibrationData = new CALIBRATION_DATA();
        calibrationData.gains = new double[6];
        calibrationData.offsets = new double[6];


        for(int i=0; i<4; i++) {
            bb.clear();
            bb.put(data[j++]);
            bb.put(data[j++]);
            bb.put(data[j++]);
            bb.put(data[j++]);
            calibrationData.gains[i] = (bb.getFloat(0));
            if (Math.abs(calibrationData.gains[i])>calibrationGainUpperLimit
                    || Math.abs(calibrationData.gains[i])<calibrationGainLowerLimit ) {
                calibrationData.gains[i] = 1;
            }

            bb.clear();
            bb.put(data[j++]);
            bb.put(data[j++]);
            bb.put(data[j++]);
            bb.put(data[j++]);
            calibrationData.offsets[i] = (bb.getFloat(0));
            if (i<3) {
                if (Math.abs(calibrationData.offsets[i])>calibrationOffsetLimitForV) {
                    calibrationData.offsets[i] = 0;
                }
            } else {
                if (Math.abs(calibrationData.offsets[i])> calibrationOffsetLimitForA) {
                    calibrationData.offsets[i] = 0;
                }
            }
        }
        calibrationData.gains[4] = 1;
        calibrationData.gains[5] = 1;
        calibrationData.offsets[4] = 0;
        calibrationData.offsets[5] = 0;

        //Reading calibration time
        bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        for (int i=0; i<8; i++) {
            bb.put(data[j++]);
        }
        calibrationData.time = bb.getLong(0)*60*1000;



        // Battery info
        /*batteryState = new BATTERY_STATE();
        byte batteryStateRead = data[j++];
        batteryState.percentage = (byte)0x7f & batteryStateRead;
        batteryState.isCharging = (0x80  & batteryStateRead) != 0;
        Log.i("SKGadi", "batteryState.Percentage: " + batteryState.percentage + "%");
        Log.i("SKGadi", "batteryState.isCharging: " + batteryState.isCharging);*/
    }

    private void preparePhasorData() {
        phasorData.postValue(null);
    }
    private void prepareHarmonicsData() {
        harmonicsData.postValue(null);
    }
    private void prepareOscilloscopeData() {
        oscilloscopeData.postValue(null);
    }

    private void calibrateData() {
        for (int i=0; i<3; i++) {
            for (int j=0; j<SAMPLE_SIZE; j++) {
                //This calibration part is updated as per the new firmware
                /*voltages[j][i].y = voltages[j][i].y * calibrationData.gains[i] + calibrationData.offsets[i];
                currents[j][i].y = currents[j][i].y * calibrationData.gains[i+3] + calibrationData.offsets[i+3];*/
                voltages[j][i].y = (2f*3.3f*voltages[j][i].y/4095.0f-3.3f)*1.00f*150000f/820f;
                currents[j][i].y = currents[j][i].y*40f/273f-300f;
            }
        }
    }


    private void resampleData() {
        int resampleSize = (int) Math.floor((voltages[0][0].t-voltages[0][SAMPLE_SIZE-1].t)/RESAMPLE_STEP_SIZE);
        voltagesResampled = new Datapoint[resampleSize][3];
        currentsResampled = new Datapoint[resampleSize][3];
        for (int i=0; i<3; i++) {
            for (int j=0; j<resampleSize; j++) {
                voltagesResampled[j][i] = new Datapoint();
                currentsResampled[j][i] = new Datapoint();
                voltagesResampled[j][i].t = j * RESAMPLE_STEP_SIZE;
                currentsResampled[j][i].t = j * RESAMPLE_STEP_SIZE;
            }
        }
        //Resampling voltages and currents using spline cubic interpolation
        for (int i=0; i<3; i++) {
            for (int j=0; j<resampleSize; j++) {
                voltagesResampled[j][i].y = cubicSplineInterpolation(voltages, voltagesResampled[j][i].t, i);
                currentsResampled[j][i].y = cubicSplineInterpolation(currents, currentsResampled[j][i].t, i);
            }
        }
    }

    private double cubicSplineInterpolation(Datapoint[][] data, double x, int i) {
        int n = data.length;
        double[] h = new double[n - 1];
        double[] b = new double[n - 1];
        double[] u = new double[n - 1];
        double[] v = new double[n - 1];
        double[] z = new double[n];
        double[] c = new double[n];
        double[] d = new double[n];
        for (int j = 0; j < n - 1; j++) {
            h[j] = data[j][i].t - data[j + 1][i].t;
            b[j] = (data[j][i].y - data[j + 1][i].y) / h[j];
        }
        u[1] = 2 * (h[0] + h[1]);
        v[1] = 6 * (b[0] - b[1]);
        for (int j = 2; j < n - 1; j++) {
            u[j] = 2 * (h[j - 1] + h[j]) - h[j - 1] * h[j - 1] / u[j - 1];
            v[j] = 6 * (b[j - 1] - b[j]) - h[j - 1] * v[j - 1] / u[j - 1];
        }
        z[n - 1] = 0;
        for (int j = n - 2; j > 0; j--) {
            z[j] = (v[j] - h[j] * z[j + 1]) / u[j];
        }
        z[0] = 0;
        for (int j = 0; j < n - 1; j++) {
            c[j] = (data[j][i].y - data[j + 1][i].y) / h[j] - h[j] * (z[j + 1] + 2 * z[j]) / 6;
            d[j] = (z[j + 1] - z[j]) / (6 * h[j]);
        }
        int j = 0;
        while (data[j][i].t < x) {
            j++;
        }
        return data[j][i].y + c[j] * (x - data[j][i].t) + z[j] * (x - data[j][i].t) * (x - data[j][i].t) / 2 + d[j] * (x - data[j][i].t) * (x - data[j][i].t) * (x - data[j][i].t) / 6;
    }

}