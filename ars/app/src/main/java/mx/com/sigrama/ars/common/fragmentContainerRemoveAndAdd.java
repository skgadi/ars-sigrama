package mx.com.sigrama.ars.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import mx.com.sigrama.ars.MainScreen;
import mx.com.sigrama.ars.R;

public class fragmentContainerRemoveAndAdd {
    public fragmentContainerRemoveAndAdd(FragmentManager fm, int id, String fragmentName) {
        if (fm.findFragmentById(id) != null) {
            fm.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(fm.findFragmentById(id))
                    .commit();
        }
        try {
            Fragment newFragment = (Fragment) (Class.forName(fragmentName).newInstance());
            fm.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(id, newFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
