#include "sig_fft.h"

void SIG_FFT::prepare() {
  errorInFFT = true;
  if (resample.isErrorInResample()) {
    return;
  }

  float tempOutput[RESAMPLE_SIZE];


  int samplingFrequency = (int) round(resample.getSamlingFrequency());

  float phaseOffset = 0; //Phase offset that is applied to make sure the phase of the fundamental frequency of first channel is 0
  for (int i = 0; i < NO_OF_CHANNELS; i++) {
    float tempInput[RESAMPLE_SIZE];
    for (int j = 0; j < RESAMPLE_SIZE; j++) {
      tempInput[j] = resample.getData(j, i);
    }

    ESP_fft FFT (RESAMPLE_SIZE, samplingFrequency, FFT_REAL, FFT_FORWARD,
                    tempInput, tempOutput);
    FFT.execute();
    if (i == 0) {
      phaseOffset = atan2(tempOutput[3], tempOutput[2]);
    }
    for (int j = 0; j < RESAMPLE_SIZE/2; j++) {
      SPECTRUM_CHANNELS_AMPLITUDE[j][i] = sqrt(pow(tempOutput[2*j], 2) + pow(tempOutput[2*j + 1], 2))*FFT_AMPLITUDE_FACTOR;
      //Serial.println(SPECTRUM_CHANNELS_AMPLITUDE[j][i]);
      //Make sure the phase of the fundamental frequency of first channel is 0
      SPECTRUM_CHANNELS_PHASE[j][i] = atan2(tempOutput[2*j + 1], tempOutput[2*j]) - phaseOffset;
    }
  }
  errorInFFT = false;
}

void SIG_FFT::printAmplitudeData() {
  if (errorInFFT) {
    Serial.println("FFT Error");
    return;
  }
  Serial.println("f, V1, V2, V3, V4, I1, I2, I3, I4");
  for (int i = 0; i < RESAMPLE_SIZE/2; i++) {
    Serial.print(i*resample.getSamlingFrequency()/RESAMPLE_SIZE);
    Serial.print(",");
    for (int j = 0; j < NO_OF_CHANNELS; j++) {
      Serial.print(SPECTRUM_CHANNELS_AMPLITUDE[i][j]);
      Serial.print(",");
    }
    Serial.println();
  }
}

void SIG_FFT::printPhaseData() {
  if (errorInFFT) {
    Serial.println("FFT Error");
    return;
  }
  Serial.println("f, V1, V2, V3, V4, I1, I2, I3, I4");
  for (int i = 0; i < RESAMPLE_SIZE/2; i++) {
    Serial.print(i*resample.getSamlingFrequency()/RESAMPLE_SIZE);
    Serial.print(",");
    for (int j = 0; j < NO_OF_CHANNELS; j++) {
      Serial.print(SPECTRUM_CHANNELS_PHASE[i][j]);
      Serial.print(",");
    }
    Serial.println();
  }
}

void SIG_FFT::sendRawData() {
  if (errorInFFT) {
    Serial.write(0x62);
    return;
  }
  //send the SAMPLE as bytes of float
  Serial.write((uint8_t *)SPECTRUM_CHANNELS_AMPLITUDE, TOTAL_RESAMPLES*2);
  Serial.write((uint8_t *)SPECTRUM_CHANNELS_PHASE, TOTAL_RESAMPLES*2);
}

float * SIG_FFT::getSamplePointer() {
  return (float *) SPECTRUM_CHANNELS_PHASE;
}

float SIG_FFT::getSpecturmAmplitude(int index, int channel) {
  return SPECTRUM_CHANNELS_AMPLITUDE[index][channel];
}

float SIG_FFT::getSpecturmPhase(int index, int channel) {
  return SPECTRUM_CHANNELS_PHASE[index][channel];
}