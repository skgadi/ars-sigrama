#ifndef SIG_POWER_H
#define SIG_POWER_H


#include "main.h"

class SIG_POWER {
  float energyActive[NO_OF_CHANNELS/2];
  float energyReactive[NO_OF_CHANNELS/2];
  float energyApparent[NO_OF_CHANNELS/2];
  float stepTime = ((SAMPLING_TIME_FOR_ENERGY_CALCULATION*1.0)/1000.0f);
  public:
    void setup();
    void loop();
    float* getEnergyActivePointer();
    float* getEnergyReactivePointer();
    float* getEnergyApparentPointer();
};

extern SIG_POWER power;

#endif // SIG_POWER_H
