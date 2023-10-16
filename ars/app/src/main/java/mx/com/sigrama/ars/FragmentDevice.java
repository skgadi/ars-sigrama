package mx.com.sigrama.ars;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.sigrama.ars.common.ManipulateFragmentContainerView;
import mx.com.sigrama.ars.databinding.FragmentDeviceBinding;

public class FragmentDevice extends Fragment {

    private enum STATE {
        NORMAL,
        QR_CODE_SCANNER
    }
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
                setStateTo(STATE.NORMAL);
            }
        };
        mainActivity.sharedPrefs.getSharedPreferencesMutableLiveData().observe(getViewLifecycleOwner(), sharedPreferencesObserver);
        //Implementing the code to read QR code and set the value of the device id

        //Implementing cancel QR button
        binder.fragmentDeviceCancelQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStateTo(STATE.NORMAL);
            }
        });


        binder.fragmentDeviceQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStateTo(STATE.QR_CODE_SCANNER);
                mainActivity.requestPermissionsForTheProject();
                new ManipulateFragmentContainerView(
                        ManipulateFragmentContainerView.MANIPULATION.REMOVE_AND_ADD,
                        getChildFragmentManager(),
                        R.id.fragment_device_container_for_qr_scanner,
                        "mx.com.sigrama.ars.QRCodeScanner",
                        false,
                        false);
                /*FragmentManager fm = mainActivity.getSupportFragmentManager();
                QRCodeScanner fragment = new QRCodeScanner();
                fm.beginTransaction().add(R.id.qr_code_fragment_holder,fragment).commit();*/
            }
        });

        /*
        new ManipulateFragmentContainerView(
                ManipulateFragmentContainerView.MANIPULATION.REMOVE_AND_ADD,
                getChildFragmentManager(),
                R.id.main_screen_fragment_container_view,
                "mx.com.sigrama.ars.QRCodeScanner");
                */

        /*
        binder.fragmentDeviceQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
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
        */

        //Implementing connect button
        binder.fragmentDeviceConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.connectionManager.connect();
            }
        });


        return binder.getRoot();

    }

    private void setStateTo (STATE state) {
        switch (state) {
            case NORMAL:
                binder.fragmentDeviceLicensesContainer.setVisibility(View.VISIBLE);
                binder.fragmentDeviceContainerForQrScanner.setVisibility(View.GONE);
                binder.fragmentDeviceQr.setVisibility(View.VISIBLE);
                binder.fragmentDeviceCancelQr.setVisibility(View.GONE);
                new ManipulateFragmentContainerView(
                        ManipulateFragmentContainerView.MANIPULATION.REMOVE,
                        getChildFragmentManager(),
                        R.id.fragment_device_container_for_qr_scanner,
                        "mx.com.sigrama.ars.QRCodeScanner",
                        false,
                        false);
                break;
            case QR_CODE_SCANNER:
                binder.fragmentDeviceLicensesContainer.setVisibility(View.GONE);
                binder.fragmentDeviceContainerForQrScanner.setVisibility(View.VISIBLE);
                binder.fragmentDeviceQr.setVisibility(View.GONE);
                binder.fragmentDeviceCancelQr.setVisibility(View.VISIBLE);
                break;
        }
    }

}