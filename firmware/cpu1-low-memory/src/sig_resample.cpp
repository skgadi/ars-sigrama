#include "sig_resample.h"

void SIG_RESAMPLE::calculateFrequency() {
  float zeroCrossings = 0;
  float firstZeroCrossingTime = -1;
  float lastZeroCrossingTime = -1;

  for (uint16_t sample = 0; sample < TOTAL_RESAMPLES; sample++) {
    if (SAMPLE[sample] > 0.0f) {
      if (SAMPLE[sample + 1] <= 0.0f) {
        zeroCrossings++;
        if (firstZeroCrossingTime == -1) {
          firstZeroCrossingTime = sample;
        }
        lastZeroCrossingTime = sample;
      }
    } else {
      if (SAMPLE[sample + 1] >= 0.0f) {
        zeroCrossings++;
        if (firstZeroCrossingTime == -1) {
          firstZeroCrossingTime = sample;
        }
        lastZeroCrossingTime = sample;
      }
    }
  }

  float timePeriodInMs = (float)(lastZeroCrossingTime - firstZeroCrossingTime)/1000.0f;
  if (zeroCrossings > 1) {
    powerFrequency = 0.5f/(timePeriodInMs/(zeroCrossings - 1));
  } else {
    powerFrequency = 0;
  }
}

float SIG_RESAMPLE::getPowerFrequency() {
  return powerFrequency;
}