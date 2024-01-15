#include "sig_fft.h"

void SIG_FFT::prepare() {
  errorInFFT = true;
  if (resample.isErrorInResample()) {
    return;
  }

  float tempOutput[RESAMPLE_SIZE];


  int samplingFrequency = (int) round(resample.getSamlingFrequency());

  for (int i = 0; i < NO_OF_CHANNELS; i++) {
    float tempInput[RESAMPLE_SIZE];
    for (int j = 0; j < RESAMPLE_SIZE; j++) {
      tempInput[j] = resample.getData(j, i);
    }

    ESP_fft FFT (RESAMPLE_SIZE, samplingFrequency, FFT_REAL, FFT_FORWARD,
                    tempInput, tempOutput);
    FFT.execute();
    for (int j = 0; j < RESAMPLE_SIZE; j++) {
      SPECTRUM_CHANNELS[j][i] = tempOutput[j];
    }
  }
  errorInFFT = false;
}

void SIG_FFT::printData() {
  if (errorInFFT) {
    Serial.println("FFT Error");
    return;
  }
  Serial.println("f, V1, V2, V3, V4, I1, I2, I3, I4");
  for (int i = 0; i < RESAMPLE_SIZE; i++) {
    Serial.print(i*resample.getSamlingFrequency()/RESAMPLE_SIZE);
    Serial.print(",");
    for (int j = 0; j < NO_OF_CHANNELS; j++) {
      Serial.print(SPECTRUM_CHANNELS[i][j]);
      Serial.print(",");
    }
    Serial.println();
  }
}