package mx.com.sigrama.ars;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.com.sigrama.ars.common.GlobalSharedPreferencesForProject;
import mx.com.sigrama.ars.databinding.FragmentDeviceBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDevice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDevice extends Fragment {

    private FragmentDeviceBinding binder;
    private MainActivity mainActivity;
    public FragmentDevice() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binder = FragmentDeviceBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        //Putting correct value of the device id Also update when SharedPreferences are updated
        Observer<SharedPreferences> sharedPreferencesObserver = new Observer<SharedPreferences>() {
            @Override
            public void onChanged(SharedPreferences sharedPreferences) {
                binder.fragmentDeviceId.setText(sharedPreferences.getString("device_id", getResources().getString(R.string.shared_prefs_default_device_id)));
            }
        };
        mainActivity.sharedPrefs.getSharedPreferencesMutableLiveData().observe(getViewLifecycleOwner(), sharedPreferencesObserver);
        //Implementing the code to read QR code and set the value of the device id
        binder.fragmentDeviceQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC)
                        .enableAutoZoom() // available on 16.1.0 and higher
                        .build();
                GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(mainActivity,options);
                scanner.startScan().addOnSuccessListener(barcode -> {
                    if (!validateMAC(barcode.getRawValue())) {
                        Snackbar.make(v, getResources().getString(R.string.fragment_device_info_03), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    String deviceID =
                            getResources().getString(R.string.fragment_device_prefix_ssid) +
                            barcode.getRawValue().replace(":","").toUpperCase();
                    mainActivity.sharedPrefs.ApplyPreferences("device_id", deviceID);
                    Snackbar.make(v, getResources().getString(R.string.fragment_device_info_02) + "\n" + deviceID, Snackbar.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    Snackbar.make(v, getResources().getString(R.string.fragment_device_info_04), Snackbar.LENGTH_LONG).show();
                });
            }
        });

        //Implementing connect button
        binder.fragmentDeviceConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.connectionManager.connect();
            }
        });


        return binder.getRoot();

    }

    private static boolean validateMAC(String inMac) {
        try {
            Pattern pattern = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
            Matcher matcher = pattern.matcher(inMac);
            return matcher.matches();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}