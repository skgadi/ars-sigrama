#ifndef SIG_RESAMPLE_H
#define SIG_RESAMPLE_H


#include "main.h"

class SIG_RESAMPLE {
  float powerFrequency;
  /*union {
    float SAMPLE[TOTAL_RESAMPLES];
    float CHANNELS[RESAMPLE_SIZE][NO_OF_CHANNELS];
  };*/
  float CHANNELS[RESAMPLE_SIZE][NO_OF_CHANNELS];
  float stepTime;
  bool errorInResample = true;
  public:
    void printData();
    void sendRawData();
    void prepare();
    float getSamlingFrequency();
    float getData(int index, int channel);
    bool isErrorInResample();
    float * getSamplePointer();
    float getStepTime();
};

extern SIG_RESAMPLE resample;

#endif // SIG_RESAMPLE_H
