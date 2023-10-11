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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mx.com.sigrama.ars.RealtimeDisplays.DisplayOscilloscope;
import mx.com.sigrama.ars.RealtimeDisplays.DisplayPhases;
import mx.com.sigrama.ars.RealtimeDisplays.DisplayPower;
import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayBinding;

public class RealtimeDisplay extends Fragment {

    //A binder to obtain ViewPager2
    private FragmentRealtimeDisplayBinding binder;
    private DisplayOscilloscope displayOscilloscope;
    private DisplayPhases displayPhases;
    private DisplayPower displayPower;
    //private ViewPager2 pagerViewRealtimeDisplay;

    public RealtimeDisplay() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binder = FragmentRealtimeDisplayBinding.inflate(inflater, container, false);
        View view = binder.getRoot();
        displayPhases = new DisplayPhases();
        displayOscilloscope = new DisplayOscilloscope();
        displayPower = new DisplayPower();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);
        viewPagerAdapter.addFragment(displayPhases, "Phases");
        viewPagerAdapter.addFragment(displayOscilloscope, "Oscilloscope");
        viewPagerAdapter.addFragment(displayPower, "Power");



        ViewPager viewPagerForRealtimeDisplay =  binder.viewPagerForRealtimeDisplay;

        viewPagerForRealtimeDisplay.setAdapter(viewPagerAdapter);

        //viewPagerForRealtimeDisplay.setAdapter(new RealTimeDisplayAdapterForPager());
        //pagerViewRealtimeDisplay = binder.pagerViewRealtimeDisplay;
        //pagerViewRealtimeDisplay.setAdapter(new RealtimeDisplayAdapter(this));
        //pagerViewRealtimeDisplay.setPageTransformer(new ZoomOutPageTransformer());
        //pagerViewRealtimeDisplay.setPageTransformer(new DepthPageTransformer());
        return view;
    }


    /**
     * Added by SKGadi
     * Pager adapter for RealtimeDisplay
     */
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitles = new ArrayList<>();
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }
        //add fragment to the viewpager
        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
        //to setup title of the tab layout
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
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