package mx.com.sigrama.ars.device;

import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import dev.gustavoavila.websocketclient.WebSocketClient;
import mx.com.sigrama.ars.MainActivity;

public class ManagingWebSocket {
    private final WebSocketClient webSocketClient;
    private static URI uri;
    private final MutableLiveData<byte[]> receivedData;
    private final MutableLiveData<Boolean> isConnected;
    private final MutableLiveData<String> receivedText;

    private MainActivity mainActivity;

    public ManagingWebSocket(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        String url = "ws://192.168.1.1";
        uri = URI.create(url);
        receivedData = new MutableLiveData<byte[]>();
        isConnected = new MutableLiveData<Boolean>();
        receivedText = new MutableLiveData<String>();

        isConnected.postValue(false);

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.d("SKGadi", "onOpen: ");
                isConnected.postValue(true);

                //Send a request to the device to start sending data every 5 seconds
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        sendRequestToDevice(0.0f, 0, 0);
                    }
                }, 0, 5000);

            }

            @Override
            public void onTextReceived(String message) {
                Log.d("SKGadi", "onTextReceived: " + message);
                receivedText.postValue(message);
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                Log.d("SKGadi", "onBinaryReceived: " + data);
                receivedData.postValue(data);
            }

            @Override
            public void onPingReceived(byte[] data) {
                Log.d("SKGadi", "onPingReceived: " + data);
            }

            @Override
            public void onPongReceived(byte[] data) {
                Log.d("SKGadi", "onPongReceived: " + data);
            }

            @Override
            public void onException(Exception e) {
                isConnected.postValue(false); //Is it required? Please check
                mainActivity.connectionManager.requestForWiFiConnectivity();
                //System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived(int reason, String description) {
                isConnected.postValue(false);
                Log.d("SKGadi", "WebSocket Closed");
            }

        };
        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();

    }

    public MutableLiveData<byte[]> getReceivedData() {
        return receivedData;
    }

    public MutableLiveData<Boolean> getIsConnected() {
        return isConnected;
    }

    public MutableLiveData<String> getReceivedText() {
        return receivedText;
    }

    public void sendText(String text) {
        webSocketClient.send(text);
    }

    public void sendBinary(byte[] data) {
        webSocketClient.send(data);
    }

    public void disconnect() {
        webSocketClient.close(500, 0, "Done");
    }

    private void sendRequestToDevice(float currentPercentage, int delay, int time) {
        if (isConnected.getValue()) {
            byte[] dataToSend = new byte[13];
            dataToSend[0]=0;

            //Converting float to byte array
            int currentPerentageBits =  Float.floatToIntBits(currentPercentage);
            dataToSend[1]=(byte) (currentPerentageBits);
            dataToSend[2]=(byte) (currentPerentageBits >> 8);
            dataToSend[3]=(byte) (currentPerentageBits >> 16);
            dataToSend[4]=(byte) (currentPerentageBits >> 24);

            // Converting delay  (int) to bytes
            dataToSend[5]=(byte) (delay);
            dataToSend[6]=(byte) (delay >> 8);
            dataToSend[7]=(byte) (delay >> 16);
            dataToSend[8]=(byte) (delay >> 24);


            // Converting time  (int) to bytes
            dataToSend[9]=(byte) (time);
            dataToSend[10]=(byte) (time >> 8);
            dataToSend[11]=(byte) (time >> 16);
            dataToSend[12]=(byte) (time >> 24);

            webSocketClient.send(dataToSend);
        }
    }

}
