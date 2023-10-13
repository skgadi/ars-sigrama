package mx.com.sigrama.ars;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

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
        //Putting correct value of the device id
        binder.fragmentDeviceId.setText(
                (String) mainActivity.sharedPrefs.GetPreferences(
                        "deviceId",
                        GlobalSharedPreferencesForProject.TYPE.STRING,
                        R.string.shared_prefs_default_device_id));

        //Implementing the code to read QR code and set the value of the device id
        binder.fragmentDeviceQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC)
                        .enableAutoZoom() // available on 16.1.0 and higher
                        //.allowManualInput()
                        .build();
                GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(mainActivity,options);
                scanner.startScan().addOnSuccessListener(barcode -> {
                    binder.fragmentDeviceId.setText(barcode.getRawValue());
                    mainActivity.sharedPrefs.ApplyPreferences("deviceId", barcode.getRawValue());
                    Snackbar.make(v, getResources().getString(R.string.fragment_device_info_02) + "\n" + barcode.getRawValue(), Snackbar.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    binder.fragmentDeviceId.setText("Error");
                });
            }
        });


        return binder.getRoot();

    }
}