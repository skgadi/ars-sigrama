#ifndef SIG_RESAMPLE_H
#define SIG_RESAMPLE_H


#include "main.h"

class SIG_RESAMPLE {
  float powerFrequency;
  union {
    float SAMPLE[TOTAL_RESAMPLES];
    float CHANNELS[RESAMPLE_SIZE][NO_OF_CHANNELS];
  };
  public:
    void calculateFrequency();
    float getPowerFrequency();
};

extern SIG_RESAMPLE resample;

#endif // SIG_RESAMPLE_H
