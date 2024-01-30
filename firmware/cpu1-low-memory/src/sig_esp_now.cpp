#include "sig_esp_now.h"



/*void onESPNowDataSent(const uint8_t *mac_addr, esp_now_send_status_t status) {
  Serial.print("\r\nLast Packet Send Status:\t");
  Serial.println(status == ESP_NOW_SEND_SUCCESS ? "Delivery Success" : "Delivery Fail");
}*/

void SIG_ESP_NOW::setup() {

  /*WiFi.mode(WIFI_MODE_STA);
  Serial.println(WiFi.macAddress());//C4:DE:E2:D3:12:2C*/


  // set the broadcastAddress as 7C:9E:BD:F4:F6:60
  broadcastAddress[0] = 0x7C;
  broadcastAddress[1] = 0x9E;
  broadcastAddress[2] = 0xBD;
  broadcastAddress[3] = 0xF4;
  broadcastAddress[4] = 0xF6;
  broadcastAddress[5] = 0x60;
  broadcastAddress[6] = 0x00;
  // Init ESP Now
  WiFi.mode(WIFI_STA);
  if (esp_now_init() != ESP_OK) {
    Serial.println("Error initializing ESP-NOW");
    return;
  }
  
  // Register peer
  memcpy(peerInfo.peer_addr, broadcastAddress, 6);
  peerInfo.channel = 0;  
  peerInfo.encrypt = false;
  
  // Add peer        
  if (esp_now_add_peer(&peerInfo) != ESP_OK){
    Serial.println("Failed to add peer");
    return;
  }
  //esp_err_t result = esp_now_register_send_cb(onESPNowDataSent);
  Serial.print("Setup ESP-NOW: ");
  //Serial.println(result == ESP_OK ? "Success" : "Fail");


  
}

bool SIG_ESP_NOW::sendSystemState() {
  // Send message via ESP-NOW

  int packetSize = sizeof(SYSTEM_STATE_0);
  int numberOfFullIterations = packetSize/sizeof(float)/50;
  int numberOfBytesLeft = packetSize - numberOfFullIterations*50*sizeof(float);

  for (int i = 0; i < numberOfFullIterations; i++) {
    esp_err_t result = esp_now_send(broadcastAddress, ((uint8_t *) &systemState_0) + i, 50*sizeof(float));
    if (result != ESP_OK) {
      return false;
    }
  }
  esp_err_t result = esp_now_send(broadcastAddress, ((uint8_t *) &systemState_0) + numberOfFullIterations, numberOfBytesLeft);
  if (result != ESP_OK) {
    return false;
  }
  Serial.print("Sent ");
  Serial.print(packetSize);
  Serial.print("; iterations: ");
  Serial.print(numberOfFullIterations);
  Serial.print("; bytes left: ");
  Serial.print(numberOfBytesLeft);
  Serial.print("; result: ");
  Serial.println("Sent successfully.");
  return true;
}

bool SIG_ESP_NOW::prepareSystemState() {
  memcpy(systemState_0.waveform, resample.getSamplePointer(), RESAMPLE_SIZE * NO_OF_CHANNELS * sizeof(float));
  memcpy(systemState_0.harmonics, fft.getSamplePointer(), RESAMPLE_SIZE * NO_OF_CHANNELS * sizeof(float));
  memcpy(systemState_0.energyActive, power.getEnergyActivePointer(), NO_OF_CHANNELS/2 * sizeof(float));
  memcpy(systemState_0.energyReactive, power.getEnergyReactivePointer(), NO_OF_CHANNELS/2 * sizeof(float));
  memcpy(systemState_0.energyApparent, power.getEnergyApparentPointer(), NO_OF_CHANNELS/2 * sizeof(float));
  systemState_0.fundamentalFrequency = sample.getFrequency();
  return true;
}

