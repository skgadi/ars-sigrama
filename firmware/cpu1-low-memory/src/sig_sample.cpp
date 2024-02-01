#include "sig_sample.h"

void SIG_SAMPLE::prepare() {


  digitalWrite(PIN_ADC_CS, LOW);
  SPI.transfer16((uint16_t)(0x1800));
  SPI.transfer16((uint16_t)(0x1800));
  SPI.transfer16((uint16_t)(0x1800));
  int idx = 0;

  unsigned long startTime = micros();
  for (int i = 0; i < SAMPLE_SIZE; i++) {
    *((uint16_t *)CHANNELS + (idx++)) = SPI.transfer16(0x1000);
    *((uint16_t *)CHANNELS + (idx++)) = SPI.transfer16(0x0800);
    *((uint16_t *)CHANNELS + (idx++)) = SPI.transfer16(0x2000);
    *((uint16_t *)CHANNELS + (idx++)) = SPI.transfer16(0x0000);
    *((uint16_t *)CHANNELS + (idx++)) = SPI.transfer16(0x0000);
    *((uint16_t *)CHANNELS + (idx++)) = SPI.transfer16(0x0000);
    *((uint16_t *)CHANNELS + (idx++)) = SPI.transfer16(0x2000);
    *((uint16_t *)CHANNELS + (idx++)) = SPI.transfer16(0x1800);
  }
  unsigned long endTime = micros();
  float timePeriod;
  if (endTime > startTime) {
    timePeriod = (float)(endTime - startTime)/1000.0f;
  } else {
    timePeriod = (float)(endTime + (0xFFFFFFFF - startTime))/1000.0f;
  }
  stepTime = timePeriod/SAMPLE_SIZE;

  digitalWrite(PIN_ADC_CS, HIGH);

  calculateFrequency();
}

void SIG_SAMPLE::print() {
  printSingal();
  printFrequency();
}


void SIG_SAMPLE::printSingal() {
  //Signal Data
  Serial.println("t,V1,V2,V3,V4,I1,I2,I3,I4,");
  for (int i = 0; i < SAMPLE_SIZE; i++) {
    Serial.print(i*stepTime);
    Serial.print(",");
    for (int j = 0; j < 8; j++) {
      Serial.print(CHANNELS[i][j]);
      Serial.print(",");
    }
    Serial.println();
  }
}

void SIG_SAMPLE::printFrequency() {
  //Frequency Data
  Serial.print("Frequency: ");
  Serial.println(powerFrequency);
}

void SIG_SAMPLE::sendRawData() {
  //send fundamental frequency float as 4 bytes
  Serial.write((uint8_t *)&powerFrequency, 4);
}



uint16_t SIG_SAMPLE::get(int i, int j) {
  return CHANNELS[i][j];
}

void SIG_SAMPLE::set(int i, int j, uint16_t value) {
  CHANNELS[i][j] = value;
}

float SIG_SAMPLE::getStepTime() {
  return stepTime;
}

void SIG_SAMPLE::calculateFrequency() {


  float zeroCrossingUp = 0;
  float zeroCrossingDown = 0;
  float lastZeroCrossingUp = -1;
  float lastZeroCrossingDown = -1;
  float totalTimeForZeroCrossingUp = 0;
  float totalTimeForZeroCrossingDown = 0;
  this->firstZeroCrossingOfMainChannel = -1;
  this->timeForFirstZeroCrossingOfMainChannel = -1;
  float previousReading = CHANNELS[0][0] - calibrate.getVoltageOffset();
  for (int i = 1; i < SAMPLE_SIZE; i++) {
    float presentReading = CHANNELS[i][0] - calibrate.getVoltageOffset();
    if (previousReading > 0.0f) {
      if (presentReading <= 0.0f) {
        if (i-lastZeroCrossingUp > 2) {
          float exactZeroCrossingTime = i - presentReading/(presentReading - previousReading);
          zeroCrossingUp++;
          if (lastZeroCrossingUp != -1) {
            totalTimeForZeroCrossingUp += (exactZeroCrossingTime-lastZeroCrossingUp);
          }
          lastZeroCrossingUp = exactZeroCrossingTime;
        }
      }
    } else {
      if (presentReading > 0.0f) {
        /*if (firstZeroCrossingOfMainChannel == -1 && i>4) {
          //find the exact zero crossing time
          float exactZeroCrossingTime = i*1.0f - presentReading/(presentReading - previousReading);
          //Serial.print("First Zero Crossing: ");
          //Serial.println(i);
          firstZeroCrossingOfMainChannel = i;
        }*/
        if (i-lastZeroCrossingDown > 2) {
          float exactZeroCrossingTime = i - presentReading/(presentReading - previousReading);
          if (timeForFirstZeroCrossingOfMainChannel<0) {
            this->firstZeroCrossingOfMainChannel = i;
            //Serial.print("First exactZeroCrossingTime: ");
            //Serial.println(exactZeroCrossingTime);
            this->timeForFirstZeroCrossingOfMainChannel = exactZeroCrossingTime*stepTime;
          }
          zeroCrossingDown++;
          if (lastZeroCrossingDown != -1) {
            totalTimeForZeroCrossingDown += (exactZeroCrossingTime-lastZeroCrossingDown);
          }
          lastZeroCrossingDown = exactZeroCrossingTime;
        }
      }
    }
    previousReading = presentReading;
  }





  if (zeroCrossingUp>2 && zeroCrossingDown>2) {
    float timePeriodInMs = stepTime * (totalTimeForZeroCrossingUp/(zeroCrossingUp-1) + totalTimeForZeroCrossingDown/(zeroCrossingDown-1))/2.0f;
    powerFrequency = 1.0f/timePeriodInMs*1000.0f;
  } else {
    powerFrequency = -1.0f;
  }

  if (powerFrequency < MIN_FREQUENCY_LIMIT || powerFrequency > MAX_FREQUENCY_LIMIT) {
    powerFrequency = -1.0f;
  }
}

float SIG_SAMPLE::getFrequency() {
  return powerFrequency;
}

int SIG_SAMPLE::getFirstZeroCrossingOfMainChannel() {
  return firstZeroCrossingOfMainChannel;
}

float SIG_SAMPLE::getTimeForFirstZeroCrossingOfMainChannel() {
  return timeForFirstZeroCrossingOfMainChannel;
}