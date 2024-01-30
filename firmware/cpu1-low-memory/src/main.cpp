#include "main.h"


SIG_SAMPLE sample;
SIG_PINS pins;
SIG_ADC adc;
SIG_CALIBRATE calibrate;
SIG_RESAMPLE resample;
SIG_FFT fft;
SIG_POWER power;
SIG_ESP_NOW sig_esp_now;
SIG_CORE_0 sig_core_0;
SIG_CORE_1 sig_core_1;

void setup() {
  pins.setup();
  Serial.begin(115200);
  adc.setup();
  power.setup();

  sig_esp_now.setup();

  sig_core_0.setup();
  sig_core_1.setup();
}

void loop() {
  
  if (Serial.available() != 0) {
    int c = Serial.parseInt();
    if (c == 1) {
      sample.prepare();
      resample.prepare();
      fft.prepare();
      Serial.write(0x67);
    }
    if (c == 2) {
      resample.sendRawData();
    }
    if (c == 3) {

      //preparing sample
      sample.prepare();
      resample.prepare();
      fft.prepare();
      power.loop(); // Energy calculations are incorrect because this is not called at regular intervals


      //sending sample
      sample.sendRawData();
      resample.sendRawData();
      fft.sendRawData();
    }
    if (c == 10) {
      //prepareSample();
      unsigned long start = micros();


      sample.prepare();
      resample.prepare();
      fft.prepare();
      
      
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
    if (c == 20) {
      sample.printSingal();
    }
    if (c == 30) {
      sample.printFrequency();
    }
    if (c == 40) {
      sample.prepare();
      sample.printFrequency();
    }
    if (c == 50) {
      resample.printData();
    }
    if (c == 60) {
      fft.printAmplitudeData();
    }
    if (c == 70) {
      fft.printPhaseData();
    }
  }
}

