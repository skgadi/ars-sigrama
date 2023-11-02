#include "sgi_include_all.h"



SGI_ADC::SGI_ADC (int sampleSize) {
  this->sampleSize = sampleSize;
  this->packageSize = sampleSize*13+1 + 4 + 4 + sizeof (SGI_ON_BOARD_STORAGE) + 1; // (4 for time + 2 for each adc + 1 for ack + temp-sensors + humidity sensor + eeprom data) for each sample + 1 for fault type + 1 for battery
  setup();
}

void SGI_ADC::setup () {

  SPI.begin(PIN_ADC_SCK, PIN_ADC_MISO, PIN_ADC_MOSI, PIN_ADC_CS);
  SPI.setFrequency(15000000);
  SPI.setBitOrder(MSBFIRST);
  SPI.setDataMode(SPI_MODE0);
  package = new char[packageSize];
  samplingTimeUS = 25;
  idxADC = 0;
  //offsetForADC = 2048;
}

uint16_t SGI_ADC::readRawADC(int channel) {
  digitalWrite(PIN_ADC_CS, LOW);
  uint16_t rawVal = SPI.transfer16((uint16_t)channel << 11);
  digitalWrite(PIN_ADC_CS, HIGH);
  return rawVal;
}

void SGI_ADC::captureADC (int channel) {
  //int16_t readVal = (int16_t)(readRawADC(channel) - offsetForADC);
  WORD_VAL readVal;
  readVal.Val = readRawADC(channel);//offset is disabled, done at android part
  package[idxADC++] = readVal.v[0];
  package[idxADC++] = readVal.v[1];
}

void SGI_ADC::setSamplingTimeUS (int delay) {
  samplingTimeUS = delay;
}

int SGI_ADC::getSamplingTimeUS () {
  return samplingTimeUS;
}

void SGI_ADC::setStartRecording() {
  startRecording = true;
}

void SGI_ADC::preparePackage (char fault) {
  SGI_EMULATE_FAULT * hEft = (SGI_EMULATE_FAULT *)SGI_GV_EFt;
  float timeOutTest = millis()*1.0/1000.0;
  while (!startRecording){
    if (millis()*1.0/1000.0 - timeOutTest >5) {
      break;
    }
  }
  startRecording = false;

  idxADC = 0;
  package = new char[packageSize];
  readRawADC(3);
  float startTime = 0;
  FLOAT_VAL timeOfSample;
  for (int i = 0; i < sampleSize; i++) {
    timeOfSample.Val = micros() * 1.0e-6 - startTime; //Time in mS
    captureADC(2);
    captureADC(1);
    captureADC(0);
    captureADC(3);
    package[idxADC++] = ((SGI_ACK*)SGI_GV_Ack)->getACK();
    //timeOfSample = (timeOfSample + micros() * 1.0e-3 - startTime) / 2.0;
    if (i == 0) {
      startTime = timeOfSample.Val;
      timeOfSample.Val = 0.0;
    }
    package[idxADC++] = timeOfSample.v[0];
    package[idxADC++] = timeOfSample.v[1];
    package[idxADC++] = timeOfSample.v[2];
    package[idxADC++] = timeOfSample.v[3];
    delayMicroseconds(samplingTimeUS);
  }
  package[idxADC++] = fault;

  //add temp sensor
  FLOAT_VAL tempFloat = ((SGI_SENSOR*)SGI_GV_Sen)->getTemperature();
  for (int i=0; i<4; i++) {
    package[idxADC++] = tempFloat.v[i];
  }
  //add humd sensor
  tempFloat = ((SGI_SENSOR*)SGI_GV_Sen)->getHumidity();
  for (int i=0; i<4; i++) {
    package[idxADC++] = tempFloat.v[i];
  }

  //add eeprom data
  SGI_ON_BOARD_STORAGE eepromData = ((SGI_EEPROM*) SGI_GV_Eep)->getData();
  for (int i=0; i<sizeof(eepromData.gains)/sizeof(float); i++) {
    //PRINTLN(eepromData.gains[i].Val);
    for (int j=0; j<4; j++) {
      package[idxADC++] = eepromData.gains[i].v[j];
    }
    for (int j=0; j<4; j++) {
      package[idxADC++] = eepromData.offsets[i].v[j];
    }
  }
  for (int i=0; i<8; i++) {
    package[idxADC++] = eepromData.timeStamp.v[i];
  }
  hEft->setAuxPower(false);

  //add battery info
  package[idxADC++] = ((SGI_BAT*)SGI_GV_Bat)->getBatteryStatus();
}

int SGI_ADC::getSampleSize() {
  return sampleSize;
}

char *SGI_ADC::getPackage() {
  return package;
}

int SGI_ADC::getPackageSize() {
  return packageSize;
}