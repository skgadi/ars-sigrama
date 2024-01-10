#ifndef SGI_SAMPLE_H
#define SGI_SAMPLE_H

#include "main.h"


class SGI_SAMPLE {
private:
  float sample[NUMBER_OF_CHANNELS][SAMPLE_SIZE];

  int sampleSize = SAMPLE_SIZE;
  int numberOfChannels = NUMBER_OF_CHANNELS;
  int readingGapInUs; // Gap between two readings in microseconds it is because ADC is samples channels one by one
public:
  SGI_SAMPLE();
  void readAllSamples();
};


#endif // SGI_SAMPLE_H

