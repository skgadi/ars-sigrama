
#ifndef MAIN_H
#define MAIN_H

#include <stdio.h>
#include "driver/gpio.h"
#include "esp_log.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/queue.h"

#include "driver/spi_master.h"
#include <string.h>


void assignPointersAndGenerateConstants();


static const char *TAG_FOR_MAIN = "SGI_MAIN";
static const char *TAG_FOR_SERIAL = "SGI_SERIAL";
static const char *TAG_FOR_SAMPLE = "SGI_SAMPLE";
static const char *TAG_FOR_ADC = "SGI_ADC";


void assignPointersAndGenerateConstants();
extern void *SGI_PINS_PTR;
extern void *SGI_SERIAL_PTR;
extern void *SGI_SAMPLE_PTR;
extern void *SGI_ADC_PTR;





//Constants for sampling and processing
#define SAMPLE_SIZE 2500
#define NUMBER_OF_CHANNELS 8 // 4 for voltages and 4 for currents (3 phases and neutral)






#include "sgi_pins.h"
#include "sgi_serial.h"
#include "sgi_sample.h"
#include "sgi_adc.h"

#endif // MAIN_H
