#include "sig_stream.h"

void SIG_STREAM::loop() {
  if (isStreaming) {
    return;
  }
  readyToStream = false;
  powerFrequency = sample.getFrequency();
  stepTime = resample.getStepTime();
  memcpy(resampleData, resample.getSamplePointer(), sizeof(resampleData));
  memcpy(spectrumAmplitudeData, fft.getSpectrumAmplitudePointer(), sizeof(spectrumAmplitudeData));
  memcpy(spectrumPhaseData, fft.getSpectrumPhasePointer(), sizeof(spectrumPhaseData));
  memcpy(energyActive, power.getEnergyActivePointer(), sizeof(energyActive));
  memcpy(energyReactive, power.getEnergyReactivePointer(), sizeof(energyReactive));
  memcpy(energyApparent, power.getEnergyApparentPointer(), sizeof(energyApparent));
  memcpy(powerActive, power.getPowerActivePointer(), sizeof(powerActive));
  memcpy(powerReactive, power.getPowerReactivePointer(), sizeof(powerReactive));
  memcpy(powerApparent, power.getPowerApparentPointer(), sizeof(powerApparent));
  readyToStream = true;
}

void SIG_STREAM::stream() {
  while (!readyToStream) {
    delay(1);
  }
  isStreaming = true;
  Serial.write((uint8_t *)&powerFrequency, sizeof(powerFrequency));
  Serial.write((uint8_t *)&stepTime, sizeof(stepTime));
  Serial.write((uint8_t *)resampleData, sizeof(resampleData));
  Serial.write((uint8_t *)spectrumAmplitudeData, sizeof(spectrumAmplitudeData));
  Serial.write((uint8_t *)spectrumPhaseData, sizeof(spectrumPhaseData));
  Serial.write((uint8_t *)powerActive, sizeof(powerActive));
  Serial.write((uint8_t *)powerReactive, sizeof(powerReactive));
  Serial.write((uint8_t *)powerApparent, sizeof(powerApparent));
  Serial.write((uint8_t *)energyActive, sizeof(energyActive));
  Serial.write((uint8_t *)energyReactive, sizeof(energyReactive));
  Serial.write((uint8_t *)energyApparent, sizeof(energyApparent));
  isStreaming = false;
}