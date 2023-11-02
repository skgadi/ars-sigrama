#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_ack.cpp"
#include "sgi_include_all.h"



SGI_ACK::SGI_ACK() {
  setup();
}

void SGI_ACK::setup() {
  ack.Val = 0x0;
}

void SGI_ACK::setBit(int bit, bool value) {
  if (value) {
    ack.Val |= (1 << bit);
  } else {
    ack.Val &= ~(1 << bit);
  }
}

char SGI_ACK::getACK() {
  ack.bits.b4 = digitalRead(PIN_AUX);
  ack.bits.b5 = digitalRead(PIN_CABLE_ACK);
  return ack.Val;
}