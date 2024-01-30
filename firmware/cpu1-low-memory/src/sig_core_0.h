#ifndef SIG_CORE_0_H
#define SIG_CORE_0_H

#include "main.h"


extern TaskHandle_t taskForCore0;

class SIG_CORE_0 {
  public:
    void setup();
};

extern SIG_CORE_0 sig_core_0;
void codeForTaskForCore0( void * pvParameters );

#endif // SIG_CORE_0_H
