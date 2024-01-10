#include "main.h"

SGI_ADC::SGI_ADC(/* args */) {
  

  
  buscfg.miso_io_num = PIN_ADC_MISO;
  buscfg.mosi_io_num = PIN_ADC_MOSI;
  buscfg.sclk_io_num = PIN_ADC_SCK;

  ret = spi_bus_initialize(SPI2_HOST, &buscfg, SPI_DMA_CH_AUTO);
  ESP_ERROR_CHECK(ret);

  
  devcfg.clock_speed_hz = SPI_MASTER_FREQ_13M;
  devcfg.mode = 0;                 // SPI mode 0
  devcfg.spics_io_num = PIN_ADC_CS;
  devcfg.queue_size = 16;
  //devcfg.flags = SPI_DEVICE_HALFDUPLEX;
  //devcfg.pre_cb = NULL;
  //devcfg.post_cb = NULL;

  ret = spi_bus_add_device(SPI2_HOST, &devcfg, &spi_handle);
  ESP_ERROR_CHECK(ret);
//
//
//
  //ESP_LOGI(TAG_FOR_ADC, "SGI_ADC object created");
}

void SGI_ADC::readChannel(int channel) {
  // Select the channel on the ADC
  uint16_t channelData = channel;
  uint16_t adcValue;

  // Create SPI transaction settings
  spi_transaction_t trans;
  memset(&trans, 0, sizeof(trans));
  trans.length = 16;
  trans.tx_buffer = &channelData;
  trans.rx_buffer = &adcValue;
  //trans.user = (void*)1; // D/C bit


  // Perform SPI transaction
  esp_err_t ret = spi_device_transmit(spi_handle, &trans);
  if (ret != ESP_OK) {
    ESP_LOGE(TAG_FOR_ADC, "SPI transaction failed. Error: %d", ret);
    return;
  }


  ESP_LOGI(TAG_FOR_ADC, "ADC-%d value is: %d", channel, adcValue);
  // Use the adcData as needed
}
  
  


