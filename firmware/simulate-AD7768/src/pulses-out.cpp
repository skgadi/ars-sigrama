#include "pulses-out.h"

SIG_PULSES_OUT::SIG_PULSES_OUT() {
    pinMode(dataPin, OUTPUT);
    pinMode(clkPin, OUTPUT);
    pinMode(drdyPin, OUTPUT);
}

void SIG_PULSES_OUT::loop_0(BYTE_VAL bitToOutput) {
    gpio_set_level(clkPin, HIGH);                  // Output the clock bit
    gpio_set_level(dataPin, bitToOutput.bits.b0);  // Output the data bit
    gpio_set_level(
        drdyPin,
        bitToOutput.bits.b1 &&
            bitToOutput.bits
                .b2);  // Turn on LED when it's the last bit of the last channel
}

void SIG_PULSES_OUT::loop_1(BYTE_VAL bitToOutput) {
    gpio_set_level(clkPin, LOW);  // Output the clock bit
}