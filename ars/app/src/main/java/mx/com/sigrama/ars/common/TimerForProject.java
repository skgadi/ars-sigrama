package mx.com.sigrama.ars.common;

import androidx.lifecycle.MutableLiveData;

import java.util.Timer;
import java.util.TimerTask;

public class TimerForProject {
    private final MutableLiveData<Integer> seconds;
    private final MutableLiveData<Integer> fiveSeconds;
    private final MutableLiveData<Integer> tenSeconds;
    private final MutableLiveData<Integer> thirtySeconds;
    private final MutableLiveData<Integer> oneMinute;
    private final MutableLiveData<Integer> fiveMinutes;
    private Integer secondsValue;

    public TimerForProject() {
        secondsValue = 0;
        seconds = new MutableLiveData<Integer>();
        fiveSeconds = new MutableLiveData<Integer>();
        tenSeconds = new MutableLiveData<Integer>();
        thirtySeconds = new MutableLiveData<Integer>();
        oneMinute = new MutableLiveData<Integer>();
        fiveMinutes = new MutableLiveData<Integer>();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                secondsValue++;
                seconds.postValue(secondsValue);
                if (secondsValue % 5 == 0) {
                    fiveSeconds.postValue(secondsValue);
                }
                if (secondsValue % 10 == 0) {
                    tenSeconds.postValue(secondsValue);
                }
                if (secondsValue % 30 == 0) {
                    thirtySeconds.postValue(secondsValue);
                }
                if (secondsValue % 60 == 0) {
                    oneMinute.postValue(secondsValue);
                }
                if (secondsValue % 300 == 0) {
                    fiveMinutes.postValue(secondsValue);
                }
            }
        }, 0, 1000);
    }

    public MutableLiveData<Integer> getFiveMinutes() {
        return fiveMinutes;
    }

    public MutableLiveData<Integer> getFiveSeconds() {
        return fiveSeconds;
    }

    public MutableLiveData<Integer> getSeconds() {
        return seconds;
    }

    public MutableLiveData<Integer> getOneMinute() {
        return oneMinute;
    }

    public MutableLiveData<Integer> getTenSeconds() {
        return tenSeconds;
    }

    public MutableLiveData<Integer> getThirtySeconds() {
        return thirtySeconds;
    }
}
