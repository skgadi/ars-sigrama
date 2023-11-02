#line 1 "C:\\Suresh\\git\\probador-de-centinela\\firmware\\uc-new\\ver003-with-battery-indicator\\uc\\sgi_wifi.h"
#ifndef SGI_WIFI_H
#define SGI_WIFI_H


#include "sgi_global.h"





class SGI_WIFI {
  void setup();
  const char* password = WIFI_PASS;
  unsigned long lastActiveTime;
  void pollAllClients();
  void pingAll();
  float lastPing=-1;
  float lastPong=-1;
  WebsocketsServer server;
  std::vector<WebsocketsClient> allClients;
  void removeAllClients();
  public:
    SGI_WIFI();
    void loop();
    void updatePongTime();
    void sendPackageToClient();
};


#endif