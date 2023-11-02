#include "sgi_include_all.h"

SGI_AUTO_POWER::SGI_AUTO_POWER() {
  setup();
}

void SGI_AUTO_POWER::setup() {
  pinMode(PIN_AUTO_POWER, INPUT_PULLUP);
  isStartPress = !digitalRead(PIN_AUTO_POWER);
  resetShutdownTime();
}

void SGI_AUTO_POWER::resetShutdownTime() {
  shutDownTime = getCurrentTime() + TIME_IN_SECONDS_AUTO_SHUTDOWN;
}

float SGI_AUTO_POWER::getCurrentTime() {
  return (millis()*1.0)/1000.0;
}

void SGI_AUTO_POWER::loop() {
  if (getCurrentTime()>=shutDownTime) {
    flagShutDownNow = true;
  }

  if (!digitalRead(PIN_AUTO_POWER)) {
    if (!isStartPress) {
      flagShutDownNow = true;
    }
  } else {
    isStartPress = false;
  }  
}

void SGI_AUTO_POWER::mainLoop() {
  if (flagShutDownNow) {
    shutdownNow();
  }
  
  /*
  if (!digitalRead(PIN_AUTO_POWER)) {
    if (!isStartPress) {
      if (lastPressTime<0) {
        lastPressTime = getCurrentTime();
      }
    }
  } else {
    lastPressTime = -1;
    isStartPress = false;
  }
  if (lastPressTime > 0 && (getCurrentTime() - lastPressTime) >= TIME_IN_SECONDS_PRESSED_TO_OFF) {
    shutdownNow();
  }*/

}

void SGI_AUTO_POWER::shutdownNow() {
  shutdownNow(0);
}

/*
 * reason:
 * 0 - normal time out
 * 1 - low battery
 * 2 - High temperature
 * 3 - High humidity
 * 
 */

void SGI_AUTO_POWER::shutdownNow(int reason) {
  PRINTLN("Shutting down...");
  WiFi.disconnect(true);
  WiFi.mode(WIFI_OFF);


  //Display reason
  switch (reason) {
    case 1:
      ((SGI_DISPLAY*)SGI_GV_Dsp)->signalBatteryLowShutdown();
      break;
    case 2:
      ((SGI_DISPLAY*)SGI_GV_Dsp)->signalTemperatureHighShutdown();
      break;
    case 3:
      ((SGI_DISPLAY*)SGI_GV_Dsp)->signalHumidityHighShutdown();
      break;
    default:
      ((SGI_DISPLAY*)SGI_GV_Dsp)->signalShutdown();
      break;
  }
  
  
  
  while(!digitalRead(PIN_AUTO_POWER)) {
  }
  delay(1000); // Time for debounce of power button
  pinMode(PIN_AUTO_POWER, INPUT_PULLDOWN);
  pinMode(PIN_AUTO_POWER, OUTPUT);
  delay(100);
  digitalWrite(PIN_AUTO_POWER, LOW);
  //delay(2000);
  for (int i=0; i<10; i++) {
    PRINT("Delay iteration: ");
    for (int j=0; j<60; j++) {
      delay(1000);
    }
    PRINTLN(i);
  }
  ESP.restart();
  //esp_deep_sleep_start();
}


void SGI_AUTO_POWER::shutdownWithDelay(float delayInS) {
  shutDownTime = getCurrentTime() + delayInS;
}
