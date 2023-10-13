package mx.com.sigrama.ars.device;

import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import mx.com.sigrama.ars.MainActivity;
import mx.com.sigrama.ars.R;
import mx.com.sigrama.ars.common.GlobalSharedPreferencesForProject;
import mx.com.sigrama.ars.common.ManipulateFragmentContainerView;

public class ConnectionManager {


    private String SSID;
    private MainActivity mainActivity;
    private final String password;
    public ConnectionManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        password = mainActivity.getString(R.string.password_wifi_key);
        Observer<SharedPreferences> sharedPreferencesObserver = new Observer<SharedPreferences>() {
            @Override
            public void onChanged(SharedPreferences sharedPreferences) {
                Log.d("SKGadi", "ConnectionManager onChanged: " + sharedPreferences.getString("settings_device_id", mainActivity.getResources().getString(R.string.shared_prefs_default_device_id)));
                SSID = sharedPreferences.getString("device_id", mainActivity.getResources().getString(R.string.shared_prefs_default_device_id));
            }
        };
        mainActivity.sharedPrefs.getSharedPreferencesMutableLiveData().observe(mainActivity, sharedPreferencesObserver);



    }


    /*
    * This function is used to request for WiFi connectivity with
    * ability to perform web socket communication
     */
    public void requestForWiFiConnectivity() {
        ConnectivityManager connectivity = (ConnectivityManager)mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = connectivity.getAllNetworks();
        Network suitableNetwork = null;
        for (int i = 0; i < networks.length; i++) {
            NetworkCapabilities capabilities = connectivity.getNetworkCapabilities(networks[i]);
            //capabilities.hasCapability()
            Log.d("SKGadi", "capabilities: " + capabilities);
        }

        NetworkRequest.Builder request = new NetworkRequest.Builder();
        request.addCapability(NetworkCapabilities.NET_CAPABILITY_WIFI_P2P);
        connectivity.requestNetwork(request.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d("SKGadi", "onAvailable: " + network);
                connectivity.bindProcessToNetwork(network);
            }
        });
    }

    public void connect() {
        Log.d("SKGadi", "Requesting connection");
        Log.d("SKGadi", "SSID: " + SSID);
        Log.d("SKGadi", "Password: " + password);
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                if (isWifiPermissionGranted()
                        && isLocationPermissionGranted()
                        //&& isWriteSettingsPermissionGranted()
                        && isChangeNetworkPermissionGranted()) {
                    String SSID = this.SSID;

                    Log.d("SKGadi", "Requesting connection");
                    Log.d("SKGadi", "SSID: " + SSID);
                    Log.d("SKGadi", "Password: " + password);
                    WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                            .setSsid( SSID )
                            .setWpa2Passphrase(password)
                            .build();
                    NetworkRequest networkRequest = new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .setNetworkSpecifier(wifiNetworkSpecifier)
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                            .build();
                    ConnectivityManager connectivityManager = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            super.onAvailable(network);

                            connectivityManager.bindProcessToNetwork(network);
                            Log.e("SKGadi","onAvailable");
                        }

                        @Override
                        public void onLosing(@NonNull Network network, int maxMsToLive) {
                            super.onLosing(network, maxMsToLive);
                            Log.e("SKGadi","onLosing");
                        }

                        @Override
                        public void onLost(Network network) {
                            super.onLost(network);
                            Log.e("SKGadi", "losing active connection");
                        }

                        @Override
                        public void onUnavailable() {
                            super.onUnavailable();
                            Log.e("SKGadi","onUnavailable");
                        }
                    };
                    connectivityManager.requestNetwork(networkRequest,networkCallback);
                }
            } else {
                WifiManager wifiManager = (WifiManager)mainActivity.getSystemService(WIFI_SERVICE);
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (!wifiInfo.getSSID().equals(getRequiredWiFiSSD())) {
                    WifiConfiguration wifiConfig = getWifiConfiguration();
                    int netId = wifiManager.addNetwork(wifiConfig);
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(netId, true);
                    wifiManager.reconnect();

                }
                wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo.getNetworkId() == -1) {
                }
            }


            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            requestForWiFiConnectivity();
                        }
                    },
                    100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  WifiConfiguration getWifiConfiguration() {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = getRequiredWiFiSSD();
        wifiConfig.preSharedKey = String.format("\"%s\"", password);
        //Log.d("SKGadi", "wifiConfig.SSID: " + wifiConfig.SSID);
        //Log.d("SKGadi", "wifiConfig.preSharedKey: " + wifiConfig.preSharedKey);
        return wifiConfig;
    }
    private String getRequiredWiFiSSD() {
        return String.format("\"%s\"", this.SSID);
    }
    public void disconnect() {
    }
    private boolean isWifiPermissionGranted() {
        if (mainActivity.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 1);
            return false;
        }
    }
    public boolean isLocationPermissionGranted() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
    }
    public boolean isChangeNetworkPermissionGranted() {
        if (mainActivity.checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {

            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.CHANGE_NETWORK_STATE}, 1);
            return false;
        }
    }
    private String getConnectedWifiSSID() {
        WifiManager wifiManager = (WifiManager)mainActivity.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }
    public boolean isConnectedToRequiredWifi() {
        Log.d("SKGadi", "getConnectedWifiSSID(): " + getConnectedWifiSSID());
        return (getConnectedWifiSSID().equals(getRequiredWiFiSSD())
                || getConnectedWifiSSID().equals(this.SSID)
        );
    }

}
