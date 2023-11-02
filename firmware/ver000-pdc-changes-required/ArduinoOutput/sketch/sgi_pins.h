#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_pins.h"
#ifndef SGI_PINS_H
#define SGI_PINS_H

#include "sgi_global.h"


#define PIN_AUTO_POWER 18

#define PIN_ENABLE_POWER 33
#define PIN_ENABLE_AUX_POWER 27
#define PIN_SELECT_L1 15
#define PIN_SELECT_L2 2
#define PIN_RLY_SIGNAL_ED 32

#define PIN_SDA 13
#define PIN_SCL 14

#define PIN_AUX 25
#define PIN_CABLE_ACK 26


#define PIN_ADC_MOSI 23
#define PIN_ADC_MISO 22
#define PIN_ADC_SCK 21
#define PIN_ADC_CS 19


#define PIN_BTN_LED 4


class SGI_PINS {
  void stage0();
  public:
    SGI_PINS();
    void stage1();
};

#endif