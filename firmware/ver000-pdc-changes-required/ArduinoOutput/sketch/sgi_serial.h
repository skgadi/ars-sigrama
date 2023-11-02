#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_serial.h"
#ifndef SGI_SERIAL_H
#define SGI_SERIAL_H

#include "sgi_global.h"


class SGI_SERIAL{
  bool enabled;
  void printMemoryStats();
  public:
  SGI_SERIAL(bool);
  void setup();
  void print(String);
  void println(String);
  void printPackage();
  void loop();
};



#endif