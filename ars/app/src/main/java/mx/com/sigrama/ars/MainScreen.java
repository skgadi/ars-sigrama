package mx.com.sigrama.ars;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.sigrama.ars.common.ManipulateFragmentContainerView;

public class MainScreen extends Fragment {


    private boolean initialVariablesInitialized = false;

    public MainScreen() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!initialVariablesInitialized) {
            initialVariablesInitialized = true;
            // Fill main screen fragment with RealtimeDisplay. It will be temp, later connect with menu
            new ManipulateFragmentContainerView(
                    ManipulateFragmentContainerView.MANIPULATION.SHOW,
                    getChildFragmentManager(),
                    R.id.main_screen_fragment_container_view,
                    "mx.com.sigrama.ars.RealtimeDisplay");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }


}