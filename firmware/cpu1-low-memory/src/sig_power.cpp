#include "sig_power.h"

void SIG_POWER::setup() {
  for (int i = 0; i < NO_OF_CHANNELS/2; i++) {
    energyActive[i] = 0.0;
    energyReactive[i] = 0.0;
    energyApparent[i] = 0.0;
  }
  factorForEnergyCalculation = stepTime/60.0f/60.0f;
}

void SIG_POWER::loop() {
  for (int i = 0; i < NO_OF_CHANNELS/2; i++) {
    powerActive[i] = 0.0;
    powerReactive[i] = 0.0;
    powerApparent[i] = 0.0;
    float multiplicationFactorForPowerCalculations = resample.getStepTime() * sample.getFrequency()*1e-6;
    for (int j = 0; j < RESAMPLE_SIZE; j++) {
      float instantPower = resample.getData(j, i)*resample.getData(j, i+NO_OF_CHANNELS/2)*multiplicationFactorForPowerCalculations;
      powerActive[i] += instantPower;
      powerApparent[i] += abs(instantPower);
    }
    powerReactive[i] = sqrt(abs(powerApparent[i]*powerApparent[i] - powerActive[i]*powerActive[i]));
    energyActive[i] += powerActive[i]*factorForEnergyCalculation;
    energyReactive[i] += powerReactive[i]*factorForEnergyCalculation;
    energyApparent[i] += powerApparent[i]*factorForEnergyCalculation;
  }
}

float* SIG_POWER::getEnergyActivePointer() {
  return energyActive;
}

float* SIG_POWER::getEnergyReactivePointer() {
  return energyReactive;
}

float* SIG_POWER::getEnergyApparentPointer() {
  return energyApparent;
}

float* SIG_POWER::getPowerActivePointer() {
  return powerActive;
}

float* SIG_POWER::getPowerReactivePointer() {
  return powerReactive;
}

float* SIG_POWER::getPowerApparentPointer() {
  return powerApparent;
}