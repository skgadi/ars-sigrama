#include "main.h"

SIG_SIM_LOOP* simLoop = nullptr;

void setup() {
    Serial.begin(115200);

    // Turn off Wi-Fi and Bluetooth radio hardware completely
    // WiFi.disconnect(true);
    // WiFi.mode(WIFI_OFF);
    // btStop();

    // Create your Core 0 task
    Serial.println("Setting up Cores ...");

    xTaskCreatePinnedToCore(
        Task0Code,     // Name of the function
        "Task0",       // Name of the task (for debugging)
        10000,         // Stack size in words
        NULL,          // Parameter passed to the task
        1,             // Task priority (higher numbers = higher priority)
        &Task0Handle,  // Task handle to keep track of it
        0              // Core ID (0 or 1)
    );

    simLoop = new SIG_SIM_LOOP();

    Serial.println("Setup complete. Starting loop...");
    while (true) {
        simLoop->loop();
    }
}

void loop() {}
