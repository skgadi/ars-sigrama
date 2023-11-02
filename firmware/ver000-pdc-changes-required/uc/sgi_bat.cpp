#include "sgi_include_all.h"

SGI_BAT::SGI_BAT() {
  setup();
}

void SGI_BAT::setup() {
  battery = new SFE_MAX1704X(MAX1704X_MAX17048);
  battery->begin();
  //battery->quickStart();
  lastUpdate = 0;




  ip5306 = new IP5306(PIN_SDA, PIN_SCL);

  //set battery voltage
  ip5306->set_battery_voltage(BATT_VOLTAGE_0);   //4.2V

  //set charging complete current
  ip5306->end_charge_current(0);
  
  //set cutoff voltage
  ip5306->set_charging_stop_voltage(CUT_OFF_VOLTAGE_3);    // 4.2/4.305/4.35/4.395  V   
  
  //set light load shutdown time
  ip5306->set_light_load_shutdown_time(SHUTDOWN_64s);      //64s

  //enable low battery shutdown mode
  ip5306->low_battery_shutdown(ENABLE);

  //allow boost even after removing Vin
  ip5306->boost_after_vin(ENABLE);

  //allow auto power on after load detection
  ip5306->power_on_load(ENABLE);

  //enable boost mode
  ip5306->boost_mode(ENABLE);

  //enable charger mode //SKGadi
  ip5306->charger_mode(ENABLE);

  //set BAT end CC Constant current
  ip5306->set_cc_loop(0);


  loop();
}

float SGI_BAT::getSOC() {
  soc = battery->getSOC();
  return soc;
}

float SGI_BAT::getVoltage() {
  voltage = battery->getVoltage();
  return voltage;
}

void SGI_BAT::loop() {
  float now = millis()/1000.0;
  if (now - lastUpdate > 5.0) {
    lastUpdate = now;
    getSOC();
    getVoltage();
    PRINTLN("Battery SOC: " + String(soc) + "%");
    PRINTLN("Battery Voltage: " + String(voltage) + "V");
    if (voltage<=(SHUT_DOWN_BATTERY_VOLTAGE_BELOW*1.0)) {
      
      ((SGI_AUTO_POWER*)SGI_GV_Apw)->shutdownNow(1);
    }
  }
}

char SGI_BAT::getBatteryStatus() {
  char out = percentage;
  isCharging() ? out = out | 1<<7 : out = out & ~(1<<7);
  PRINT("Battery OUT: ");
  PRINTLN((int)out);
  PRINT("Charging Rate: ");
  PRINTLN(battery->getChangeRate());
  return out;
}

void SGI_BAT::updateBatteryStatus() {
  percentage = round(getSOC())*1;
}

bool SGI_BAT::isCharging() {
  return ip5306->check_charging_status() > 0;
} 
