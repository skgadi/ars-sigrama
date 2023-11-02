#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_ack.h"
#ifndef SGI_ACK_H
#define SGI_ACK_H

#include "sgi_global.h"

class SGI_ACK {
  void setup();
  volatile BYTE_BITS ack;
  public:
    SGI_ACK();
    char getACK();
    void setBit(int, bool);
};


#endif