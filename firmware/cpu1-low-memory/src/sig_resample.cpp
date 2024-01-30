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
  //first getFirstZeroCrossingOfMainChannel samples are not resampled (to avoid extrapolation 
  // errors and to zero cross the main channel at the begining)
  //Last 5 samples are not resampled (to avoid extrapolation errors)
  int avoidFirstSamples = sample.getFirstZeroCrossingOfMainChannel();
  //Serial.print("avoidFirstSamples from resample: ");
  //Serial.println(avoidFirstSamples);
  if (sample.getStepTime()*SAMPLE_SIZE < stepTime*(RESAMPLE_SIZE + avoidFirstSamples + 5)) {
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

  // translate the avoidFirstSamples to resample index
  avoidFirstSamples = (int)(avoidFirstSamples*sample.getStepTime()/stepTime)+1;
  for (int i = 0; i < RESAMPLE_SIZE; i++) {
    float resampleTime = (i + avoidFirstSamples)*stepTime;
    int sampleIndexParent = (int)(resampleTime/sample.getStepTime());
    for (int j = 0; j < NO_OF_CHANNELS; j++) {
      int sampleIndex_0 = sampleIndexParent;
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

void SIG_RESAMPLE::sendRawData() {
  if (errorInResample) {
    Serial.write(0x62);
    return;
  }
  //send stepTime float as 4 bytes
  Serial.write((uint8_t *)&stepTime, 4);
  //send the SAMPLE as bytes of float
  Serial.write((uint8_t *)CHANNELS, TOTAL_RESAMPLES*4);
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

float * SIG_RESAMPLE::getSamplePointer() {
  return (float *) CHANNELS;
}