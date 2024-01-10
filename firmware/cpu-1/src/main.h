#ifndef MAIN_H
#define MAIN_H


#include <Arduino.h>

#include "SPI.h"



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

void setupPins();
void SPI_ADC_Init();

uint16_t readADC(uint8_t channel);



#define CALIBRATION_OFFSET (2047) // A dyanamic value may be used in future to calibrate the ADC
#define CALIBRATION_GAIN (1.0f) // A dyanamic value may be used in future to calibrate the ADC

#define VOLTAGE_GAIN (150000.0f/820.0f*3.3f*2.0f/4095.0f)

#define CURRENT_GAIN (40.0f/273.0f)


extern unsigned long startTime;

class READING {
  float time;
  union {
    float readings[8];
    struct {
      float voltages[4];
      float currents[4];
    };
    struct {
      float v_l1; //Voltage L1
      float v_l2; //Voltage L2
      float v_l3; //Voltage L3
      float v_n; //Voltage Neutral
      float i_l1; //Current L1
      float i_l2; //Current L2
      float i_l3; //Current L3
      float i_n; //Current Neutral
    };
  };
  public:
  READING() {
    time = 0;
    for (int i = 0; i < 8; i++) {
      readings[i] = 0;
    }
  }
  READING(uint16_t *data) {
    unsigned long currentTime = micros();
    if (currentTime < startTime) {
      time = (currentTime + (0xFFFFFFFF - startTime)) / 1000000.0f;
    } else {
      time = (currentTime - startTime) / 1000000.0f;
    }
    for (int i = 0; i < 8; i++) {
      readings[i] = data[i]*1.0f;//(data[i]-CALIBRATION_OFFSET)*CALIBRATION_GAIN;
    }
    /*for (int i = 0; i < 4; i++) {
      voltages[i] *= VOLTAGE_GAIN;
      currents[i] *= CURRENT_GAIN;
    }*/
  }
  float getVoltage(uint8_t channel) {
    return voltages[channel];
  }
  float getCurrent(uint8_t channel) {
    return currents[channel];
  }
  float getChannel(uint8_t channel) {
    return readings[channel];
  }
  float getTime() {
    return time;
  }
};

READING prepareReading();

#define SAMPLE_SIZE 2048

extern READING sample[SAMPLE_SIZE];

void prepareSample();
void printSample();


#endif // MAIN_H
