
#include "main.h"

// Task handles
TaskHandle_t dataProcessingTaskHandle = NULL;
TaskHandle_t userInterfaceTaskHandle = NULL;

// Task functions
void dataProcessingTask(void *pvParameters) {
  TickType_t xLastWakeTime;
  const TickType_t xFrequency = 2000 / portTICK_PERIOD_MS;

  // Initialize the last wake time
  xLastWakeTime = xTaskGetTickCount();

  while (1) {
    // Data processing task code here

    for (int i = 0; i < 8; i++) {
      ((SGI_ADC*)SGI_ADC_PTR)->readChannel(i);
    }

    vTaskDelayUntil(&xLastWakeTime, xFrequency);
  }
}

void userInterfaceTask(void *pvParameters) {
  TickType_t xLastWakeTime;
  const TickType_t xFrequency = 10 / portTICK_PERIOD_MS;

  // Initialize the last wake time
  xLastWakeTime = xTaskGetTickCount();

  char command[] = "0";
  while (1) {
    // User interface task code here
    scanf("%1s\n", command);
    if (command[0] == '1') {
      ESP_LOGI(TAG_FOR_MAIN, "Pressed 1");
    }
    if (command[0] == '2') {
      ESP_LOGI(TAG_FOR_MAIN, "Pressed 2");
    }
    command[0] = '0';

    vTaskDelayUntil(&xLastWakeTime, xFrequency);
  }
}

extern "C" {
  void app_main();
}

void app_main() {
  // Generate the constants
  assignPointersAndGenerateConstants();

  // Create the data processing task
  xTaskCreate(dataProcessingTask, "DataProcessingTask", 20000, NULL, tskIDLE_PRIORITY, &dataProcessingTaskHandle);

  // Create the user interface task
  xTaskCreate(userInterfaceTask, "UserInterfaceTask", 20000, NULL, tskIDLE_PRIORITY, &userInterfaceTaskHandle);

  ESP_LOGI(TAG_FOR_MAIN, "Starting up...");
}


// pointers to the classes that are defined in the header files
void *SGI_PINS_PTR = NULL;
void *SGI_SERIAL_PTR = NULL;
void *SGI_SAMPLE_PTR = NULL;
void *SGI_ADC_PTR = NULL;

// The actual classes. They are not generated by the code generator
// because memory allocation is clear at the compile time.
SGI_PINS SGI_PINS_VAL;
SGI_SERIAL SGI_SERIAL_VAL;
SGI_SAMPLE SGI_SAMPLE_VAL;
SGI_ADC SGI_ADC_VAL;

// This fucntion assigns the pointers to the classes and generates the constants
void assignPointersAndGenerateConstants() {
  ESP_LOGI(TAG_FOR_MAIN, "Assigning pointers and generating constants ...");
  SGI_PINS_PTR = &SGI_PINS_VAL;
  SGI_SERIAL_PTR = &SGI_SERIAL_VAL;
  SGI_SAMPLE_PTR = &SGI_SAMPLE_VAL;
  SGI_ADC_PTR = &SGI_ADC_VAL;
}

