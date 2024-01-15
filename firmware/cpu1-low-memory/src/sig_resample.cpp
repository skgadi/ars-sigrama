#include "sig_resample.h"

void SIG_RESAMPLE::prepare() {

  errorInResample = true;

  // Check if the fundamental frequency is correctly calculated (Any mistake in the
  // sample data will result in a wrong fundamental frequency, which is -1)
  if (sample.getFrequency() == -1) {
    return;
  }
  

  stepTime = 1000.0f/(sample.getFrequency()*RESAMPLE_SIZE);

  //Check that no extrapolation is required for resampling
  if (stepTime < sample.getStepTime()) {
    return;
  }
  
  //Check that no extrapolation is required for resampling both ends of the sample
  //First and last 5 samples are not resampled (to avoid extrapolation errors)
  if (sample.getStepTime()*SAMPLE_SIZE < stepTime*(RESAMPLE_SIZE + 10)) {
    return;
  }
  
  // Resample the data
  // If the hardware doesn't syncronized the sample
  // with the resample, then time offset will be introduced
  // in the resampled data
  // Time differnce between the channel to channel is 1/NO_OF_CHANNELS of the sample time
  float timeOffset = 0;
  if (!ALL_SIGNALS_SYNC_FROM_HARDWARE) {
    timeOffset = sample.getStepTime()/NO_OF_CHANNELS;
  }
  for (int i = 0; i < RESAMPLE_SIZE; i++) {
    float resampleTime = (i + 5)*stepTime;
    int sampleIndex_0 = (int)(resampleTime/sample.getStepTime());
    for (int j = 0; j < NO_OF_CHANNELS; j++) {
      float sampleTime = sampleIndex_0*sample.getStepTime() + j*timeOffset;
      int sampleIndex_1;
      if (sampleTime < resampleTime) {
        sampleIndex_1 = sampleIndex_0 + 1;
      } else {
        sampleIndex_1 = sampleIndex_0;
        sampleIndex_0 = sampleIndex_0 - 1;
      }
      sampleTime = sampleIndex_0*sample.getStepTime() + j*timeOffset;
      float sampleValue_0 = sample.get(sampleIndex_0, j);
      float sampleValue_1 = sample.get(sampleIndex_1, j);
      float resampleValue = sampleValue_0 + (sampleValue_1 - sampleValue_0)*(resampleTime - sampleTime)/(sample.getStepTime() + timeOffset);
      CHANNELS[i][j] =  calibrate.applyFullCalibration(resampleValue, j);
    }
  }
  errorInResample = false;
}

void SIG_RESAMPLE::printData() {
  if (errorInResample) {
    Serial.println("Resample Error");
    return;
  }
  Serial.println("t, V1, V2, V3, V4, I1, I2, I3, I4");
  for (int i = 0; i < RESAMPLE_SIZE; i++) {
    Serial.print(i*stepTime);
    Serial.print(",");
    for (int j = 0; j < NO_OF_CHANNELS; j++) {
      Serial.print(CHANNELS[i][j]);
      Serial.print(",");
    }
    Serial.println();
  }
}

float SIG_RESAMPLE::getSamlingFrequency() {
  return 1000.0f/stepTime;
}

float SIG_RESAMPLE::getData(int index, int channel) {
  return CHANNELS[index][channel];
}

bool SIG_RESAMPLE::isErrorInResample() {
  return errorInResample;
}