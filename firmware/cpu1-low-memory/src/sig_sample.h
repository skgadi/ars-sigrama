#ifndef SIG_SAMPLE_H
#define SIG_SAMPLE_H

#include "main.h"

class SIG_SAMPLE {
  /*union {
    uint16_t SAMPLE[TOTAL_SAMPLES];
    uint16_t CHANNELS[SAMPLE_SIZE][NO_OF_CHANNELS];
  };*/
  uint16_t CHANNELS[SAMPLE_SIZE][NO_OF_CHANNELS];
  int firstZeroCrossingOfMainChannel;
  float stepTime;
  float powerFrequency;
  void calculateFrequency();
  public:
    void print();
    void printSingal();
    void printFrequency();
    void prepare();
    uint16_t get(int, int);
    void set(int, int, uint16_t);
    float getStepTime();
    float getFrequency();
    void sendRawData();
    int getFirstZeroCrossingOfMainChannel();
};

extern SIG_SAMPLE sample;

#endif // SIG_SAMPLE_H
