#include "main.h"

SIG_SD_CARD sdCard;
SIG_ESP_NOW sig_esp_now;

void setup() {
  Serial.begin(115200);
  WiFi.mode(WIFI_MODE_STA);
  sig_esp_now.setup();
  Serial.println(WiFi.macAddress()); // Print MAC address 7C:9E:BD:F4:F6:60
}

void loop() {
  if (Serial.available()) {
    float input = Serial.parseFloat();
    unsigned long start;
    unsigned long end;
    if (input == 0.0) {
      return;
    }
    if (input == 1.0) {
      start = micros();
      bool isSDCardPresent = sdCard.isSDCardPresent();
      end = micros();
      Serial.print("isSDCardPresent: ");
      Serial.println(isSDCardPresent);
    }
    if (input == 2.0) {
      start = micros();
      bool isAllFilesClosed = sdCard.closeAllFiles();
      end = micros();
      end = micros();
      end = micros();
      Serial.print("closeAllFiles: ");
      Serial.println(isAllFilesClosed);
    }
    if (input == 3.0) {
      int length = 1000;
      float dataToWrite[length];
      for (int i = 0; i < length; i++) {
        dataToWrite[i] = i;
      }
      start = micros();
      bool isDataWritten = sdCard.appendToSDCard(dataToWrite, length);
      end = micros();
      end = micros();
      Serial.print("isDataWritten: ");
      Serial.println(isDataWritten);
    }

    // Print time
    Serial.print("Time: ");
    float time;
    if (end >= start) {
      time = (float)(end - start) / 1000.0;
    } else {
      time = (float)((end + (4294967295 - start))*1.0) / 1000.0;
    }
    Serial.print(time);
    Serial.println(" ms");
  }
}
