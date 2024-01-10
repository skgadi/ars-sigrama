
#ifndef SGI_SERIAL_H
#define SGI_SERIAL_H

#include "driver/uart.h"

class SGI_SERIAL {
private:
public:
  SGI_SERIAL(/* args */);
  void readAllSamples();
};

#endif // SGI_SERIAL_H
