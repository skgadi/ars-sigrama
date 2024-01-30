#include "sig_sd_card.h"

SIG_SD_CARD::SIG_SD_CARD() {
  pinMode(SIG_SD_CS, OUTPUT);
  digitalWrite(SIG_SD_CS, HIGH);
  if (!initializeSD()) {
    return;
  }
  openMainFile();
}

bool SIG_SD_CARD::initializeSD() {
  SPI.begin(SIG_SD_SCK, SIG_SD_MISO, SIG_SD_MOSI, SIG_SD_CS);
  if (!SD.begin(SIG_SD_CS, SPI, 4000000)) {
    Serial.println("SD card initialization failed!");
    return isIntializationSuccessful = false;
  } else {
    Serial.println("SD card initialization done.");
    isIntializationSuccessful=true;
    return isIntializationSuccessful = true;
  }
}

bool SIG_SD_CARD::openMainFile() {
  if (isIntializationSuccessful) {
    closeMainFile();
    mainFile = SD.open("/main.bin", FILE_APPEND);
    if (mainFile) {
      return true;
    } else {
      return false;
    }
  } else {
    return false;
  }
}

bool SIG_SD_CARD::closeMainFile() {
  if (isIntializationSuccessful) {
    mainFile.close();
    return true;
  } else {
    return false;
  }
}


bool SIG_SD_CARD::isSDCardPresent() {
  if (isIntializationSuccessful) {
    // generate a random character
    char randomChar = random(0, 255);
    testFile = SD.open("/test.bin", FILE_WRITE);
    testFile.write(randomChar);
    testFile.close();
    testFile = SD.open("/test.bin", FILE_READ);
    if (testFile.read()==randomChar) {
      return true;
    } else {
      return false;
    }
    testFile.close();
  } else {
    return initializeSD();
  }
}

bool SIG_SD_CARD::appendToSDCard(float *data, int length) {
  if (isIntializationSuccessful) {
    if (mainFile) {
      mainFile.write((uint8_t *)data, length*sizeof(float));
      mainFile.flush();
      return true;
    } else {
      return openMainFile();
    }
  } else {
    return initializeSD();
  }
}

bool SIG_SD_CARD::closeAllFiles() {
  return closeMainFile();
}