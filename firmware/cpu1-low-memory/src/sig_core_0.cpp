#include "sig_core_0.h"

TaskHandle_t taskForCore0;


void SIG_CORE_0::setup() {
  Serial.println("Core 0");
  xTaskCreatePinnedToCore(
                    codeForTaskForCore0,   /* Task function. */
                    "taskForCore0",     /* name of task. */
                    10000,       /* Stack size of task */
                    NULL,        /* parameter of the task */
                    1,           /* priority of the task */
                    &taskForCore0,      /* Task handle to keep track of created task */
                    0);          /* pin task to core 0 */                  
}

void codeForTaskForCore0( void * pvParameters ){


    while(1){
      sample.prepare();
      resample.prepare();
      fft.prepare();
      power.loop();
      vTaskDelay(SAMPLING_TIME_FOR_ENERGY_CALCULATION / portTICK_PERIOD_MS);
    }
  }