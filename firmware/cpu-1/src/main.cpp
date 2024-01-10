#include "main.h"


void setup() {
  setupPins();
  Serial.begin(115200);
  SPI_ADC_Init();
}

void loop() {
  
  if (Serial.available()) {
    char c = Serial.parseFloat();
    if (c == 1) {
      prepareSample();
      Serial.println("Sample Ready");
    }
    if (c == 2) {
      printSample();
    }
  }
}


void setupPins() {
    //IO PINs
  //Turn off all the relays.
  // Relay for 3 phase power
  pinMode(PIN_SELECT_L1, PULLDOWN);
  pinMode(PIN_SELECT_L2, PULLDOWN);
  pinMode(PIN_SELECT_L1, OUTPUT);
  pinMode(PIN_SELECT_L2, OUTPUT);
  digitalWrite(PIN_SELECT_L1, LOW);
  digitalWrite(PIN_SELECT_L2, LOW);
  //Relay for earth wire discontinuty
  pinMode(PIN_RLY_SIGNAL_ED, PULLDOWN);
  pinMode(PIN_RLY_SIGNAL_ED, OUTPUT);
  digitalWrite(PIN_RLY_SIGNAL_ED, LOW);

  
  //Auto power off - Hold the pin high to keep the circuit on
  pinMode(PIN_AUTO_POWER, INPUT_PULLUP);


  //Setting ADC SPI PIN configuration
  pinMode(PIN_ADC_MISO, INPUT_PULLUP);
  pinMode(PIN_ADC_MOSI, OUTPUT);
  pinMode(PIN_ADC_SCK, OUTPUT);
  pinMode(PIN_ADC_CS, OUTPUT);

  //Setting Power pins
  pinMode(PIN_ENABLE_POWER, OUTPUT);
  pinMode(PIN_ENABLE_AUX_POWER, OUTPUT);

  




  //Aux and cable ack pins
  pinMode(PIN_AUX, INPUT);
  pinMode(PIN_CABLE_ACK, INPUT);
}

void SPI_ADC_Init() {
  SPI.begin(PIN_ADC_SCK, PIN_ADC_MISO, PIN_ADC_MOSI, PIN_ADC_CS);
  SPI.setFrequency(16000000);
  SPI.setBitOrder(MSBFIRST);
  SPI.setDataMode(SPI_MODE0);
}

uint8_t channelAddress[8] = {0x02, 0x01, 0x04, 0x00, 0x00, 0x00, 0x04, 0x03};
uint16_t readADC(uint8_t channel) {
  uint16_t rawVal = SPI.transfer16((uint16_t)(channelAddress[channel] << 11));
  return rawVal;
}

READING prepareReading() {
  uint16_t data[8];
  for (int i = 0; i < 8; i++) {
    data[i] = readADC(i);
  }
  return READING(data);
}

unsigned long startTime = 0;
READING sample[SAMPLE_SIZE];
void prepareSample() {
  digitalWrite(PIN_ADC_CS, LOW);
  readADC(7);
  readADC(7);
  readADC(7);
  startTime = micros();
  for (int i = 0; i < SAMPLE_SIZE; i++) {
    sample[i] = prepareReading();
  }
  digitalWrite(PIN_ADC_CS, HIGH);
}

void printSample() {
  Serial.println("t,V1,V2,V3,V4,I1,I2,I3,I4,");
  for (int i = 0; i < SAMPLE_SIZE; i++) {
    Serial.print(sample[i].getTime(), 6);
    Serial.print(",");
    for (int j = 0; j < 8; j++) {
      Serial.print(sample[i].getChannel(j));
      Serial.print(",");
    }
    Serial.println();
  }
}