package mx.com.sigrama.ars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.sidesheet.SideSheetBehavior;
import com.google.android.material.sidesheet.SideSheetDialog;

import mx.com.sigrama.ars.common.ManipulateFragmentContainerView;
import mx.com.sigrama.ars.common.fragmentContainerAddOrShow;

public class MainActivity extends AppCompatActivity {

    private SideSheetDialog sideSheetForMenu;
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
                ManipulateFragmentContainerView.MANIPULATION.ADD_ONLY_IF_NOT_EXISTS,
                getSupportFragmentManager(),
                R.id.main_activity_fragment_container_view,
                "mx.com.sigrama.ars.MainScreen");

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
        switch (item.getItemId()) {
            case :
                new ManipulateFragmentContainerView(
                        ManipulateFragmentContainerView.MANIPULATION.REMOVE_AND_ADD,
                        getSupportFragmentManager(),
                        R.id.main_activity_fragment_container_view,
                        "mx.com.sigrama.ars.RealtimeDisplay");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item);
        }
        Log.d("SKGadi", "MenuItem: "+ item.getItemId());
        return false;
    }

}