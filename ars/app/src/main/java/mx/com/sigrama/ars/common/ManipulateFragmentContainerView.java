package mx.com.sigrama.ars.common;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import mx.com.sigrama.ars.R;

public class ManipulateFragmentContainerView {
    public ManipulateFragmentContainerView(MANIPULATION type, FragmentManager fm, int id, String fragmentToShow) {
        new ManipulateFragmentContainerView(type, fm, id, fragmentToShow, false);
    }
    public ManipulateFragmentContainerView(MANIPULATION type, FragmentManager fm, int id, String fragmentToShow, boolean addToBackStack) {
        new ManipulateFragmentContainerView(type, fm, id, fragmentToShow, addToBackStack, true);
    }
    public ManipulateFragmentContainerView(MANIPULATION type, FragmentManager fm, int id, String fragmentToShow, boolean addToBackStack, boolean useAnimations) {
        if (fm == null) {
            return;
        }
        boolean doesFragmentExist = (fm.findFragmentById(id) != null);
        //Log.d("SKGadi", "doesFragmentExist: " + doesFragmentExist);
        FragmentTransaction fragmentTransaction=  fm.beginTransaction();
        if (useAnimations) {
            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            );
        }
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        switch (type) {
            case SHOW:
                if (doesFragmentExist) {
                    fragmentTransaction.show(Objects.requireNonNull(fm.findFragmentById(id)));
                } else {
                    try {
                        Fragment newFragment = (Fragment) (Class.forName(fragmentToShow).newInstance());
                        fragmentTransaction.add(id, newFragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REMOVE_AND_ADD:
                if (doesFragmentExist) {
                    fragmentTransaction.remove(Objects.requireNonNull(fm.findFragmentById(id)));
                }
                try {
                    Fragment newFragment = (Fragment) (Class.forName(fragmentToShow).newInstance());
                    fragmentTransaction.add(id, newFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case SHOW_ONLY_IF_EXISTS:
                if (doesFragmentExist) {
                    fragmentTransaction.show(Objects.requireNonNull(fm.findFragmentById(id)));
                }
                break;
            case REMOVE:
                if (doesFragmentExist) {
                    fragmentTransaction.remove(Objects.requireNonNull(fm.findFragmentById(id)));
                }
                break;
        }
        fragmentTransaction.commit();

    }
    public enum MANIPULATION {
        SHOW,
        REMOVE_AND_ADD,
        SHOW_ONLY_IF_EXISTS,
        REMOVE
    }
}
