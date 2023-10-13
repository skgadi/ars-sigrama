package mx.com.sigrama.ars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.sidesheet.SideSheetDialog;

import java.util.Timer;
import java.util.TimerTask;

import mx.com.sigrama.ars.common.GlobalSharedPreferencesForProject;
import mx.com.sigrama.ars.common.ManipulateFragmentContainerView;
import mx.com.sigrama.ars.device.ConnectionManager;
import mx.com.sigrama.ars.device.ManagingWebSocket;

public class MainActivity extends AppCompatActivity {

    private SideSheetDialog sideSheetForMenu;
    public GlobalSharedPreferencesForProject sharedPrefs;
    public ConnectionManager connectionManager;
    public ManagingWebSocket managingWebSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Fill main screen with fragment
        new ManipulateFragmentContainerView(
                ManipulateFragmentContainerView.MANIPULATION.SHOW,
                getSupportFragmentManager(),
                R.id.main_activity_fragment_container_view,
                "mx.com.sigrama.ars.MainScreen");


        //Shared preferences for the project starts here
        if (sharedPrefs == null) {
            sharedPrefs = new GlobalSharedPreferencesForProject(this);
        }
        //Shared preferences for the project ends here

        //Menu button functionality starts here
        // It generates side sheet for menu and sets up its functionality
        if (sideSheetForMenu == null) {
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
            findViewById(R.id.main_menu_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sideSheetForMenu.show();
                }
            });
        }
        //Menu button functionality ends here
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ConnectionManager starts here
        if (connectionManager == null) {
            connectionManager = new ConnectionManager(this);
        }
        //ConnectionManager ends here
        //ManagingWebSocket starts here
        if (managingWebSocket == null) {
            managingWebSocket = new ManagingWebSocket(this);
        }
        //ManagingWebSocket ends here



    }


    /**
     * This method is called when the user clicks on a button in the menu item of the side sheet
     * @param item The menu item that was clicked
     * @return false
     */
    private boolean handleMainMenuButton (@NonNull MenuItem item) {
        Log.d("SKGadi", "MenuItem: "+ item.getItemId());
        //Configuring device
        if (item.getItemId() == R.id.main_menu_device_configure) {
            //This is done to make sure that the device fragment is loaded after the side sheet is closed
            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            new ManipulateFragmentContainerView(
                                    ManipulateFragmentContainerView.MANIPULATION.SHOW,
                                    getSupportFragmentManager(),
                                    R.id.main_screen_fragment_container_view,
                                    "mx.com.sigrama.ars.FragmentDevice",
                                    true);
                        }
                    },
                    100);
        }
        //Connecting to device
        if (item.getItemId() == R.id.main_menu_device_connect) {
            //This is done to make sure that the device is connected after the side sheet is closed
            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            connectionManager.connect();
                        }
                    },
                    100);
        }
        sideSheetForMenu.hide();
        return false;
    }

}