#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_eeprom.cpp"
#include "sgi_include_all.h"

SGI_EEPROM::SGI_EEPROM () {
  EEPROM.begin(sizeof(SGI_ON_BOARD_STORAGE));
  setup();
}

void SGI_EEPROM::setup() {
  EEPROM.get(0, data);
}

SGI_ON_BOARD_STORAGE SGI_EEPROM::getData () {
  return data;
}

void SGI_EEPROM::updateData(SGI_ON_BOARD_STORAGE inData) {
  EEPROM.put(0, inData);
  EEPROM.commit();
  setup();
}