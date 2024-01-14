#include "sig_pins.h"

void SIG_PINS::setup() {
  //IO PINs
  //Turn off all the relays.
  // Relay for 3 phase power
  pinMode(PIN_SELECT_L1, PULLDOWN);
  pinMode(PIN_SELECT_L2, PULLDOWN);
  pinMode(PIN_SELECT_L1, OUTPUT);
  pinMode(PIN_SELECT_L2, OUTPUT);
  digitalWrite(PIN_SELECT_L1, LOW);
  digitalWrite(PIN_SELECT_L2, LOW);
  //Relay for earth wire discontinuty
  pinMode(PIN_RLY_SIGNAL_ED, PULLDOWN);
  pinMode(PIN_RLY_SIGNAL_ED, OUTPUT);
  digitalWrite(PIN_RLY_SIGNAL_ED, LOW);

  
  //Auto power off - Hold the pin high to keep the circuit on
  pinMode(PIN_AUTO_POWER, INPUT_PULLUP);


  //Setting ADC SPI PIN configuration
  pinMode(PIN_ADC_MISO, INPUT_PULLUP);
  pinMode(PIN_ADC_MOSI, OUTPUT);
  pinMode(PIN_ADC_SCK, OUTPUT);
  pinMode(PIN_ADC_CS, OUTPUT);

  //Setting Power pins
  pinMode(PIN_ENABLE_POWER, OUTPUT);
  pinMode(PIN_ENABLE_AUX_POWER, OUTPUT);

  




  //Aux and cable ack pins
  pinMode(PIN_AUX, INPUT);
  pinMode(PIN_CABLE_ACK, INPUT);

}