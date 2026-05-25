#include "sim-loop.h"

SIG_PULSES_OUT* pulsesOut = nullptr;
SIG_SIM_STACKS* simStacks = nullptr;

SIG_SIM_LOOP::SIG_SIM_LOOP() {
    simStacks = new SIG_SIM_STACKS();
    pulsesOut = new SIG_PULSES_OUT();
}

void SIG_SIM_LOOP::loop() {
    BYTE_VAL result = simStacks->loop_0();
    pulsesOut->loop_0(result);
    simStacks->loop_1();
    pulsesOut->loop_1(result);
}