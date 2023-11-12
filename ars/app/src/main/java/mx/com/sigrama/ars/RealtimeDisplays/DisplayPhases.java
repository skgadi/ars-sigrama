package mx.com.sigrama.ars.RealtimeDisplays;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

import mx.com.sigrama.ars.MainActivity;
import mx.com.sigrama.ars.common.PhasorDiagram;
import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayPhasesBinding;


public class DisplayPhases extends Fragment {

    private MainActivity mainActivity;
    private FragmentRealtimeDisplayPhasesBinding binder;

    public DisplayPhases() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentRealtimeDisplayPhasesBinding.inflate(inflater, container, false);

        // Get a reference to MainActivity
        mainActivity = (MainActivity) getActivity();

        //Update binder.fragmentRealtimeDisplayPhasesDiagram when
        //mainActivity.signalConditioningAndProcessing.getPhasorData() is available
        mainActivity.signalConditioningAndProcessing.getPhasorData().observe(getViewLifecycleOwner(), phasorData -> {
            binder.fragmentRealtimeDisplayPhasesDiagram.invalidateViewForAnimation(phasorData);
        });


        //update the binder.fragmentRealtimeDisplayPhasesDiagram every 100ms
        /*new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                binder.fragmentRealtimeDisplayPhasesDiagram.invalidateViewForAnimation(null);
                Log.d("SKGadi", "Invalidate called");
            }
        },0,5000);*/





        return binder.getRoot();
    }

}