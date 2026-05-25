#ifndef MAIN_H
#define MAIN_H

#include <Arduino.h>
#include <math.h>

#include "GenericTypeDefsPlus.h"

#define PIN_LED 2  // LED pin to indicate the status
#define PIN_DCLK \
    GPIO_NUM_16  // Clock output pin to simulate the ADC clock output
#define PIN_DRDY GPIO_NUM_17  // Data ready pin to simulate the ADC
#define PIN_DOUT0 \
    GPIO_NUM_18  // Data output dataPin to simulate the ADC data output for
                 // channel 0

#define TOTAL_CHANNELS 8  // Total number of channels to simulate
#define SUPPORTED_LEAST_FREQUENCY \
    200  // The least frequency we can support with integer number of samples
         // per
// cycle at 256kSPS is 25Hz
#define THE_MAX_STACK_SIZE_PER_CHANNEL \
    ((uint32_t)(256000UL /             \
                (uint32_t)(SUPPORTED_LEAST_FREQUENCY)))  // The maximum stack
                                                         // size per channel we
                                                         // need to support for
                                                         // simulating
                                                         // frequencies down to
                                                         // 25Hz at 256kSPS

#define ADC_RESOLUTION_BITS 24  // ADC resolution in bits

#define DEFAULT_FREQUENCY 200  // Default frequency for the simulated sine wave
#define DEFAULT_EVENT_CYCLE_START \
    10  // Default cycle number where the event occurs
#define DEFAULT_EVENT_CYCLE_END 20  // Default cycle number where the event ends
#define DEFAULT_CYCLE_SIZE \
    ((uint32_t)(256000UL / \
                (uint32_t)(DEFAULT_FREQUENCY)))  // Integer number of samples
                                                 // per cycle at 256kSPS
#define DEFAULT_VOLTAGE_AMPLITUDE 1024  // Default voltage amplitude to simulate
#define DEFAULT_EVENT_VOLTAGE_AMPLITUDE \
    512  // Default voltage amplitude during event condition to simulate

class SIG_PULSES_OUT;
class SIG_SIM_LOOP;
class SIG_SIM_STACKS;

// declared in sim-loop.cpp, used in main.cpp
extern SIG_PULSES_OUT* pulsesOut;
extern SIG_SIM_STACKS* simStacks;

// declared in main.cpp
extern SIG_SIM_LOOP* simLoop;

#include "core-0.h"
#include "pulses-out.h"
#include "sim-loop.h"
#include "sim-stacks.h"

#endif  // MAIN_H