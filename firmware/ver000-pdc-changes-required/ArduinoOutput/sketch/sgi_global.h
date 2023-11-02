#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_global.h"
#ifndef SGI_GLOBAL_H
#define SGI_GLOBAL_H

#include <Arduino.h>
#include "GenericTypeDefs.h"
#include "GenericTypeDefsPlus.h"
#include <Wire.h>

#define SAMPLE_SIZE 2500

extern void *SGI_GV_Pin;
extern void *SGI_GV_Ter;
extern void *SGI_GV_Ack;
extern void *SGI_GV_Adc;
extern void *SGI_GV_Wfi;
extern void *SGI_GV_Dsp;
extern void *SGI_GV_EFt;
extern void *SGI_GV_Apw;
extern void *SGI_GV_Eep;
extern void *SGI_GV_Sen;
extern void *SGI_GV_Bat;


//Serial Interface related global variable
#define ENABLE_DEBUG true
#define PRINTLN(x) ((SGI_SERIAL *)SGI_GV_Ter)->println(String(x));
#define PRINT(x) ((SGI_SERIAL *)SGI_GV_Ter)->print(String(x));

//Wifi related global variables
#include <WiFi.h>
#include <ESPmDNS.h>
#include <ArduinoWebsockets.h>

using namespace websockets;

#ifndef WIFI_PASS
#define WIFI_PASS "/'J2Cn5_sB>@qb#A"
#endif

#define CHECK_PING_EVERY_x_SECONDS 2


void SGI_GF_wifiOnEvent (WebsocketsClient&, WebsocketsEvent, WSInterfaceString);
void SGI_GF_onWiFiMessage(WebsocketsClient& client, WebsocketsMessage message);



//Autoshutdown
#define TIME_IN_SECONDS_AUTO_SHUTDOWN (5*60)
#define TIME_IN_SECONDS_PRESSED_TO_OFF (0.1)

//Temperature, humidity and battery levels

#define SHUT_DOWN_BATTERY_VOLTAGE_BELOW 3.2
#define SHUT_DOWN_TEMPERATURE_AHOVE 100
#define SHUT_DOWN_HUMIDITY_ABOVE 85



//Fault emulator

void SGI_GF_FaultLoop(void*);

#define TIME_IN_MS_FOR_HALF_CYCLE 17 // CIEL OF (1/60)

//Sensor

#define UPDATE_SENSOR_EVERY_x_SECONDS 1

#endif 
