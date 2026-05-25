#include "core-0.h"

TaskHandle_t Task0Handle = NULL;
void Task0Code(void* pvParameters) {
    for (;;) {
        if (Serial.available()) {
            char c = Serial.read();
            if (c == 0x30) {
                simStacks->displayStackInfo();
                Serial.println("Received command to display stack info");
                Serial.print("Number of channels: ");
                Serial.println(TOTAL_CHANNELS);
                Serial.print("Stack size per channel: ");
                Serial.println(THE_MAX_STACK_SIZE_PER_CHANNEL);
                Serial.print("Current Stack: ");
                Serial.println(TOTAL_CHANNELS * THE_MAX_STACK_SIZE_PER_CHANNEL *
                               sizeof(int32_t) * 2 /
                               1024);  // Print total stack size in KB
            }
        }
        vTaskDelay(10 / portTICK_PERIOD_MS);  // Yield time to the OS
    }
}
