package mx.com.sigrama.ars.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import mx.com.sigrama.ars.MainScreen;
import mx.com.sigrama.ars.R;

public class fragmentContainerAddOrShow {
    public fragmentContainerAddOrShow(FragmentManager fm, int id, String fragmentName) {
        if (fm.findFragmentById(id) == null) {
            try {
                Fragment newFragment = (Fragment) (Class.forName(fragmentName).newInstance());
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
    }
}
