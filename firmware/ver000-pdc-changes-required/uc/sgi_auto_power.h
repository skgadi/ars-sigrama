#ifndef SGI_AUTO_POWER_H
#define SGI_AUTO_POWER_H

#include "sgi_global.h"
#include <EasyButton.h>


class SGI_AUTO_POWER {
  void setup();
  float getCurrentTime();
  float lastPressTime=-1;
  EasyButton* button;
  float shutDownTime;
  bool isStartPress;
  bool flagShutDownNow = false;
  public:
    SGI_AUTO_POWER();
    void loop();
    void mainLoop();
    void resetShutdownTime();
    static void shutdownNow();
    static void shutdownNow(int);
    void shutdownWithDelay(float);
};



#endif
