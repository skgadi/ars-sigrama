package mx.com.sigrama.ars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.sidesheet.SideSheetDialog;

import mx.com.sigrama.ars.common.GlobalSharedPreferencesForProject;
import mx.com.sigrama.ars.common.ManipulateFragmentContainerView;

public class MainActivity extends AppCompatActivity {

    private SideSheetDialog sideSheetForMenu;
    public GlobalSharedPreferencesForProject sharedPrefs;
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
        new ManipulateFragmentContainerView(
                ManipulateFragmentContainerView.MANIPULATION.SHOW,
                getSupportFragmentManager(),
                R.id.main_activity_fragment_container_view,
                "mx.com.sigrama.ars.MainScreen");


        sharedPrefs = new GlobalSharedPreferencesForProject(this);

        //Menu button functionality
        View tempMainMenuView = getLayoutInflater().inflate(R.layout.main_menu,null);
        NavigationView mainMenuNavigationView = tempMainMenuView.findViewById(R.id.main_menu_navigationView);
        mainMenuNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return handleMainMenuButton(item);
            }
        });


        sideSheetForMenu = new SideSheetDialog(this);
        sideSheetForMenu.setContentView(tempMainMenuView);
        //sideSheetForMenu.setContentView(R.layout.main_menu);

        /*PopupMenu mainMenu = new PopupMenu(this, null);
        mainMenu.getMenuInflater().inflate(R.menu.main_menu, mainMenu.getMenu());*/
        findViewById(R.id.main_menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sideSheetForMenu.show();
            }
        });
    }


    private boolean handleMainMenuButton (@NonNull MenuItem item) {
        Log.d("SKGadi", "MenuItem: "+ item.getItemId());
        if (item.getItemId() == R.id.main_menu_device_configure) {
            new ManipulateFragmentContainerView(
                    ManipulateFragmentContainerView.MANIPULATION.SHOW,
                    getSupportFragmentManager(),
                    R.id.main_screen_fragment_container_view,
                    "mx.com.sigrama.ars.FragmentDevice",
                    true);
        }
        sideSheetForMenu.hide();
        return false;
    }

}