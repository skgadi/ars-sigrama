#ifndef SIG_CALIBRATE_H
#define SIG_CALIBRATE_H


#include "main.h"

#include <EEPROM.h>

// Your code here
class SIG_CALIBRATE {
  float voltageGain = 11.0f/37.31f; //3.3f/4095f*2f/820.0f*150000f;
  float currentGain =  1.0f/6.8250f;//3.3f/4095f*2f/330.0f*150/5*1000 <-- Measures in Ma;
  float voltageOffset = 2047;
  float currentOffset = 2047;
  float channelGain[NO_OF_CHANNELS];
  float channelOffset[NO_OF_CHANNELS];

  void getChannelGainsAndOffsets();
  float applyVoltageCalibration(float);
  float applyCurrentCalibration(float);
  float applyChannelCalibration(float, uint8_t);
  public:
    SIG_CALIBRATE();
    void putChannelGainsAndOffsets();
    float applyFullCalibration(float, uint8_t);
    float getVoltageOffset();
};

extern SIG_CALIBRATE calibrate;

#endif // SIG_CALIBRATE_H
