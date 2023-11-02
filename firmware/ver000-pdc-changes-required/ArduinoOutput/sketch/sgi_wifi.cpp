#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_wifi.cpp"
#include "sgi_include_all.h"

SGI_WIFI::SGI_WIFI() {
  setup();
}

void SGI_WIFI::setup() {
  // Set device as a Wi-Fi Station
  WiFi.mode(WIFI_STA);

  if(!MDNS.begin("sigrama")) {
    PRINTLN("Error starting mDNS");
    return;
  }
  IPAddress local_ip(192, 168, 1, 1);
  IPAddress gateway(192, 168, 1, 1);
  IPAddress subnet(255, 255, 255, 0);
  uint8_t mac[6];
  esp_read_mac(mac, ESP_MAC_WIFI_STA);
  char ssid_gen[21];
  sprintf(ssid_gen, "PCS-2.0-%02X%02X%02X%02X%02X%02X\0", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5]);
  
  WiFi.softAP(ssid_gen, password);
  WiFi.softAPConfig(local_ip, gateway, subnet);
  delay(100);

  server.listen(80);
  PRINTLN("HTTP server started");
}

void SGI_WIFI::loop() {
  if (server.poll()) {
    PRINTLN("Found a new request");
    WebsocketsClient client = server.accept();
    // Removes previous connections
    removeAllClients();
    // adds new connections
    client.onEvent(SGI_GF_wifiOnEvent);
    client.onMessage(SGI_GF_onWiFiMessage);
    
    allClients.push_back(client);

    updatePongTime();
    ((SGI_DISPLAY*)SGI_GV_Dsp)->signalConnected();
  }
  pollAllClients();
  pingAll();
}

void SGI_WIFI::pollAllClients() {
  for (auto& client : allClients) {
    client.poll();
  }
}

void SGI_WIFI::pingAll() {
  float newPing = millis()/1000.0;
  if (abs(newPing - lastPing) > CHECK_PING_EVERY_x_SECONDS) {
    lastPing = newPing;
    for (auto& client : allClients) {
      if (!client.ping()) {
        //((SGI_DISPLAY*)SGI_GV_Dsp)->reset();
        // Removes previous connections
        removeAllClients();
        ((SGI_DISPLAY*)SGI_GV_Dsp)->signalDisconnected();
      } else {
        PRINTLN("Pinged");
      }
    }
    if (abs(lastPong-lastPing) > (CHECK_PING_EVERY_x_SECONDS * 2.0)) {
      // Removes previous connections
      removeAllClients();
      ((SGI_DISPLAY*)SGI_GV_Dsp)->signalDisconnected();

      //((SGI_DISPLAY*)SGI_GV_Dsp)->reset();
    }
  }
}

void SGI_WIFI::updatePongTime() {
  lastPong = millis()/1000.0;
}

void SGI_WIFI::removeAllClients() {
  // Removes previous connections
  if (allClients.size() > 0) {
    allClients.front().close(CloseReason_GoingAway);
    allClients.erase(allClients.begin());
  }
}

void SGI_WIFI::sendPackageToClient() {
  PRINT("Package size: ");
  PRINTLN(((SGI_ADC*)SGI_GV_Adc)->getPackageSize());
  allClients.at(0).sendBinary(((SGI_ADC*)SGI_GV_Adc)->getPackage(), ((SGI_ADC*)SGI_GV_Adc)->getPackageSize());
  ((SGI_DISPLAY*)SGI_GV_Dsp)->signalSentPacket();
}


void SGI_GF_wifiOnEvent (WebsocketsClient& client, WebsocketsEvent evt, WSInterfaceString interString) {
  switch(evt) {
    case WebsocketsEvent::ConnectionClosed:
      PRINTLN("ConnectionClosed");
      //((SGI_DISPLAY*)SGI_GV_Dsp)->signalDisconnected();
      break;
    case WebsocketsEvent::ConnectionOpened :
      PRINTLN("ConnectionOpened");
      //((SGI_DISPLAY*)SGI_GV_Dsp)->signalConnected();
      break;
    case WebsocketsEvent::GotPing:
      PRINTLN("GotPing");
      break;
    case WebsocketsEvent::GotPong:
      ((SGI_WIFI*)SGI_GV_Wfi)->updatePongTime();
      PRINTLN("GotPong");
      break;
  }
}


void SGI_GF_onWiFiMessage(WebsocketsClient& client, WebsocketsMessage message) {
  ((SGI_WIFI*)SGI_GV_Wfi)->updatePongTime();
  PRINT("WebSocket received message of size: ");
  PRINTLN(message.rawData().length());
  int dataSize = message.rawData().size();
  char RecData[dataSize];
  message.rawData().copy(RecData,dataSize,0);
  if (RecData[0]>=0 && RecData[0]<5) {
    FAULT_INFO faultRequest;

    faultRequest.emulateFault = static_cast<FAULT>(RecData[0]);
    if (faultRequest.emulateFault != NONE) {
      ((SGI_AUTO_POWER*)SGI_GV_Apw)->resetShutdownTime();
    }
    uint32_t samplingTime;
    memcpy(&samplingTime, RecData+5, 4);
    ((SGI_ADC*)SGI_GV_Adc)->setSamplingTimeUS(samplingTime);

    memcpy(&(faultRequest.timeOut), RecData+9, 4);

    float currentPercent;
    memcpy(&currentPercent, RecData+1, 4);
    faultRequest.currentPercent = currentPercent;


    //sendPackageToClient(client);
    faultRequest.applyNow = true;

    //update the battery information
    ((SGI_BAT*)SGI_GV_Bat)->updateBatteryStatus();

    ((SGI_EMULATE_FAULT*)SGI_GV_EFt)->setFaultInfo(faultRequest);
    delay(1);
    ((SGI_ADC*)SGI_GV_Adc)->preparePackage(faultRequest.emulateFault);
    ((SGI_WIFI*)SGI_GV_Wfi)->sendPackageToClient();
    return;
  } else if (RecData[0] == 5) {
    SGI_ON_BOARD_STORAGE calData;
    float tempFloat;
    int inIdx = 1;
    for (int i=0; i<4; i++) {
      memcpy(&tempFloat, RecData + inIdx, 4);
      inIdx += 4;
      calData.gains[i].Val = tempFloat;
      memcpy(&tempFloat, RecData + inIdx, 4);
      inIdx += 4;
      calData.offsets[i].Val = tempFloat;
    }
    LONGLONG tempTime;
    memcpy(&tempTime, RecData + inIdx, 8);
    calData.timeStamp.Val = tempTime;
    ((SGI_EEPROM*)SGI_GV_Eep)->updateData(calData);
  }
  /*
  if (dataSize == LCD_COLS*LCD_ROWS) {
    displayContentToLCD(message.c_str());
  }
  */

}
