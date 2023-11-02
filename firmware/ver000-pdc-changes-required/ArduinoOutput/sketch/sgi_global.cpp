#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_global.cpp"
#include "sgi_include_all.h"



void *SGI_GV_Pin;
void *SGI_GV_Ter;
void *SGI_GV_Ack;
void *SGI_GV_Adc;
void *SGI_GV_Wfi;
void *SGI_GV_Dsp;
void *SGI_GV_Apw;
void *SGI_GV_EFt;
void *SGI_GV_Eep;
void *SGI_GV_Sen;
void *SGI_GV_Bat;

void setup() {
  SGI_GV_Pin = new SGI_PINS;
  SGI_GV_Ter = new SGI_SERIAL (ENABLE_DEBUG);
  SGI_GV_Apw = new SGI_AUTO_POWER;
  SGI_GV_Dsp = new SGI_DISPLAY;
  SGI_GV_Bat = new SGI_BAT;
  ((SGI_PINS*)SGI_GV_Pin)->stage1();
  SGI_GV_Sen = new SGI_SENSOR;
  SGI_GV_Ack = new SGI_ACK;
  SGI_GV_Adc = new SGI_ADC(SAMPLE_SIZE);
  SGI_GV_Wfi = new SGI_WIFI;
  SGI_GV_EFt = new SGI_EMULATE_FAULT;
  SGI_GV_Eep = new SGI_EEPROM;
}


void loop() {
  ((SGI_SERIAL*)SGI_GV_Ter)->loop();
  ((SGI_WIFI*)SGI_GV_Wfi)->loop();
  //((SGI_AUTO_POWER*)SGI_GV_Apw)->loop();
  ((SGI_SENSOR*)SGI_GV_Sen)->loop();
  ((SGI_BAT*)SGI_GV_Bat)->loop();
  //PRINTLN("Hi");
  delay(10);
}
