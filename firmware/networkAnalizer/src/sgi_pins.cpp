#include "main.h"

SGI_PINS::SGI_PINS () {
  configurePins();
}


void SGI_PINS::configurePins() {
  gpio_set_direction((gpio_num_t)PIN_SELECT_L1, GPIO_MODE_OUTPUT);
  gpio_set_level((gpio_num_t)PIN_SELECT_L1, 0);
  gpio_set_direction((gpio_num_t)PIN_SELECT_L2, GPIO_MODE_OUTPUT);
  gpio_set_level((gpio_num_t)PIN_SELECT_L2, 0);

  gpio_set_direction((gpio_num_t)PIN_RLY_SIGNAL_ED, GPIO_MODE_OUTPUT);
  gpio_set_level((gpio_num_t)PIN_RLY_SIGNAL_ED, 0);

  gpio_set_direction((gpio_num_t)PIN_AUTO_POWER, GPIO_MODE_OUTPUT);
  gpio_set_level((gpio_num_t)PIN_AUTO_POWER, 1);
  
  gpio_set_direction((gpio_num_t)PIN_ADC_MISO, GPIO_MODE_INPUT);
  gpio_pullup_en((gpio_num_t)PIN_ADC_MISO);
  gpio_set_direction((gpio_num_t)PIN_ADC_MOSI, GPIO_MODE_OUTPUT);
  gpio_set_level((gpio_num_t)PIN_ADC_MOSI, 0);
  gpio_set_direction((gpio_num_t)PIN_ADC_SCK, GPIO_MODE_OUTPUT);
  gpio_set_level((gpio_num_t)PIN_ADC_SCK, 0);
  gpio_set_direction((gpio_num_t)PIN_ADC_CS, GPIO_MODE_OUTPUT);
  gpio_set_level((gpio_num_t)PIN_ADC_CS, 1);
  


}