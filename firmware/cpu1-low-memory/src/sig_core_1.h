#ifndef SIG_CORE_1_H
#define SIG_CORE_1_H

#include "main.h"


extern TaskHandle_t taskForCore1;

class SIG_CORE_1 {
  public:
    void setup();
};

extern SIG_CORE_1 sig_core_1;
void codeForTaskForCore1( void * pvParameters );

#endif // SIG_CORE_1_H
