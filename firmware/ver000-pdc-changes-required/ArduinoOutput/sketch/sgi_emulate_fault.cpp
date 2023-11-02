#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_emulate_fault.cpp"
#include "sgi_include_all.h"


SGI_EMULATE_FAULT::SGI_EMULATE_FAULT() {
  xTaskCreatePinnedToCore(
             SGI_GF_FaultLoop,  /* Task function. */
             "FalutEmulator",    /* name of task. */
             10000,      /* Stack size of task */
             NULL,       /* parameter of the task */
             1,          /* priority of the task */
             &hEmulatorLoop,     /* Task handle to keep track of created task */
             0);         /* pin task to core 0 */
  
}


void SGI_EMULATE_FAULT::setEmulatorPower(bool value) {
  digitalWrite(PIN_ENABLE_POWER, value);
}

void SGI_EMULATE_FAULT::setAuxPower(bool value) {
  digitalWrite(PIN_ENABLE_AUX_POWER, value);
}

FAULT_INFO SGI_EMULATE_FAULT::getFaultInfo() {
  return faultInfo;
}

void SGI_EMULATE_FAULT::setFaultInfo(FAULT_INFO value) {
  faultInfo = value;
}


void SGI_GF_FaultLoop(void*) {
  
  //Setting up for fault
  SGI_ADC * hADC = (SGI_ADC *)SGI_GV_Adc;
  SGI_EMULATE_FAULT * hEft = (SGI_EMULATE_FAULT *)SGI_GV_EFt;
  SGI_ACK * hACK = (SGI_ACK *)SGI_GV_Ack;
  while (true) {
    //Auto power loop
    ((SGI_AUTO_POWER*)SGI_GV_Apw)->loop();

    //Fault applying loop
    FAULT_INFO faultRequest = hEft->getFaultInfo();
    if (faultRequest.applyNow) {
      faultRequest.applyNow = false;
      hEft->setFaultInfo(faultRequest);
      //Displaying summary
      PRINTLN("Applying fault");
      PRINT("Fault type: ");
      PRINTLN(faultRequest.emulateFault);
      PRINT("Current Percent: ");
      PRINTLN(faultRequest.currentPercent);
      PRINT("Time out: ");
      PRINTLN(faultRequest.timeOut);
      PRINT("Sampling time (uS): ");
      PRINTLN(hADC->getSamplingTimeUS());

      //Applying fault

      bool enableCurrentEmulator = (faultRequest.emulateFault >0) && (faultRequest.emulateFault <4);
      if (enableCurrentEmulator) {
        hEft->setEmulatorPower(true);
      }
      hEft->setAuxPower(true);
      delay(100);
      AD5245 *AD;
      if (enableCurrentEmulator) {
        AD = new AD5245 (0x2C);     //  AD0 == GND
      }
      if (enableCurrentEmulator) {
        AD->write(0);
        delay(20);
      }

      for (int i = 0; i < 4; i++) {
        hACK->setBit(i, false);
      }
      delay(10);
      switch (faultRequest.emulateFault) {
        case NONE:
          digitalWrite(PIN_SELECT_L1, LOW);
          digitalWrite(PIN_SELECT_L2, LOW);
          digitalWrite(PIN_RLY_SIGNAL_ED, LOW);
        break;
        case EARTH_FAULT_LINE_1:
          digitalWrite(PIN_SELECT_L1, HIGH);
          digitalWrite(PIN_SELECT_L2, LOW);
          digitalWrite(PIN_RLY_SIGNAL_ED, LOW);
          delay(5 + 4*TIME_IN_MS_FOR_HALF_CYCLE);
        break;
        case EARTH_FAULT_LINE_2:
          digitalWrite(PIN_SELECT_L1, LOW);
          digitalWrite(PIN_SELECT_L2, HIGH);
          digitalWrite(PIN_RLY_SIGNAL_ED, LOW);
          delay(5 + 4*TIME_IN_MS_FOR_HALF_CYCLE);
        break;
        case EARTH_FAULT_LINE_3:
          digitalWrite(PIN_SELECT_L1, HIGH);
          digitalWrite(PIN_SELECT_L2, HIGH);
          digitalWrite(PIN_RLY_SIGNAL_ED, LOW);
          delay(5 + 4*TIME_IN_MS_FOR_HALF_CYCLE);
        break;
        case EARTH_DISCONTINUITY:
          digitalWrite(PIN_SELECT_L1, LOW);
          digitalWrite(PIN_SELECT_L2, LOW);
          digitalWrite(PIN_RLY_SIGNAL_ED, HIGH);
          delay(3);//Delay for relay operation
        break;
      }


      hADC->setStartRecording();
      delay(15);//Delay for relay operation

      
      if(enableCurrentEmulator) {
        u8_t wiperPosition = static_cast<u8_t>(round(faultRequest.currentPercent *255.0/ 100.0));
        AD->write(wiperPosition);
        delayMicroseconds(700);
      }
      if (faultRequest.emulateFault!=0) {
        hACK->setBit(faultRequest.emulateFault-1, true);
      }
      delay(faultRequest.timeOut);
      if (enableCurrentEmulator) {
        AD->write(0);
        delay(1);
      }
      digitalWrite(PIN_SELECT_L1, LOW);
      digitalWrite(PIN_SELECT_L2, LOW);
      digitalWrite(PIN_RLY_SIGNAL_ED, LOW);
      for (int i = 0; i < 4; i++) {
        hACK->setBit(i, false);
      }
      hEft->setEmulatorPower(false);
    }
    delay(1);
  }
}
