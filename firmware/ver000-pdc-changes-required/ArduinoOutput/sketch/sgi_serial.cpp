#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_serial.cpp"
#include "sgi_include_all.h"



SGI_SERIAL::SGI_SERIAL(bool enable) {
  this->enabled = enable;
  setup();
}

void SGI_SERIAL::setup() {
  if (enabled) {
    Serial.begin(115200);
    Serial.println("Serial interface started.");
  }
}

void SGI_SERIAL::print(String value){
  if (enabled) {
    Serial.print(value);
  }
}

void SGI_SERIAL::println(String value) {
  if (enabled) {
    Serial.println(value);
  }
}

void SGI_SERIAL::printPackage () {
  if (enabled) {
    int sampleSize = ((SGI_ADC*)SGI_GV_Adc)->getSampleSize();
    char *package = ((SGI_ADC*)SGI_GV_Adc)->getPackage();
    for (int i = 0; i < sampleSize; i++) {
      int16_t V1 = *((int16_t *)&package[i * 13 + 0]);
      int16_t V2 = *((int16_t *)&package[i * 13 + 2]);
      int16_t V3 = *((int16_t *)&package[i * 13 + 4]);
      int16_t IE = *((int16_t *)&package[i * 13 + 6]);
      int8_t ack = package[i * 13 + 8];
      float time = *((float *)&package[i * 13 + 9]);
      Serial.print(time * 1000);
      Serial.print(",");
      Serial.print(V1);
      Serial.print(",");
      Serial.print(V2);
      Serial.print(",");
      Serial.print(V3);
      Serial.print(",");
      Serial.print(IE);
      Serial.print(",");
      Serial.print(ack);
      Serial.println("");
    }
    int idxForPackage = sampleSize*13;
    //Fault type
    
    Serial.print("Fault type: ");
    Serial.println(package[idxForPackage++]);
    //Sensor data
    FLOAT_VAL tempVal;
    for (int i=0;i<4;i++) {
      tempVal.v[i] = package[idxForPackage++];
    }
    Serial.print("Temperature: ");
    Serial.println(tempVal.Val);
    for (int i=0;i<4;i++) {
      tempVal.v[i] = package[idxForPackage++];
    }
    Serial.print("Humidity: ");
    Serial.println(tempVal.Val);


    //Eeprom data
    SGI_ON_BOARD_STORAGE eepromData;
    for (int i=0; i<4; i++) {
      for (int j=0;j<4;j++) {
        eepromData.gains[i].v[j] = package[idxForPackage++];
      }
      Serial.print("Gain [");
      Serial.print((i+1));
      Serial.print("]: ");
      Serial.println(eepromData.gains[i].Val);
      for (int j=0;j<4;j++) {
        eepromData.offsets[i].v[j] = package[idxForPackage++];
      }
      Serial.print("Offset [");
      Serial.print((i+1));
      Serial.print("]: ");
      Serial.println(eepromData.offsets[i].Val);
    }
    for (int j=0;j<8;j++) {
      eepromData.timeStamp.v[j] = package[idxForPackage++];
    }
    Serial.print("Time stamp: ");
    Serial.println(eepromData.timeStamp.Val);
  }
}

void SGI_SERIAL::loop() {
  if (enabled) {
    if (Serial.available()) {
      float readVal = Serial.parseFloat();
      if (readVal==1) {
        ((SGI_ADC*)SGI_GV_Adc)->preparePackage(0);
      } else if(readVal==2) {
        printPackage();
      } else if(readVal == 3) {
        SGI_ON_BOARD_STORAGE val;
        val.gains[0].Val = 5.2;
        val.offsets[0].Val = 9.7;
        val.gains[1].Val = 6.4;
        val.offsets[1].Val = 10.6;
        val.gains[2].Val = 7.7;
        val.offsets[2].Val = 11.1;
        val.gains[3].Val = 8.4;
        val.offsets[3].Val = 12.5;
        val.timeStamp.Val = 3870912047;
        ((SGI_EEPROM*)SGI_GV_Eep)->updateData(val);
      } else if (readVal == 4) {
        //((SGI_DISPLAY*)SGI_GV_Dsp)->setDisplayAs(0x0200);
      } else if (readVal == 5) {
        //((SGI_DISPLAY*)SGI_GV_Dsp)->setDisplayAs(0x0400);
      } else if (readVal == 6) {
        //((SGI_DISPLAY*)SGI_GV_Dsp)->setDisplayAs(0x0500);
      } else if (readVal == 7) {
        printMemoryStats();        
      }
    }
  }
}

void SGI_SERIAL::printMemoryStats() {
  if (enabled) {
    Serial.print("Total heap: ");
    Serial.println (ESP.getHeapSize());
    Serial.print("Free heap: ");
    Serial.println (ESP.getFreeHeap());
    Serial.print("Total PSRAM: ");
    Serial.println (ESP.getPsramSize());
    Serial.print("Free PSRAM: ");
    Serial.println (ESP.getFreePsram());
  }
}
