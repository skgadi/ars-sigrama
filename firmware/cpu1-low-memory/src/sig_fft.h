#ifndef SIG_FFT_H
#define SIG_FFT_H

#include "main.h"
#include "sig_libs/ESP_fft.h"

class SIG_FFT {
  float powerFrequency;
  union {
    float SPECTRUM[TOTAL_RESAMPLES];
    float SPECTRUM_CHANNELS[RESAMPLE_SIZE][NO_OF_CHANNELS];
  };
  bool errorInFFT = true;
  public:
    void printData();
    void prepare();
};

extern SIG_FFT fft;

#endif // SIG_FFT_H
