#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_sensor.cpp"
#include "sgi_include_all.h"


SGI_SENSOR::SGI_SENSOR() {
  setup();
  loop();
}

void SGI_SENSOR::setup() {
  sht = new SHTSensor;
  sht3xAnalog = new SHT3xAnalogSensor(PIN_SDA, PIN_SCL);
  //Wire.begin(PIN_SDA, PIN_SCL);
  if (sht->init()) {
    PRINTLN ("Sensor intiated.");
  } else {
    PRINTLN ("Unable to start sensor.");
  }
  sht->setAccuracy(SHTSensor::SHT_ACCURACY_HIGH); // only supported by SHT3x
}

FLOAT_VAL SGI_SENSOR::getTemperature() {
  FLOAT_VAL out;
  out.Val = sht->getTemperature();
  return out;
}

FLOAT_VAL SGI_SENSOR::getHumidity() {
  FLOAT_VAL out;
  out.Val = sht->getHumidity();
  return out;
}

void SGI_SENSOR::loop() {
  float currentTime = millis()*1.0/1000.0;
  if (currentTime - timeOfLastUpdate > UPDATE_SENSOR_EVERY_x_SECONDS) {
    timeOfLastUpdate = currentTime;
    sht->readSample();

    if (getTemperature().Val>(SHUT_DOWN_TEMPERATURE_AHOVE*1.0)) {
      //Shutting down because of high temperature
      PRINTLN("High temperature");
      ((SGI_AUTO_POWER*)SGI_GV_Apw)->shutdownNow(2);
    }
    if (getHumidity().Val>(SHUT_DOWN_HUMIDITY_ABOVE*1.0)) {
      //Shutting down because of high humidity
      PRINTLN("High humidity");
      ((SGI_AUTO_POWER*)SGI_GV_Apw)->shutdownNow(3);
    }
  }
}
