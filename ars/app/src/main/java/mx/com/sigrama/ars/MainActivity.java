package mx.com.sigrama.ars;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.sidesheet.SideSheetBehavior;
import com.google.android.material.sidesheet.SideSheetDialog;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Editing starts here
        //Making full screen --> Fullscreen is done through themes
        /*Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
            window.setNavigationBarContrastEnforced(false);
        }
        //window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);*/

        // Fill main screen with fragment
        if (getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_container_view) == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_activity_fragment_container_view, new MainScreen())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .show(getSupportFragmentManager().findFragmentById(R.id.main_activity_fragment_container_view))
                    .commit();
        }

        //Menu button functionality
        SideSheetDialog sideSheetForMenu = new SideSheetDialog(this);
        sideSheetForMenu.setContentView(R.layout.main_menu);




        /*NavigationView mainMenuNavigationView = new NavigationView(this);
        mainMenuNavigationView.*/

        //navigationView = findViewById(R.id.navigationView1);
        Log.d("SKGadi", "onCreate: " + navigationView);
        findViewById(R.id.main_menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sideSheetForMenu.show();
            }
        });
    }


}