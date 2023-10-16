package mx.com.sigrama.ars;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codeboy.pager2_transformers.Pager2_AccordionTransformer;
import com.codeboy.pager2_transformers.Pager2_CubeInDepthTransformer;
import com.codeboy.pager2_transformers.Pager2_CubeOutScalingTransformer;
import com.codeboy.pager2_transformers.Pager2_DefaultTransformer;
import com.codeboy.pager2_transformers.Pager2_DepthTransformer;
import com.codeboy.pager2_transformers.Pager2_GateTransformer;

import java.util.ArrayList;
import java.util.List;

import mx.com.sigrama.ars.RealtimeDisplays.DisplayOscilloscope;
import mx.com.sigrama.ars.RealtimeDisplays.DisplayPhases;
import mx.com.sigrama.ars.RealtimeDisplays.DisplayPower;
import mx.com.sigrama.ars.common.DepthPageTransformer;
import mx.com.sigrama.ars.common.ZoomOutPageTransformer;
import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayBinding;

public class RealtimeDisplay extends Fragment {

    //A binder to obtain ViewPager2
    private FragmentRealtimeDisplayBinding binder;
    private ViewPager2 pagerViewRealtimeDisplay;

    public RealtimeDisplay() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binder = FragmentRealtimeDisplayBinding.inflate(inflater, container, false);


        // Instantiate a ViewPager2 and a PagerAdapter.
        pagerViewRealtimeDisplay = binder.viewPagerForRealtimeDisplay;
        pagerViewRealtimeDisplay.setAdapter(new RealtimeDisplayAdapter(this));
        pagerViewRealtimeDisplay.setOffscreenPageLimit(3);
        // Set animation for ViewPager2
        //pagerViewRealtimeDisplay.setPageTransformer(new Pager2_DepthTransformer());
        pagerViewRealtimeDisplay.setPageTransformer(new ZoomOutPageTransformer());
        //pagerViewRealtimeDisplay.setPageTransformer(new DepthPageTransformer());




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

}