#ifndef SIG_STREAM_H
#define SIG_STREAM_H

// Your code here

#include "main.h"

class SIG_STREAM {
  float resampleData[RESAMPLE_SIZE][NO_OF_CHANNELS];
  float spectrumAmplitudeData[RESAMPLE_SIZE/2][NO_OF_CHANNELS];
  float spectrumPhaseData[RESAMPLE_SIZE/2][NO_OF_CHANNELS];
  float energyActive[NO_OF_CHANNELS/2];
  float energyReactive[NO_OF_CHANNELS/2];
  float energyApparent[NO_OF_CHANNELS/2];
  float powerActive[NO_OF_CHANNELS/2];
  float powerReactive[NO_OF_CHANNELS/2];
  float powerApparent[NO_OF_CHANNELS/2];
  float stepTime;
  float powerFrequency;
  bool isStreaming = false;
  bool readyToStream = false;
  public:
    void loop();
    void stream();
};

extern SIG_STREAM sig_stream;

#endif // SIG_STREAM_H
