#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_adc.h"
#ifndef SGI_ADC_H
#define SGI_ADC_H

#include "sgi_global.h"
#include "SPI.h"



class SGI_ADC {
  void setup();
  int packageSize;
  int sampleSize;
  //int offsetForADC;
  int idxADC;
  uint16_t readRawADC(int);
  void captureADC(int);
  int samplingTimeUS;
  char* package;
  volatile bool startRecording=false;
  public:
    SGI_ADC(int);
    void preparePackage(char);
    void setSamplingTimeUS(int);
    int getSamplingTimeUS();
    int getSampleSize();
    void setStartRecording();
    char* getPackage();
    int getPackageSize();
};


#endif 