#include "sim-stacks.h"

int32_t stack_0[TOTAL_CHANNELS *
                THE_MAX_STACK_SIZE_PER_CHANNEL];  // Stack-0 for wave during
                                                  // normal condition for 8
                                                  // channels
int32_t stack_1[TOTAL_CHANNELS *
                THE_MAX_STACK_SIZE_PER_CHANNEL];  // Stack-1 for wave
                                                  // during event condition
                                                  // for 8 channels

SIG_SIM_STACKS::SIG_SIM_STACKS() {
    // Initialize stacks with some values (for simulation purposes)
    int32_t frequency = DEFAULT_FREQUENCY;
    for (int i = 0; i < stackSizeFor1Cycle; i++) {
        for (int channel = 0; channel < TOTAL_CHANNELS; channel++) {
            stack_0[i * TOTAL_CHANNELS + channel] = lroundf(
                sin(i * frequency * 2 * M_PI / (stackSizeFor1Cycle * 1.0f)) *
                DEFAULT_VOLTAGE_AMPLITUDE);  // Simulated sine wave data for
                                             // normal condition
            stack_1[i * TOTAL_CHANNELS + channel] = lroundf(
                sin(i * frequency * 2 * M_PI / (stackSizeFor1Cycle * 1.0f)) *
                DEFAULT_EVENT_VOLTAGE_AMPLITUDE);  // Simulated sine wave data
                                                   // for event condition
        }
    }
}

/*
 The byte is used to represent various flags
 0: output bit value (0 or 1)
 1: if it is the last bit of the current element (bit 31)
 2: if it is the last channel (channel 7)
*/
BYTE_VAL SIG_SIM_STACKS::loop_0() {
    // We have to ensure that this loop function takes same amount of time for
    // each call to simulate real-time behavior when if conditions are used both
    // branches should take same amount of time. So we will use a dummy loop to
    // equalize the time taken by both branches.
    int32_t numberToUse = 0;
    BYTE_VAL bitToOutput;
    bitToOutput.bits.b1 = (currentBit == 0);  // Mark if it's the last bit
    bitToOutput.bits.b2 = (currentChannel == lastChannelId);  // Mark if
                                                              // it's the last
                                                              // channel
    if (currentStack == 0) {
        numberToUse = stack_0[currentElement * TOTAL_CHANNELS + currentChannel];
    } else {
        numberToUse = stack_1[currentElement * TOTAL_CHANNELS + currentChannel];
    }
    bitToOutput.bits.b0 = (numberToUse >> currentBit) & 0x1;
    // Update indices for next call

    return bitToOutput;
}

void SIG_SIM_STACKS::loop_1() {
    currentBit--;
    if (currentBit < 0) {
        currentBit = 31;  // Reset to the last bit position
        currentChannel++;
    } else {
        // spend some time which takes same amount of time as the if branch
        // above to ensure real-time behavior
        __asm__ __volatile__("nop\n\tnop");
    }
    if (currentChannel >= TOTAL_CHANNELS) {
        currentChannel = 0;
        currentElement++;
    } else {
        // spend some time which takes same amount of time as the if branch
        // above to ensure real-time behavior
        __asm__ __volatile__("nop\n\tnop");
    }
    if (currentElement >= stackSizeFor1Cycle) {
        currentElement = 0;
        currentCycle++;
    } else {
        // spend some time which takes same amount of time as the if branch
        // above to ensure real-time behavior
        __asm__ __volatile__("nop\n\tnop");
    }
    if (currentCycle >= cycleWhereEventOccurs &&
        currentCycle < cycleWhereEventEnds) {
        currentStack = 1;  // Switch to event condition stack
    } else {
        currentStack = 0;  // Switch to normal condition stack
    }
}

void SIG_SIM_STACKS::displayStackInfo() {
    Serial.print("Current Stack: ");
    Serial.println(currentStack);
    Serial.print("Current Channel: ");
    Serial.println(currentChannel);
    Serial.print("Current Element: ");
    Serial.println(currentElement);
    Serial.print("Current Bit: ");
    Serial.println(currentBit);
    Serial.print("Current Cycle: ");
    Serial.println(currentCycle);
}