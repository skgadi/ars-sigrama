#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_display.h"
#ifndef SGI_DISPLAY_H
#define SGI_DISPLAY_H

#include "sgi_global.h"
#include "sgi_pins.h"

class SGI_DISPLAY {
  int intensityFrequency = 1000;
  int blinkNormalFrequency = 2;
  int blinkFastFrequency = 4;
  int currentType = 0;
  int currentDutyCycle = 0;
  int pin = PIN_BTN_LED;
  int channel = 0;
  void newLevelSlow(int);
  void newLevelSlow(int, int);
  void moveLevelSlow(int, int, int);
  void moveLevelSlow(int, int);
  void setBrightness(int);
  int defaultDelayTime = 2;
  bool isBusy = false;
  public:
  SGI_DISPLAY();
  void putBlinks(int,int);
  void signalConnected();
  void signalDisconnected();
  void signalBatteryLowShutdown();
  void signalTemperatureHighShutdown();
  void signalHumidityHighShutdown();
  void signalShutdown();
  void signalSentPacket();
  void signalDeviceOn();
};


#endif
