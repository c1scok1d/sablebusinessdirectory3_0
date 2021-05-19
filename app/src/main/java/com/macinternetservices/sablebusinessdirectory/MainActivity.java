package com.macinternetservices.sablebusinessdirectory;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.login.LoginManager;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.macinternetservices.sablebusinessdirectory.databinding.ActivityMainBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.NavigationController;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSAppCompactActivity;
import com.macinternetservices.sablebusinessdirectory.ui.dashboard.DashBoardCityListFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AppLanguage;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.GPSTracker;
import com.macinternetservices.sablebusinessdirectory.utils.GeolocationService;
import com.macinternetservices.sablebusinessdirectory.utils.MyContextWrapper;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.utils.PermissionRationaleActivity;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.common.NotificationViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.user.UserViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.User;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.CityParameterHolder;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

/**
 * MainActivity of Panacea-Soft
 * Contact Email : teamps.is.cool@gmail.com
 *
 * @author Panacea-soft
 * @version 1.0
 * @since 11/15/17.
 */

public class MainActivity extends PSAppCompactActivity {


    //region Variables

    @Inject
    SharedPreferences pref;

    @Inject
    AppLanguage appLanguage;

    private Boolean notiSetting = false;
    private String token = "";
    private UserViewModel userViewModel;

    LocationManager locationManager;
    Location location;

    private NotificationViewModel notificationViewModel;
    private User user;
    private PSDialogMsg psDialogMsg;
    private boolean isLogout = false;
    Drawable yourdrawable = null;
    ActionBarDrawerToggle drawerToggle;
    private int toolbarIconColor = Color.GRAY;
    private String loginUserId;
    //    public String firstTime;
    private String token1;
    private ConsentForm form;
    public String notificationItemId, notificationMsg, notificationFlag, userId;


    //private Boolean alreadyNotiMsgShow = false;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    public ActivityMainBinding binding;
    //private BroadcastReceiver broadcastReceiver = null;
    //endregion


    //region Override Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Base_PSTheme);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        initUIAndActions();

        initModels();

        initData();

        checkConsentStatus();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String LANG_CURRENT = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);
        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT, CURRENT_LANG_COUNTRY_CODE, true));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    protected void onResume() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                     checkPermissionsQ();
                 } else {
                     checkPermissions();
                 }
        super.onResume();
    }

    private void checkPermissions() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )) {
            startActivity(new Intent(this, PermissionRationaleActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {
            if(pref.getBoolean(Constants.GEO_SERVICE_KEY,true)){
                if (!isMyServiceRunning(GeolocationService.class)) {
                    startService(new Intent(MainActivity.this, GeolocationService.class));
                }
            }
        }
    }

    private void checkPermissionsQ() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            startActivity(new Intent(this, PermissionRationaleActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {
            if(pref.getBoolean(Constants.GEO_SERVICE_KEY,true)){
                if (!isMyServiceRunning(GeolocationService.class)) {
                    startService(new Intent(MainActivity.this, GeolocationService.class));
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

            if(fragment != null)
            {
                if(fragment instanceof DashBoardCityListFragment)
                {
                    String message = getBaseContext().getString(R.string.message__want_to_quit);
                    String okStr =getBaseContext().getString(R.string.message__ok_close);
                    String cancelStr = getBaseContext().getString(R.string.message__cancel_close);

                    psDialogMsg.showConfirmDialog(message, okStr,cancelStr );

                    psDialogMsg.show();

                    psDialogMsg.okButton.setOnClickListener(view -> {
                        psDialogMsg.cancel();
                        MainActivity.this.finish();
                        System.exit(0);
                    });
                    psDialogMsg.cancelButton.setOnClickListener(view -> psDialogMsg.cancel());
                }else {
                    setSelectMenu(R.id.nav_home);

                    binding.bottomNavigationView.setSelectedItemId(R.id.home_menu);

                    setToolbarText(binding.toolbar, Constants.EMPTY_STRING);
                    navigationController.navigateToHome(MainActivity.this);

                }

            }
        }
        return  true;
    }


    //endregion


    //region Private Methods

    /**
     * Initialize Models
     */
    private void initModels() {

        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
        notificationViewModel = new ViewModelProvider(this, viewModelFactory).get(NotificationViewModel.class);
    }


    /**
     * Show alert message to user.
     *
     * @param msg Message to show to user
     */
    private void showAlertMessage(String msg) {


        psDialogMsg.showNotiDefaultDialog(msg, getString(R.string.app__ok) );


       psDialogMsg.showNotiDefaultDialog(msg, getString(R.string.app__ok) );
        psDialogMsg.show();

        psDialogMsg.okButton.setOnClickListener(view -> psDialogMsg.cancel());


    }

    /**

     * Show alert message to user.

     *

     * @param message Message to show to user

     */

    private void showNotiMessage(String message) {
        psDialogMsg.showNotiDialog(message, getString(R.string.app__noti_open),getString(R.string.app__cancel) );

        psDialogMsg.show();

        psDialogMsg.okButton.setOnClickListener(view -> {
            psDialogMsg.cancel();
            navigationController.navigateToNotificationList(MainActivity.this);
        });
        psDialogMsg.cancelButton.setOnClickListener(view -> psDialogMsg.cancel());

    }

    /**
     * This function will initialize UI and Event Listeners
     */
    private void initUIAndActions() {
        psDialogMsg = new PSDialogMsg(this, false);

        Menu navViewMenu = binding.navView.getMenu();
        if(!Config.ENABLE_ITEM_UPLOAD){
            navViewMenu.findItem(R.id.nav_upload_item_login).setVisible(false);
        }else{
            navViewMenu.findItem(R.id.nav_upload_item_login).setVisible(true);
        }

        initToolbar(binding.toolbar, "");

        initDrawerLayout();

        initNavigationView();

        navigationController.navigateToCityList(this);

        getIntentData();

        setSelectMenu(R.id.nav_home);
    }


    private void initDrawerLayout() {

        drawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.app__drawer_open, R.string.app__drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle.setHomeAsUpIndicator(R.drawable.baseline_menu_grey_24);

        drawerToggle.setToolbarNavigationClickListener(view -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.drawerLayout.addDrawerListener(drawerToggle);
        binding.drawerLayout.post(drawerToggle::syncState);

    }

    private void initNavigationView() {

        if (binding.navView != null) {

            // Updating Custom Fonts
            Menu m = binding.navView.getMenu();
            try {
                if (m != null) {

                    for (int i = 0; i < m.size(); i++) {
                        MenuItem mi = m.getItem(i);

                        //for applying a font to subMenu ...
                        SubMenu subMenu = mi.getSubMenu();
                        if (subMenu != null && subMenu.size() > 0) {
                            for (int j = 0; j < subMenu.size(); j++) {
                                MenuItem subMenuItem = subMenu.getItem(j);

                                subMenuItem.setTitle(subMenuItem.getTitle());
                                // update font

                                subMenuItem.setTitle(Utils.getSpannableString(getBaseContext(), subMenuItem.getTitle().toString(), Utils.Fonts.ROBOTO));

                            }
                        }

                        mi.setTitle(mi.getTitle());
                        // update font

                        mi.setTitle(Utils.getSpannableString(getBaseContext(), mi.getTitle().toString(), Utils.Fonts.ROBOTO));
                    }
                }
            } catch (Exception e) {
                Utils.psErrorLog("Error in Setting Custom Font", e);
            }

            binding.navView.setNavigationItemSelectedListener(menuItem -> {
                navigationMenuChanged(menuItem);
                return true;
            });

        }

        if (binding.bottomNavigationView != null) {

            // Updating Custom Fonts
            Menu m = binding.bottomNavigationView.getMenu();
            try {

                for (int i = 0; i < m.size(); i++) {
                    MenuItem mi = m.getItem(i);

                    //for applying a font to subMenu ...
                    SubMenu subMenu = mi.getSubMenu();
                    if (subMenu != null && subMenu.size() > 0) {
                        for (int j = 0; j < subMenu.size(); j++) {
                            MenuItem subMenuItem = subMenu.getItem(j);

                            subMenuItem.setTitle(subMenuItem.getTitle());
                            // update font

                            subMenuItem.setTitle(Utils.getSpannableString(getBaseContext(), subMenuItem.getTitle().toString(), Utils.Fonts.ROBOTO));

                        }
                    }

                    mi.setTitle(mi.getTitle());
                    // update font

                    mi.setTitle(Utils.getSpannableString(getBaseContext(), mi.getTitle().toString(), Utils.Fonts.ROBOTO));
                }
            } catch (Exception e) {
                Utils.psErrorLog("Error in Setting Custom Font", e);
            }

            binding.navView.setNavigationItemSelectedListener(menuItem -> {
                navigationMenuChanged(menuItem);
                return true;
            });

        }

    }

//    private void showBottomNavigation() {
//        binding.bottomNavigationView.setVisibility(View.VISIBLE);
//    }

    private void hideBottomNavigation() {
        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    private void navigationMenuChanged(MenuItem menuItem) {
        openFragment(menuItem.getItemId());

        if (menuItem.getItemId() != R.id.nav_logout_login) {
            menuItem.setChecked(true);
            binding.drawerLayout.closeDrawers();
        }
    }

    public void setSelectMenu(int id) {
        binding.navView.setCheckedItem(id);
    }

    private int menuId = 0;

    /**
     * Open Fragment
     *
     * @param menuId To know which fragment to open.
     */
    private void openFragment(int menuId) {

        this.menuId = menuId;
        switch (menuId) {
            case R.id.nav_home:
            case R.id.nav_home_login:
                setToolbarText(binding.toolbar, Constants.EMPTY_STRING);
                navigationController.navigateToHome(this);
                break;

            case R.id.nav_all_cities:
            case R.id.nav_all_cities_login:
                setToolbarText(binding.toolbar, getString(R.string.menu__all_cities));
                navigationController.navigateToAllCityListHomeFragment(this, new CityParameterHolder().getRecentCities());
                break;

            case R.id.nav_popular_cities:
            case R.id.nav_popular_cities_login:
                setToolbarText(binding.toolbar, getString(R.string.menu__popular_cities));
                navigationController.navigateToPopularCityListHomeFragment(this, new CityParameterHolder().getPopularCities());
                break;

            case R.id.nav_recommended_cities:
            case R.id.nav_recommended_cities_login:
                setToolbarText(binding.toolbar, getString(R.string.menu__recommended_cities));
                navigationController.navigateToRecommendedCityListHomeFragment(this, new CityParameterHolder().getFeaturedCities());
                break;
            case R.id.nav_profile:
            case R.id.nav_profile_login:

                Utils.navigateOnUserVerificationFragment(pref,user,navigationController,this);

                Utils.psLog("nav_profile");

                hideBottomNavigation();

                break;

            case R.id.nav_favourite_news_login:

                setToolbarText(binding.toolbar, getString(R.string.menu__favourite_items));
                navigationController.navigateToFavourite(this);
                Utils.psLog("nav_favourite_news");

                hideBottomNavigation();
                break;

//            case R.id.nav_transaction_login:
//                setToolbarText(binding.toolbar,getString(R.string.menu__transaction));
//                navigationController.navigateToTransaction(this);
//                Utils.psLog("nav_transaction");
//
//                hideBottomNavigation();
//                break;

            case R.id.nav_user_history_login:
                setToolbarText(binding.toolbar, getString(R.string.menu__user_history));
                navigationController.navigateToHistory(this);
                Utils.psLog("nav_history");

                hideBottomNavigation();
                break;

            case R.id.nav_logout_login:

                psDialogMsg.showConfirmDialog(getString(R.string.edit_setting__logout_question), getString(R.string.app__ok), getString(R.string.app__cancel));

                psDialogMsg.show();

                psDialogMsg.okButton.setOnClickListener(view -> {

                    psDialogMsg.cancel();

                    hideBottomNavigation();

                    userViewModel.deleteUserLogin(user).observe(this, status -> {
                        if (status != null) {
                            this.menuId = 0;

                            setToolbarText(binding.toolbar, getString(R.string.app__app_name));

                            isLogout = true;

                            LoginManager.getInstance().logOut();

                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .build();
                            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                            googleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    });

                    Utils.psLog("nav_logout_login");
                });

                psDialogMsg.cancelButton.setOnClickListener(view -> psDialogMsg.cancel());

                break;

//            case R.id.nav_about_us:
//            case R.id.nav_about_us_login:
//
//                Utils.psLog("nav_about_us");
//                setToolbarText(binding.toolbar, getString(R.string.menu__about_app));
//                navigationController.navigateToAppInfoFragment(this);
//
//                hideBottomNavigation();
//                break;

//            case R.id.nav_noti:
//            case R.id.nav_noti_login:
//
//                setToolbarText(binding.toolbar, getString(R.string.menu__notification_setting));
//                navigationController.navigateToNotificationSetting(this);
//                Utils.psLog("nav_setting");
//
//                hideBottomNavigation();
//                break;

//            case R.id.nav_contact_us:
//            case R.id.nav_contact_us_login:
//
//                setToolbarText(binding.toolbar, getString(R.string.menu__setting));
//                navigationController.navigateToContactUs(this);
//                Utils.psLog("nav_setting");
//
//                hideBottomNavigation();
//                break;

            case R.id.nav_setting:
            case R.id.nav_setting_login:

                setToolbarText(binding.toolbar, getString(R.string.menu__setting));
                navigationController.navigateToSetting(this);
                Utils.psLog("nav_setting");

                hideBottomNavigation();
                break;

            case R.id.nav_language:
            case R.id.nav_language_login:

                setToolbarText(binding.toolbar, getString(R.string.menu__language));
                navigationController.navigateToLanguageSetting(this);
                Utils.psLog("nav_language");
                hideBottomNavigation();

                break;
            case R.id.nav_rate_this_app:
            case R.id.nav_rate_this_app_login:

                setToolbarText(binding.toolbar, getString(R.string.menu__rate));
                navigationController.navigateToPlayStore(this);
                hideBottomNavigation();

                break;

            case R.id.nav_upload_item_login:

                setToolbarText(binding.toolbar, getString(R.string.menu__uploaded_items));
                navigationController.navigateToItemUpdated(this);
                Utils.psLog("nav_uploaded_news");

                hideBottomNavigation();
                break;

            case R.id.nav_privacy_policy:
            case R.id.nav_privacy_policy_login:

                setToolbarText(binding.toolbar, getString(R.string.menu__privacy_policy));
                navigationController.navigateToPrivacyPolicy(this);
                hideBottomNavigation();

                break;

            case R.id.nav_transaction_login:

                setToolbarText(binding.toolbar, getString(R.string.menu__paid_ad_transaction));
                navigationController.navigateToTransactions(this);
                Utils.psLog("nav_transactions_news");

                hideBottomNavigation();
                break;
        }

    }


    private void getIntentData() {
        notificationMsg = getIntent().getStringExtra(Constants.NOTI_MSG);
        notificationFlag = getIntent().getStringExtra(Constants.NOTI_FLAG);

        userId = pref.getString(Constants.USER_ID, Constants.EMPTY_STRING);


        if(notificationFlag != null) {
            switch (notificationFlag) {

                case Constants.NOTI_APPROVAL:
                    showAlertMessage(notificationMsg);

                    break;
                case Constants.NOTI_BROADCAST:
                    showNotiMessage(notificationMsg);

                    break;
                default:
                    showAlertMessage(notificationMsg);
                    break;
            }
        }
    }

    /**
     * Initialize Data
     */
    private void initData() {

        try {

            notiSetting = pref.getBoolean(Constants.NOTI_SETTING, false);
            token = pref.getString(Constants.NOTI_TOKEN, "");
            loginUserId = pref.getString(Constants.USER_ID, Constants.EMPTY_STRING);
            Utils.psLog(loginUserId);

        } catch (NullPointerException ne) {
            Utils.psErrorLog("Null Pointer Exception.", ne);
        } catch (Exception e) {
            Utils.psErrorLog("Error in getting notification flag data.", e);
        }

        userViewModel.getLoginUser().observe(this, data -> {

            if (data != null) {

                if (data.size() > 0) {
                    user = data.get(0).user;

                    pref.edit().putString(Constants.USER_ID, user.userId).apply();
                    pref.edit().putString(Constants.USER_NAME, user.userName).apply();
                    pref.edit().putString(Constants.USER_EMAIL, user.userEmail).apply();

                } else {
                    user = null;

                    pref.edit().remove(Constants.USER_ID).apply();
                    pref.edit().remove(Constants.USER_NAME).apply();
                    pref.edit().remove(Constants.USER_EMAIL).apply();
                }

            } else {

                user = null;
                pref.edit().remove(Constants.USER_ID).apply();
                pref.edit().remove(Constants.USER_NAME).apply();
                pref.edit().remove(Constants.USER_EMAIL).apply();

            }
            updateMenu();

            if (isLogout) {
//                setToolbarText(binding.toolbar, getString(R.string.app__app_name));
//                showBottomNavigation();
                navigationController.navigateToHome(MainActivity.this);
                isLogout = false;
            }

        });


        registerNotificationToken(); // Just send "" because don't have token to sent. It will get token itself.
    }

    /**
     * This function will change the menu based on the user is logged in or not.
     */
    private void updateMenu() {

        if (user == null) {

            binding.navView.getMenu().setGroupVisible(R.id.group_before_login, true);
            binding.navView.getMenu().setGroupVisible(R.id.group_after_login, false);

            setSelectMenu(R.id.nav_home);

        } else {
            binding.navView.getMenu().setGroupVisible(R.id.group_after_login, true);
            binding.navView.getMenu().setGroupVisible(R.id.group_before_login, false);

            if (menuId == R.id.nav_profile) {
                setSelectMenu(R.id.nav_profile_login);
            } else if (menuId == R.id.nav_profile_login) {
                setSelectMenu(R.id.nav_profile_login);
            } else {
                setSelectMenu(R.id.nav_home_login);
            }

        }


    }

    private void registerNotificationToken() {
        /*
         * Register Notification
         */
        // Check already submit or not
        // If haven't, submit to server
        if (!notiSetting) {

            if (this.token.equals("")) {

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {

                                return;
                            }

                            // Get new Instance ID token
                            if (task.getResult() != null) {
                                token1 = task.getResult().getToken();
                            }

                            notificationViewModel.registerNotification(getBaseContext(), Constants.PLATFORM, token1,loginUserId);
                        });


            }
        } else {
            Utils.psLog("Notification Token is already registered. Notification Setting : true.");
        }
    }

    //endregion


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.notification_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    public void updateToolbarIconColor(int color) {
        toolbarIconColor = color;
        if (yourdrawable != null) {
            yourdrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    public void updateMenuIconWhite() {
        drawerToggle.setHomeAsUpIndicator(R.drawable.baseline_menu_white_24);
    }

    public void updateMenuIconGrey() {
        drawerToggle.setHomeAsUpIndicator(R.drawable.baseline_menu_grey_24);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);

        for(int i = 0; i< menu.size(); i++) {
            yourdrawable = menu.getItem(i).getIcon();
            yourdrawable.setColorFilter(toolbarIconColor, PorterDuff.Mode.SRC_ATOP);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_notification) {

            navigationController.navigateToNotificationList(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkConsentStatus() {

        // For Testing Open this
//        ConsentInformation.getInstance(this).
//                setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);

        ConsentInformation consentInformation = ConsentInformation.getInstance(this);
        String[] publisherIds = {getString(R.string.adview_publisher_key)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.

                Utils.psLog(consentStatus.name());

                if (!consentStatus.name().equals(pref.getString(Config.CONSENTSTATUS_CURRENT_STATUS, Config.CONSENTSTATUS_CURRENT_STATUS)) || consentStatus.name().equals(Config.CONSENTSTATUS_UNKNOWN)) {
                    collectConsent();
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
                Utils.psLog("Failed to updateeee");
            }
        });
    }

    private void collectConsent() {
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL(Config.POLICY_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }

        form = new ConsentForm.Builder(this, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.

                        Utils.psLog("Form loaded");

                        if (form != null) {
                            form.show();
                        }
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.

                        Utils.psLog("Form Opened");
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.

                        pref.edit().putString(Config.CONSENTSTATUS_CURRENT_STATUS, consentStatus.name()).apply();
                        pref.edit().putBoolean(Config.CONSENTSTATUS_IS_READY_KEY, true).apply();
                        Utils.psLog("Form Closed");
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.

                        pref.edit().putBoolean(Config.CONSENTSTATUS_IS_READY_KEY, false).apply();
                        Utils.psLog("Form Error " + errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();

        form.load();

    }
}