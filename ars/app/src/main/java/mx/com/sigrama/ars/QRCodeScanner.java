package mx.com.sigrama.ars;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mx.com.sigrama.ars.common.ManipulateFragmentContainerView;


public class QRCodeScanner extends Fragment {
    private CodeScanner mCodeScanner;
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_q_r_code_scanner, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setAutoFocusMode(AutoFocusMode.CONTINUOUS);
        //mCodeScanner.setFlashEnabled(true);
        mCodeScanner.setScanMode(ScanMode.SINGLE); // SINGLE or CONTINUOUS or PREVIEW

        mainActivity = (MainActivity) getActivity();



        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                new ManipulateFragmentContainerView(
                        ManipulateFragmentContainerView.MANIPULATION.REMOVE,
                        getChildFragmentManager(),
                        R.id.fragment_device_container_for_qr_scanner,
                        "mx.com.sigrama.ars.QRCodeScanner",
                        false,
                        false);
                View parentLayout = mainActivity.findViewById(android.R.id.content);
                if (!validateMAC(result.getText())) {
                    Snackbar.make(parentLayout, getResources().getString(R.string.fragment_device_info_03), Snackbar.LENGTH_LONG).show();
                    return;
                }
                String deviceID =
                        getResources().getString(R.string.fragment_device_prefix_ssid) +
                                result.getText().replace(":","").toUpperCase();
                mainActivity.sharedPrefs.ApplyPreferences("device_id", deviceID);
                Snackbar.make(parentLayout, getResources().getString(R.string.fragment_device_info_02) + "\n" + deviceID, Snackbar.LENGTH_LONG).show();
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }


    /*
        * This function is used to validate the MAC address
     */
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