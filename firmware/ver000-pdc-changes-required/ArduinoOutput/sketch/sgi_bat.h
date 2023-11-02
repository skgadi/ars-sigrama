#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_bat.h"
#ifndef SGI_BAT_H //Obtains battery info
#define SGI_BAT_H


// inspired from https://github.com/sparkfun/SparkFun_MAX1704x_Fuel_Gauge_Arduino_Library/blob/main/examples/Example1_Simple/Example1_Simple.ino

#include "sgi_global.h"
#include <SparkFun_MAX1704x_Fuel_Gauge_Arduino_Library.h> // Click here to get the library: http://librarymanager/All#SparkFun_MAX1704x_Fuel_Gauge_Arduino_Library
#include <IP5306_I2C.h>
#include <Wire.h>

class SGI_BAT {
  void setup ();
  SFE_MAX1704X* battery;
  float soc;
  float voltage;
  float lastUpdate;
  float getSOC();
  float getVoltage();
  bool isCharging();
  char percentage;

  IP5306* ip5306;


  public:
    SGI_BAT();
    void loop();
    void updateBatteryStatus();
    char getBatteryStatus();
};



#endif // SGI_BAT_H
