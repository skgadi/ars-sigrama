#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_display.cpp"
#include "sgi_include_all.h"

SGI_DISPLAY::SGI_DISPLAY() {
  ledcSetup(channel, intensityFrequency, 8);
  ledcAttachPin(pin, channel);
  

  //show startup sequence on LED
  signalDeviceOn();
}

/*
Type:
  0 = intensity
  1 = blinkNormal
  2 = blinkFast

*/
/*
void SGI_DISPLAY::setDisplayAs(int newType, int newDutyCycle){
  if (currentType == newType) {
    if (currentDutyCycle != newDutyCycle) {
      currentDutyCycle = newDutyCycle;
      ledcWrite(channel, currentDutyCycle);
    }
  } else {
    currentType = newType;
    currentDutyCycle = newDutyCycle;
    if (currentType == 0) {
      ledcSetup(channel, intensityFrequency, 8);
      ledcWrite(channel, currentDutyCycle);
    } else if (currentType == 1) {
      ledcSetup(channel, blinkNormalFrequency, 8);
      ledcWrite(channel, currentDutyCycle);
    } else if (currentType == 2) {
      ledcSetup(channel, blinkFastFrequency, 8);
      ledcWrite(channel, currentDutyCycle);
    }
 }
}*/

/*
brightness: 0-255 indicating the new intensity level
*/
void SGI_DISPLAY::setBrightness(int brightness) {
  ledcWrite(channel, brightness);
}

void SGI_DISPLAY::putBlinks(int num, int waitTime) {
  //moveLevelSlow(currentDutyCycle, 0);
  for (int i=0; i<num; i++) {
    setBrightness(0);
    delay(waitTime);
    setBrightness(255);
    delay(waitTime);
  }
  setBrightness(currentDutyCycle);
  //moveLevelSlow(2255, currentDutyCycle);
}

/*
startLevel: 0-255 indicating the starting intensity level
endLevel: 0-255 indicating the ending intensity level
waitTime: the time to wait between each step in the transition
*/

void SGI_DISPLAY::moveLevelSlow(int startLevel, int endLevel, int waitTime) {
  if (startLevel>endLevel) {
    for (int i=startLevel; i>=endLevel; i--) {
      ledcWrite(channel, i);
      delay(waitTime);
    }
  } else {
    for (int i=startLevel; i<=endLevel; i++) {
      ledcWrite(channel, i);
      delay(waitTime);
    }
  }
}

/*
startLevel: 0-255 indicating the starting intensity level
endLevel: 0-255 indicating the ending intensity level
uses a default waitTime of defaultDelayTime

*/

void SGI_DISPLAY::moveLevelSlow(int startLevel, int endLevel) {
  moveLevelSlow(startLevel, endLevel, defaultDelayTime);
}

/*
newLevel: 0-255 indicating the new intensity level
*/

void SGI_DISPLAY::newLevelSlow(int newLevel) {
  newLevelSlow(newLevel, defaultDelayTime);
}

/*
newLevel: 0-255 indicating the new intensity level
waitTime: the time to wait between each step in the transition
*/

void SGI_DISPLAY::newLevelSlow(int newLevel, int waitTime) {
  moveLevelSlow(currentDutyCycle, newLevel, waitTime);
  currentDutyCycle = newLevel;
}


void SGI_DISPLAY::signalConnected() {
  while (isBusy){}
  isBusy = true;
  putBlinks(1, 50);
  newLevelSlow(255);
  isBusy = false;
}

void SGI_DISPLAY::signalDisconnected() {
  while (isBusy){}
  isBusy = true;
  putBlinks(2, 50);
  newLevelSlow(50);
  isBusy = false;
}

void SGI_DISPLAY::signalShutdown() {
  while (isBusy){}
  isBusy = true;
  newLevelSlow(255);
  newLevelSlow(0);
}

void SGI_DISPLAY::signalBatteryLowShutdown() {
  while (isBusy){}
  isBusy = true;
  putBlinks(3, 250);
  newLevelSlow(0);
}

void SGI_DISPLAY::signalTemperatureHighShutdown() {
  while (isBusy){}
  isBusy = true;
  putBlinks(4, 250);
  newLevelSlow(0);
}

void SGI_DISPLAY::signalHumidityHighShutdown() {
  while (isBusy){}
  isBusy = true;
  putBlinks(5, 250);
  newLevelSlow(0);
}

void SGI_DISPLAY::signalSentPacket() {
  while (isBusy){}
  isBusy = true;
  putBlinks(1, 50);
  isBusy = false;
}

void SGI_DISPLAY::signalDeviceOn() {
  while (isBusy){}
  isBusy = true;
  currentDutyCycle = 0;
  putBlinks(1, 50);
  newLevelSlow(255);
  newLevelSlow(50);
  isBusy = false;
}