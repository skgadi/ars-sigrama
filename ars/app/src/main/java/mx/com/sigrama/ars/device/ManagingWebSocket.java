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
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.d("SKGadi", "onOpen: ");
                isConnected.postValue(true);

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
                System.out.println(e.getMessage());
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

}
