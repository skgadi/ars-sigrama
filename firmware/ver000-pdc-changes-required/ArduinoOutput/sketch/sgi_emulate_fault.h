#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_emulate_fault.h"
#ifndef SGI_EMULATE_FAULT_H
#define SGI_EMULATE_FAULT_H

#include "sgi_global.h"

#include "AD5245.h"



enum FAULT {
  NONE = 0,
  EARTH_FAULT_LINE_1 = 1,
  EARTH_FAULT_LINE_2 = 2,
  EARTH_FAULT_LINE_3 = 3,
  EARTH_DISCONTINUITY = 4
};

typedef struct FAULT_INFO {
  FAULT emulateFault;
  double currentPercent;
  bool applyNow = false;
  bool setNoneFirst = false;
  int timeOut = 100;
} REC_FAULT_INFO;



class SGI_EMULATE_FAULT {
  FAULT_INFO faultInfo;
  TaskHandle_t hEmulatorLoop;
  public:
    void setEmulatorPower(bool);
    void setAuxPower(bool);
    SGI_EMULATE_FAULT();
    FAULT_INFO getFaultInfo();
    void setFaultInfo(FAULT_INFO);
};


#endif