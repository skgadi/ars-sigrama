#ifndef SIG_SD_CARD_H
#define SIG_SD_CARD_H

#include "Arduino.h"

#include "SD.h"
#include "SPI.h"


#define SIG_SD_CS 5
#define SIG_SD_MOSI 23
#define SIG_SD_MISO 19
#define SIG_SD_SCK 18


class SIG_SD_CARD {
  File mainFile;
  File testFile;
  bool isIntializationSuccessful=false;
  bool openMainFile();
  bool closeMainFile();
  bool openTestFile();
  bool initializeSD();
  public:
    SIG_SD_CARD();
    bool isSDCardPresent();
    bool appendToSDCard(float *, int );
    bool closeAllFiles();
};

extern SIG_SD_CARD sdCard;

#endif // SIG_SD_CARD_H
