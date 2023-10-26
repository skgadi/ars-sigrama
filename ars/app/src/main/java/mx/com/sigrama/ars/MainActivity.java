package mx.com.sigrama.ars;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.sidesheet.SideSheetDialog;
import com.permissionx.guolindev.PermissionMediator;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.request.PermissionBuilder;

import java.util.Timer;
import java.util.TimerTask;

import mx.com.sigrama.ars.common.GlobalSharedPreferencesForProject;
import mx.com.sigrama.ars.common.ManipulateFragmentContainerView;
import mx.com.sigrama.ars.common.TimerForProject;
import mx.com.sigrama.ars.device.ConnectionManager;
import mx.com.sigrama.ars.device.ManagingWebSocket;
import mx.com.sigrama.ars.device.SignalConditioningAndProcessing;

public class MainActivity extends AppCompatActivity {

    private SideSheetDialog sideSheetForMenu;
    public GlobalSharedPreferencesForProject sharedPrefs;
    public ConnectionManager connectionManager;
    public ManagingWebSocket managingWebSocket;
    public SignalConditioningAndProcessing signalConditioningAndProcessing;
    public TimerForProject timerForProject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //The order should be respected to make sure all the modules are loaded properly
        // because some modules depend on other modules


        //Timer for the project starts here
        if (timerForProject == null) {
            timerForProject = new TimerForProject();
        }
        //Timer for the project ends here

        //Shared preferences for the project starts here
        if (sharedPrefs == null) {
            sharedPrefs = new GlobalSharedPreferencesForProject(this);
        }
        //Shared preferences for the project ends here


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

        //Signal conditioning and processing starts here
        // This should be called after creating ManagingWebSocket
        signalConditioningAndProcessing = new SignalConditioningAndProcessing(this);
        //Signal conditioning and processing ends here


        // Fill main screen with fragment
        new ManipulateFragmentContainerView(
                ManipulateFragmentContainerView.MANIPULATION.SHOW,
                getSupportFragmentManager(),
                R.id.main_activity_fragment_container_view,
                "mx.com.sigrama.ars.MainScreen");


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
            sideSheetForMenu.setSheetEdge(Gravity.END);
            findViewById(R.id.main_menu_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sideSheetForMenu.show();
                }
            });
        }
        //Menu button functionality ends here

        //Requesting permissions for the project
        requestPermissionsForTheProject();

    }



    /**
     * This method is called when the user clicks on a button in the menu item of the side sheet
     * @param item The menu item that was clicked
     * @return false
     */
    private boolean handleMainMenuButton (@NonNull MenuItem item) {
        //Log.d("SKGadi", "MenuItem: "+ item.getItemId());
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

    /*
    * This function is used to request user to give permissions.
    * The permissions are required for camera, location and storage.
     */

    public void requestPermissionsForTheProject() {
        PermissionBuilder permissionBuilder = PermissionX.init(this).permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE)
                .explainReasonBeforeRequest();
        permissionBuilder.onExplainRequestReason((scope, deniedList, beforeRequest) -> {
            scope.showRequestReasonDialog(deniedList,
                    getString(R.string.main_activity_permissions_request),
                    getString(R.string.general_ok),
                    getString(R.string.general_cancel));
        });
        permissionBuilder.onForwardToSettings((scope, deniedList) -> {
            scope.showForwardToSettingsDialog(deniedList,
                    getString(R.string.main_activity_permissions_manual_request),
                    getString(R.string.general_ok),
                    getString(R.string.general_cancel));
        });
        permissionBuilder.request((allGranted, grantedList, deniedList) -> {
            if (allGranted) {
                //Log.d("SKGadi", "All permissions are granted");
            } else {
                //Log.d("SKGadi", "These permissions are denied: $deniedList");
            }
        });
    }

}