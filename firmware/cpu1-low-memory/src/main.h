#ifndef MAIN_H
#define MAIN_H

#include <Arduino.h>

void setupPins();
void SPI_ADC_Init();
uint16_t readADC(uint8_t channel);

#define CALIBRATION_OFFSET (2047)
#define CALIBRATION_GAIN (1.0f)
#define VOLTAGE_GAIN (150000.0f/820.0f*3.3f*2.0f/4095.0f)
#define CURRENT_GAIN (40.0f/273.0f)
#define SAMPLE_SIZE 1024
#define NO_OF_CHANNELS 8
#define TOTAL_SAMPLES (SAMPLE_SIZE*NO_OF_CHANNELS)
#define RESAMPLE_SIZE 1024
#define TOTAL_RESAMPLES (RESAMPLE_SIZE*NO_OF_CHANNELS)

// EEPROM MAP
#define EEPROM_CHANNEL_GAIN_ADDRESS 0
#define EEPROM_CHANNEL_OFFSET_ADDRESS 8


// Min and Max Frequency to be detected
#define MIN_FREQUENCY_LIMIT 30.0f
#define MAX_FREQUENCY_LIMIT 100.0f

#include "sig_sample.h"
#include "sig_adc.h"
#include "sig_pins.h"
#include "sig_calibrate.h"
#include "sig_resample.h"

#endif // MAIN_H
