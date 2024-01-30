#include "sig_calibrate.h"


SIG_CALIBRATE::SIG_CALIBRATE() {
  getChannelGainsAndOffsets();
  if (channelGain[0] == 0.0f) {
    for (uint8_t channel = 0; channel < NO_OF_CHANNELS; channel++) {
      channelGain[channel] = 1.0f;
      channelOffset[channel] = 0.0f;
    }
    putChannelGainsAndOffsets();
  }
}


void SIG_CALIBRATE::getChannelGainsAndOffsets() {
  for (uint8_t channel = 0; channel < NO_OF_CHANNELS; channel++) {
    channelGain[channel] = EEPROM.readFloat(EEPROM_CHANNEL_GAIN_ADDRESS + channel * sizeof(float));
    channelOffset[channel] = EEPROM.readFloat(EEPROM_CHANNEL_OFFSET_ADDRESS + channel * sizeof(float));
  }
}

void SIG_CALIBRATE::putChannelGainsAndOffsets() {
  for (uint8_t channel = 0; channel < NO_OF_CHANNELS; channel++) {
    EEPROM.writeFloat(EEPROM_CHANNEL_GAIN_ADDRESS + channel * sizeof(float), channelGain[channel]);
    EEPROM.writeFloat(EEPROM_CHANNEL_OFFSET_ADDRESS + channel * sizeof(float), channelOffset[channel]);
  }
}

float SIG_CALIBRATE::applyVoltageCalibration(float inValue) {
  return (inValue - voltageOffset) * voltageGain;
}

float SIG_CALIBRATE::applyCurrentCalibration(float inValue) {
  return (inValue - currentOffset) * currentGain;
}

float SIG_CALIBRATE::applyChannelCalibration(float inValue, uint8_t channel) {
  return (inValue - channelOffset[channel]) * channelGain[channel];
}

float SIG_CALIBRATE::applyFullCalibration(float inValue, uint8_t channel) {
  if (channel < 4) {
    return applyChannelCalibration(applyVoltageCalibration(inValue), channel);
  } else {
    return applyChannelCalibration(applyCurrentCalibration(inValue), channel);
  }
}

float SIG_CALIBRATE::getVoltageOffset() {
  return voltageOffset;
}
