package mx.com.sigrama.ars.RealtimeDisplays;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayOscilloscopeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayOscilloscope#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayOscilloscope extends Fragment {

    private FragmentRealtimeDisplayOscilloscopeBinding binder;
    public DisplayOscilloscope() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentRealtimeDisplayOscilloscopeBinding.inflate(inflater, container, false);
        return binder.getRoot();
    }


    /**
     * Added by SKGadi
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned,
     * but before any saved state has been restored in to the view. This gives subclasses a chance
     * to initialize themselves once they know their view hierarchy has been completely created.
     * The fragment's view hierarchy is not however attached to its parent at this point.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d("SKGadi", "onViewCreated: DisplayOscilloscope");
    }
}