#ifndef SIG_ESP_NOW_H
#define SIG_ESP_NOW_H

#include "main.h"

#include <esp_now.h>
#include <WiFi.h>


typedef struct SYSTEM_STATE_0 {
  union {
    float waveform[TOTAL_RESAMPLES];
    float waveformChannels[RESAMPLE_SIZE][NO_OF_CHANNELS];
  };
  union {
    float harmonics[TOTAL_RESAMPLES];
    union {
      float SPECTRUM_AMPLITUDE[TOTAL_RESAMPLES/2];
      float SPECTRUM_CHANNELS_AMPLITUDE[RESAMPLE_SIZE/2][NO_OF_CHANNELS];
    };
    union {
      float SPECTRUM_PHASE[TOTAL_RESAMPLES/2];
      float SPECTRUM_CHANNELS_PHASE[RESAMPLE_SIZE/2][NO_OF_CHANNELS];
    };
  };
  float energyActive[NO_OF_CHANNELS/2];
  float energyReactive[NO_OF_CHANNELS/2];
  float energyApparent[NO_OF_CHANNELS/2];
  float fundamentalFrequency;
} SYSTEM_STATE_0;

class SIG_ESP_NOW {
  SYSTEM_STATE_0 systemState_0;
  
  esp_now_peer_info_t peerInfo;
  uint8_t broadcastAddress[7];// = {0x7C, 0x9E, 0xBD, 0xF4, 0xF6, 0x60}; // Broadcast Address 7C:9E:BD:F4:F6:60
  public:
    void setup();
    bool sendSystemState();
    bool prepareSystemState();
};



extern SIG_ESP_NOW sig_esp_now;


#endif // SIG_ESP_NOW_H
