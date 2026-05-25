#ifndef PULSES_OUT_H
#define PULSES_OUT_H

#include "main.h"

class SIG_PULSES_OUT {
   public:
    SIG_PULSES_OUT();
    void loop_0(BYTE_VAL bitToOutput);
    void loop_1(BYTE_VAL bitToOutput);

   private:
    gpio_num_t dataPin =
        PIN_DOUT0;  // Data output dataPin to simulate the ADC data output
    gpio_num_t clkPin =
        PIN_DCLK;  // Clock output pin to simulate the ADC clock output
    gpio_num_t drdyPin =
        PIN_DRDY;  // Data ready pin to simulate the ADC data ready signal
};

#endif  // PULSES_OUT_H