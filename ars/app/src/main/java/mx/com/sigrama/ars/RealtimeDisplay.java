package mx.com.sigrama.ars;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.sigrama.ars.RealtimeDisplays.DisplayOscilloscope;
import mx.com.sigrama.ars.RealtimeDisplays.DisplayPhases;
import mx.com.sigrama.ars.RealtimeDisplays.DisplayPower;
import mx.com.sigrama.ars.common.ZoomOutPageTransformer;
import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayBinding;

public class RealtimeDisplay extends Fragment {

    //A binder to obtain ViewPager2
    private FragmentRealtimeDisplayBinding binder;
    private ViewPager2 pagerViewRealtimeDisplay;
    private MainActivity mainActivity;

    public RealtimeDisplay() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binder = FragmentRealtimeDisplayBinding.inflate(inflater, container, false);

        mainActivity = (MainActivity) getActivity();

        // Instantiate a ViewPager2 and a PagerAdapter.
        pagerViewRealtimeDisplay = binder.viewPagerForRealtimeDisplay;
        pagerViewRealtimeDisplay.setAdapter(new RealtimeDisplayAdapter(this));
        pagerViewRealtimeDisplay.setOffscreenPageLimit(3);
        // Set animation for ViewPager2
        //pagerViewRealtimeDisplay.setPageTransformer(new Pager2_DepthTransformer());
        pagerViewRealtimeDisplay.setPageTransformer(new ZoomOutPageTransformer());
        //pagerViewRealtimeDisplay.setPageTransformer(new DepthPageTransformer());


        // Enabling and disabling the device activity indicator
        mainActivity.managingWebSocket.getIsConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d("SKGadi", "onChanged: " + aBoolean);
                if (aBoolean) {
                    binder.fragmentRealtimeDisplayActivityIndicator.setVisibility(View.VISIBLE);
                } else {
                    binder.fragmentRealtimeDisplayActivityIndicator.setVisibility(View.INVISIBLE);
                }
                /*if (aBoolean) {
                    binder.fragmentRealtimeDisplayActivityIndicator.animate().alpha(1).setDuration(500);
                } else {
                    binder.fragmentRealtimeDisplayActivityIndicator.animate().alpha(0).setDuration(500);
                }*/
                //binder.fragmentRealtimeDisplayActivityIndicator.animate().alpha(0).setDuration(1000);
            }
        });

        // Device activity indicator shows a blink when data received
        mainActivity.signalConditioningAndProcessing.getIsDataProcessingSuccessful().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                setIndicatorStateView(aBoolean);
            }
        });

        binder.fragmentRealtimeDisplayActivityIndicator.setEnabled(false);


        View view = binder.getRoot();
        return view;
    }

    /**
     * Added by SKGadi
     * A simple pager adapter that represents the ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class RealtimeDisplayAdapter extends FragmentStateAdapter {
        public RealtimeDisplayAdapter(Fragment fragment) {
            super(fragment);
        }

        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            //Log.d("SKGadi", "createFragment: " + position);
            switch (position) {
                case 0:
                    fragment = new DisplayPhases();
                    break;
                case 1:
                    fragment = new DisplayOscilloscope();
                    break;
                case 2:
                    fragment = new DisplayPower();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + position);
            }
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }


    ColorStateList normalColoState = new ColorStateList(
            new int[][]{
                    new int[]{-android.R.attr.state_enabled}, //disabled
                    new int[]{android.R.attr.state_enabled}, //enabled
                    new int[]{-android.R.attr.state_checked}, //disabled
                    new int[]{android.R.attr.state_checked} //enabled
            },
            new int[] {
                    Color.LTGRAY, //disabled
                    Color.GREEN, //enabled
                    Color.GREEN, //disabled
                    Color.GREEN //enabled
            }
    );

    ColorStateList errorColorState = new ColorStateList(
            new int[][]{
                    new int[]{-android.R.attr.state_enabled}, //disabled
                    new int[]{android.R.attr.state_enabled}, //enabled
            },
            new int[] {
                    Color.RED, //disabled
                    Color.RED, //enabled
            }
    );


    private void setIndicatorStateView (boolean isSuccessful) {
        if (isSuccessful) {
            binder.fragmentRealtimeDisplayActivityIndicator.setButtonTintList(normalColoState);
            binder.fragmentRealtimeDisplayActivityIndicator.setEnabled(true);
            binder.fragmentRealtimeDisplayActivityIndicator.setChecked(true);
            binder.fragmentRealtimeDisplayActivityIndicator.setBackgroundColor(Color.GREEN);
        } else {
            binder.fragmentRealtimeDisplayActivityIndicator.setButtonTintList(errorColorState);
            binder.fragmentRealtimeDisplayActivityIndicator.setEnabled(true);
            binder.fragmentRealtimeDisplayActivityIndicator.setChecked(true);
            binder.fragmentRealtimeDisplayActivityIndicator.setBackgroundColor(Color.RED);
        }
        //Returns to unchecked after 0.5 seconds
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
                        binder.fragmentRealtimeDisplayActivityIndicator.setChecked(false);
                    }
                },
                500);
    }

}