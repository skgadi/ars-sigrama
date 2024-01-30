#include "sig_core_1.h"

TaskHandle_t taskForCore1;


void SIG_CORE_1::setup() {
  Serial.println("Core 1");
  xTaskCreatePinnedToCore(
                    codeForTaskForCore1,   /* Task function. */
                    "taskForCore1",     /* name of task. */
                    10000,       /* Stack size of task */
                    NULL,        /* parameter of the task */
                    1,           /* priority of the task */
                    &taskForCore1,      /* Task handle to keep track of created task */
                    0);          /* pin task to core 0 */                  
}

void codeForTaskForCore1( void * pvParameters ){


    while(1){
      /*sig_esp_now.prepareSystemState();
      sig_esp_now.sendSystemState();*/
      vTaskDelay(10000 / portTICK_PERIOD_MS);
    }
  }