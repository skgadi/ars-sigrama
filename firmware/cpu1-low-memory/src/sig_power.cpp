#include "sig_power.h"

void SIG_POWER::setup() {
  for (int i = 0; i < NO_OF_CHANNELS/2; i++) {
    energyActive[i] = 0.0;
    energyReactive[i] = 0.0;
    energyApparent[i] = 0.0;
  }
}

void SIG_POWER::loop() {
  for (int i = 0; i < NO_OF_CHANNELS/2; i++) {
    energyApparent[i] += fft.getSpecturmAmplitude(1, i)*fft.getSpecturmAmplitude(1, 3+i);
    energyActive[i] += energyApparent[i]*cos(fft.getSpecturmPhase(1, i)-fft.getSpecturmPhase(1, 3+i))*stepTime;
    energyReactive[i] += energyApparent[i]*sin(fft.getSpecturmPhase(1, i)-fft.getSpecturmPhase(1, 3+i))*stepTime;
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