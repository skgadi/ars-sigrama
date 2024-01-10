#ifndef SGI_ADC_H
#define SGI_ADC_H

#include "main.h"
#include "driver/spi_master.h"
#include <string.h>

class SGI_ADC {
  int adcMosiPin = PIN_ADC_MOSI;
  int adcMisoPin = PIN_ADC_MISO;
  int adcSckPin = PIN_ADC_SCK;
  int adcCsPin = PIN_ADC_CS;
  spi_device_handle_t spi_handle;
  esp_err_t ret;
  spi_bus_config_t buscfg;
  spi_device_interface_config_t devcfg;
  public:
    SGI_ADC();
    void readChannel(int channel);
};

#endif // SGI_ADC_H
