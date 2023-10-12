package mx.com.sigrama.ars.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class ManipulateFragmentContainerView {
    public ManipulateFragmentContainerView(MANIPULATION type, FragmentManager fm, int id, String fragmentToShow) {
        switch (type) {
            case ADD_ONLY_IF_NOT_EXISTS:
                if (fm.findFragmentById(id) == null) {
                    try {
                        Fragment newFragment = (Fragment) (Class.forName(fragmentToShow).newInstance());
                        fm.beginTransaction()
                                .setReorderingAllowed(true)
                                .add(id, newFragment)
                                .commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    fm.beginTransaction()
                            .setReorderingAllowed(true)
                            .show(fm.findFragmentById(id))
                            .commit();
                }
                break;
            case REMOVE_AND_ADD:
                if (fm.findFragmentById(id) != null) {
                    fm.beginTransaction()
                            .setReorderingAllowed(true)
                            .remove(fm.findFragmentById(id))
                            .commit();
                }
                try {
                    Fragment newFragment = (Fragment) (Class.forName(fragmentToShow).newInstance());
                    fm.beginTransaction()
                            .setReorderingAllowed(true)
                            .add(id, newFragment)
                            .commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case SHOW_ONLY_IF_EXISTS:
                if (fm.findFragmentById(id) != null) {
                    fm.beginTransaction()
                            .setReorderingAllowed(true)
                            .show(fm.findFragmentById(id))
                            .commit();
                }
                break;
        }
    }
    public enum MANIPULATION {
        ADD_ONLY_IF_NOT_EXISTS,
        REMOVE_AND_ADD,
        SHOW_ONLY_IF_EXISTS,
    }
}
