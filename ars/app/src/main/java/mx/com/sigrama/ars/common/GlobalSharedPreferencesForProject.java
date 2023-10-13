package mx.com.sigrama.ars.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.lifecycle.MutableLiveData;

import java.util.Set;

public class GlobalSharedPreferencesForProject {
    public enum TYPE{
        STRING,
        INT,
        FLOAT,
        BOOLEAN,
        SET_STRING
    }
    public static final String SHARED_PREFERENCES_NAME = "mx.com.sigrama.ars";
    private Context context;
    private MutableLiveData<SharedPreferences> sharedPreferencesMutableLiveData;
    private static SharedPreferences sharedPreferences;

    public GlobalSharedPreferencesForProject(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferencesMutableLiveData = new MutableLiveData<SharedPreferences>();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }

    public void ApplyPreferences(String key, String value) {
        SharedPreferences.Editor writer = sharedPreferences.edit();
        writer.putString(key, value);
        writer.apply();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }
    public void ApplyPreferences(String key, int value) {
        SharedPreferences.Editor writer = sharedPreferences.edit();
        writer.putInt(key, value);
        writer.apply();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }
    public void ApplyPreferences(String key, boolean value) {
        SharedPreferences.Editor writer = sharedPreferences.edit();
        writer.putBoolean(key, value);
        writer.apply();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }
    public void ApplyPreferences(String key, float value) {
        SharedPreferences.Editor writer = sharedPreferences.edit();
        writer.putFloat(key, value);
        writer.apply();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }
    public void ApplyPreferences(String key, Set<String> value) {
        SharedPreferences.Editor writer = sharedPreferences.edit();
        writer.putStringSet(key, value);
        writer.apply();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }
    public void ApplyPreferences(String key, long value) {
        SharedPreferences.Editor writer = sharedPreferences.edit();
        writer.putLong(key, value);
        writer.apply();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }
    public void ApplyPreferences(String key, TYPE type, Object value) {
        SharedPreferences.Editor writer = sharedPreferences.edit();
        switch (type){
            case STRING:
                writer.putString(key, (String) value);
                break;
            case INT:
                writer.putInt(key, (int) value);
                break;
            case FLOAT:
                writer.putFloat(key, (float) value);
                break;
            case BOOLEAN:
                writer.putBoolean(key, (boolean) value);
                break;
            case SET_STRING:
                writer.putStringSet(key, (Set<String>) value);
                break;
        }
        writer.apply();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }

    public static String GetPreferences(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
    public static int GetPreferences(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }
    public static boolean GetPreferences(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
    public static float GetPreferences(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }
    public static Set<String> GetPreferences(String key, Set<String> defaultValue) {
        return sharedPreferences.getStringSet(key, defaultValue);
    }
    public static long GetPreferences(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }
    public static Object GetPreferences(String key, TYPE type, Object defaultValue) {
        switch (type){
            case STRING:
                return sharedPreferences.getString(key, (String) defaultValue);
            case INT:
                return sharedPreferences.getInt(key, (int) defaultValue);
            case FLOAT:
                return sharedPreferences.getFloat(key, (float) defaultValue);
            case BOOLEAN:
                return sharedPreferences.getBoolean(key, (boolean) defaultValue);
            case SET_STRING:
                return sharedPreferences.getStringSet(key, (Set<String>) defaultValue);
        }
        return null;
    }

    public Object GetPreferences(String key, TYPE type, int defaultValue) {
        switch (type){
            case STRING:
                return sharedPreferences.getString(key, context.getResources().getString(defaultValue));
            case INT:
                return sharedPreferences.getInt(key, context.getResources().getInteger(defaultValue));
            case FLOAT:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    return sharedPreferences.getFloat(key, context.getResources().getFloat(defaultValue));
                } else {
                    return sharedPreferences.getFloat(key, context.getResources().getInteger(defaultValue));
                }
            case BOOLEAN:
                return sharedPreferences.getBoolean(key, context.getResources().getBoolean(defaultValue));
            case SET_STRING:
                String[] temp = context.getResources().getStringArray(defaultValue);
                Set<String> temp2 = Set.of(temp);
                return sharedPreferences.getStringSet(key, temp2);
        }
        return null;
    }

    public void RemovePreferences(String key) {
        SharedPreferences.Editor writer = sharedPreferences.edit();
        writer.remove(key);
        writer.apply();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }
    public void ClearPreferences() {
        SharedPreferences.Editor writer = sharedPreferences.edit();
        writer.clear();
        writer.apply();
        sharedPreferencesMutableLiveData.setValue(sharedPreferences);
    }

    public static boolean ContainsPreferences(String key) {
        return sharedPreferences.contains(key);
    }

    public static SharedPreferences GetSharedPreferences() {
        return sharedPreferences;
    }

    public  MutableLiveData<SharedPreferences> getSharedPreferencesMutableLiveData () {
        return sharedPreferencesMutableLiveData;
    }
}
