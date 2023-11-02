#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_eeprom.h"
#ifndef SGI_EEPROM_H
#define SGI_EEPROM_H

#include "sgi_global.h"

#include <EEPROM.h>


struct SGI_ON_BOARD_STORAGE {
  FLOAT_VAL gains[4];
  FLOAT_VAL offsets[4];
  QWORD_VAL timeStamp; //Unix timestamp
};


class SGI_EEPROM {
  void setup();
  SGI_ON_BOARD_STORAGE data;
  public:
    SGI_EEPROM();
    void updateData(SGI_ON_BOARD_STORAGE);
    SGI_ON_BOARD_STORAGE getData();
};

#endif