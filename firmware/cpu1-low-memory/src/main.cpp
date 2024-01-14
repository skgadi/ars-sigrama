#include "main.h"


SIG_SAMPLE sample;
SIG_PINS pins;
SIG_ADC adc;
SIG_CALIBRATE calibrate;
SIG_RESAMPLE resample;

void setup() {
  pins.setup();
  Serial.begin(115200);
  adc.setup();
}

void loop() {
  
  if (Serial.available() != 0) {
    int c = Serial.parseInt();
    if (c == 1) {
      //prepareSample();
      unsigned long start = micros();


      sample.prepare();
      
      
      //resample.calculateFrequency();
      unsigned long end = micros();
      Serial.print("Time Taken: ");
      float timePeriodInMs;
      if (end > start) {
        timePeriodInMs = (float)(end - start)/1000.0f;
      } else {
        timePeriodInMs = (float)(end + (0xFFFFFFFF - start))/1000.0f;
      }
      Serial.println(timePeriodInMs);
      Serial.println("Sample Ready");
    }
    if (c == 2) {
      sample.printSingal();
    }
    if (c == 3) {
      sample.printFrequency();
    }
    if (c == 4) {
      sample.prepare();
      sample.printFrequency();
    }
  }
}

