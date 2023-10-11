package mx.com.sigrama.ars.RealtimeDisplays;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayPowerBinding;

public class DisplayPower extends Fragment {
    private FragmentRealtimeDisplayPowerBinding binder;
    public DisplayPower() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentRealtimeDisplayPowerBinding.inflate(inflater, container, false);
        return binder.getRoot();
    }
}