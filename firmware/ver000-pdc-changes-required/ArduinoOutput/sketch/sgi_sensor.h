#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_sensor.h"
#ifndef SGI_SENSOR_H
#define SGI_SENSOR_H

#include "sgi_global.h"
#include "SHTSensor.h"


class SGI_SENSOR {
  void setup();
  SHTSensor *sht;
  SHT3xAnalogSensor *sht3xAnalog;
  float timeOfLastUpdate=0;
  public:
    SGI_SENSOR();
    FLOAT_VAL getTemperature();
    FLOAT_VAL getHumidity();
    void loop();
};



#endif