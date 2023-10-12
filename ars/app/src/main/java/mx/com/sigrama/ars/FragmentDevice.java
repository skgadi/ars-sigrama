package mx.com.sigrama.ars;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.sigrama.ars.databinding.FragmentDeviceBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDevice#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDevice extends Fragment {

    private FragmentDeviceBinding binder;
    public FragmentDevice() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binder = FragmentDeviceBinding.inflate(inflater, container, false);
        View view = binder.getRoot();
        return view;
    }
}