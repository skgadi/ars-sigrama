package mx.com.sigrama.ars;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainScreen extends Fragment {


    public MainScreen() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fill main screen fragment with RealtimeDisplay. It will be temp, later connect with menu
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.main_screen_fragment_container_view, new RealtimeDisplay())
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_screen, container, false);


    }
}