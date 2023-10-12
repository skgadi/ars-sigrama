package mx.com.sigrama.ars.RealtimeDisplays;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayPhasesBinding;


public class DisplayPhases extends Fragment {

    private FragmentRealtimeDisplayPhasesBinding binder;

    public DisplayPhases() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentRealtimeDisplayPhasesBinding.inflate(inflater, container, false);
        return binder.getRoot();
    }

}