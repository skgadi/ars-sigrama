#ifndef SIG_FFT_H
#define SIG_FFT_H

#include "main.h"
#include "sig_libs/ESP_fft.h"



class SIG_FFT {
  float powerFrequency;
  /*union fullSpectrum{
    float SAMPLE[TOTAL_RESAMPLES];
    union amplitudeSpectrum{
      float SPECTRUM_AMPLITUDE[TOTAL_RESAMPLES/2];
      float SPECTRUM_CHANNELS_AMPLITUDE[RESAMPLE_SIZE/2][NO_OF_CHANNELS];
    } AmplitudeSpectrum;
    union phaseSpectrum{
      float SPECTRUM_PHASE[TOTAL_RESAMPLES/2];
      float SPECTRUM_CHANNELS_PHASE[RESAMPLE_SIZE/2][NO_OF_CHANNELS];
    } PhaseSpectrum;
  } FullSpectrum;*/
  float SPECTRUM_CHANNELS_AMPLITUDE[RESAMPLE_SIZE/2][NO_OF_CHANNELS];
  float SPECTRUM_CHANNELS_PHASE[RESAMPLE_SIZE/2][NO_OF_CHANNELS];
  bool errorInFFT = true;
  public:
    void printAmplitudeData();
    void printPhaseData();
    void prepare();
    float * getSamplePointer();
    float getSpecturmAmplitude(int index, int channel);
    float getSpecturmPhase(int index, int channel);
    void sendRawData();
};

extern SIG_FFT fft;

#endif // SIG_FFT_H
