#include "sig_esp_now.h"



void onEspNowDataRecv(const uint8_t * mac, const uint8_t *incomingData, int len) {
  sdCard.appendToSDCard((float *)incomingData, len/sizeof(float));
  if (len<200) {
    Serial.print(".");
  }
}

void SIG_ESP_NOW::setup() {

  // fill broadcastAddress with broadcast MAC address C4:DE:E2:D3:12:2C
  broadcastAddress[0] = 0xC4;
  broadcastAddress[1] = 0xDE;
  broadcastAddress[2] = 0xE2;
  broadcastAddress[3] = 0xD3;
  broadcastAddress[4] = 0x12;
  broadcastAddress[5] = 0x2C;
  broadcastAddress[6] = 0x00;

  // Init ESP-NOW
  if (esp_now_init() != ESP_OK) {
    Serial.println("Error initializing ESP-NOW");
    return;
  }
  // Register peer
  memcpy(peerInfo.peer_addr, broadcastAddress, 6);
  peerInfo.channel = 0;
  peerInfo.encrypt = false;
  if (esp_now_add_peer(&peerInfo) != ESP_OK) {
    Serial.println("Failed to add peer");
    return;
  }

  
  esp_err_t result = esp_now_register_recv_cb(onEspNowDataRecv);
  Serial.print("Setup ESP-NOW: ");
  Serial.println(result == ESP_OK ? "Success" : "Fail");


}