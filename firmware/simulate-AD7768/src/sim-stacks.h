#ifndef SIM_STACKS_H
#define SIM_STACKS_H

#include "main.h"

extern int32_t
    stack_0[TOTAL_CHANNELS *
            THE_MAX_STACK_SIZE_PER_CHANNEL];  // Stack-0 for wave during
                                              // normal condition for 8
                                              // channels
extern int32_t
    stack_1[TOTAL_CHANNELS *
            THE_MAX_STACK_SIZE_PER_CHANNEL];  // Stack-1 for wave
                                              // during event condition
                                              // for 8 channels

class SIG_SIM_STACKS {
   public:
    SIG_SIM_STACKS();
    BYTE_VAL loop_0();
    void loop_1();
    void
    displayStackInfo();  // For debugging: display current stack information

   private:
    int32_t lastChannelId = TOTAL_CHANNELS - 1;

    int32_t stackSizeFor1Cycle = DEFAULT_CYCLE_SIZE;
    int32_t cycleWhereEventOccurs =
        DEFAULT_EVENT_CYCLE_START;  // cycle number where the event occurs
    int32_t cycleWhereEventEnds =
        DEFAULT_EVENT_CYCLE_END;  // cycle number where the event ends

    int32_t currentStack = 0;    // pointing index of the stack (0 or 1)
    int32_t currentChannel = 0;  // pointing index of the channel (0 to 7)
    int32_t currentElement =
        0;  // pointing index of the element in the stack (0 to 10239)
    int32_t currentBit = 31;  // Each value is 24-bit, but we will use 32-bit to
                              // store it and output bit by bit, so this is the
                              // pointing index of the bit (0 to 31)
    int32_t currentCycle = 0;  // current cycle count
};

#endif  // SIM_STACKS_H