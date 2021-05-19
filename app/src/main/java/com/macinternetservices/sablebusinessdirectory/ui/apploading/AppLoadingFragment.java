package com.macinternetservices.sablebusinessdirectory.ui.apploading;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.login.LoginManager;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentAppLoadingBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.apploading.AppLoadingViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.clearalldata.ClearAllDataViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.user.UserViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.PSAppInfo;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppLoadingFragment extends PSFragment {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private PSDialogMsg psDialogMsg;
    private String startDate = Constants.ZERO;
    private String endDate = Constants.ZERO;

    private AppLoadingViewModel appLoadingViewModel;
    private ClearAllDataViewModel clearAllDataViewModel;
    private UserViewModel userViewModel;
    private Handler mHandler = new Handler();
    private int DELAY = 5000;

    @VisibleForTesting
    private AutoClearedValue<FragmentAppLoadingBinding> binding;

    //endregion Variables

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAppLoadingBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_loading, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }


    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(), false);

//        if (force_update) {
//            navigationController.navigateToForceUpdateActivity(this.getActivity(), force_update_title, force_update_msg);
//        }
    }

    @Override
    protected void initViewModels() {
        appLoadingViewModel = new ViewModelProvider(this, viewModelFactory).get(AppLoadingViewModel.class);
        clearAllDataViewModel = new ViewModelProvider(this, viewModelFactory).get(ClearAllDataViewModel.class);
        userViewModel = new ViewModelProvider(this,viewModelFactory).get(UserViewModel.class);
    }

    @Override
    protected void initAdapters() {
    }

    @Override
    protected void initData() {

        if (connectivity.isConnected()) {
            if (startDate.equals(Constants.ZERO)) {

                startDate = getDateTime();
                Utils.setDatesToShared(startDate, endDate, pref);
            }

            endDate = getDateTime();
            appLoadingViewModel.setDeleteHistoryObj(startDate, endDate,loginUserId);

        }else {
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    navigationController.navigateToMainActivity(AppLoadingFragment.this.getActivity());
                }
            }, DELAY);

            if(getActivity() != null) {
                getActivity().finish();
            }
        }

        appLoadingViewModel.getDeleteHistoryData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case SUCCESS:

                        if (result.data != null) {


                        switch (result.data.userInfo.userStatus) {
                            case Constants.USER_STATUS__DELECTED:
                                AppLoadingFragment.this.logout();
                                showErrorDialog(result.data, getString(R.string.error_message__user_deleted));
                                break;
                            case Constants.USER_STATUS__BANNED:
                                AppLoadingFragment.this.logout();
                                showErrorDialog(result.data, getString(R.string.error_message__user_banned));
                                break;
                            case Constants.USER_STATUS__UNPUBLISHED:
                                AppLoadingFragment.this.logout();
                                showErrorDialog(result.data, getString(R.string.error_message__user_unpublished));
                                break;
                            default:
                                //default
                                appLoadingViewModel.psAppInfo = result.data;
                                checkVersionNumber(result.data);
                                startDate = endDate;
                                break;
                            }

                        }
                        break;

                    case ERROR:

                        break;
                }
            }

        });

        clearAllDataViewModel.getDeleteAllDataData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case ERROR:
                        break;

                    case SUCCESS:
                        checkForceUpdate(appLoadingViewModel.psAppInfo);
                        break;
                }
            }
        });

        userViewModel.getLoginUser().observe(this,data ->{
            if (data !=null){
                if (data.size() > 0 ){
                    userViewModel.user = data.get(0).user;
                }
            }
        });

    }


    private void logout(){
        userViewModel.deleteUserLogin(userViewModel.user).observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> status) {
                if (status !=null) {
                    LoginManager.getInstance().logOut();
                }
            }
        });
    }

    public void showErrorDialog(PSAppInfo psAppInfo, String message){
        psDialogMsg.showErrorDialog(message, getString(R.string.app__ok));
        psDialogMsg.show();

        psDialogMsg.okButton.setOnClickListener(view -> {
            psDialogMsg.cancel();
            appLoadingViewModel.psAppInfo = psAppInfo;
            checkVersionNumber(psAppInfo);
            startDate = endDate;
        });

    }

    private void checkForceUpdate(PSAppInfo psAppInfo) {
        if (!Config.APP_VERSION.equals(psAppInfo.psAppVersion.versionNo)) {
            if (psAppInfo.psAppVersion.versionForceUpdate.equals(Constants.ONE)) {

                pref.edit().putString(Constants.APPINFO_PREF_VERSION_NO, psAppInfo.psAppVersion.versionNo).apply();
                pref.edit().putBoolean(Constants.APPINFO_PREF_FORCE_UPDATE, true).apply();
                pref.edit().putString(Constants.APPINFO_FORCE_UPDATE_TITLE, psAppInfo.psAppVersion.versionTitle).apply();
                pref.edit().putString(Constants.APPINFO_FORCE_UPDATE_MSG, psAppInfo.psAppVersion.versionMessage).apply();

                navigationController.navigateToForceUpdateActivity(this.getActivity(), psAppInfo.psAppVersion.versionTitle, psAppInfo.psAppVersion.versionMessage);

            } else if (psAppInfo.psAppVersion.versionForceUpdate.equals(Constants.ZERO)) {

                pref.edit().putBoolean(Constants.APPINFO_PREF_FORCE_UPDATE, false).apply();

                psDialogMsg.showAppInfoDialog(getString(R.string.update), getString(R.string.app__cancel), psAppInfo.psAppVersion.versionTitle, psAppInfo.psAppVersion.versionMessage);
                psDialogMsg.show();
                psDialogMsg.okButton.setOnClickListener(v -> {
                    psDialogMsg.cancel();
                    //navigationController.navigateToMainActivity(AppLoadingFragment.this.getActivity());
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            navigationController.navigateToMainActivity(AppLoadingFragment.this.getActivity());
                        }
                    }, DELAY);
                    navigationController.navigateToPlayStore(AppLoadingFragment.this.getActivity());
                    if (getActivity() != null) {
                        getActivity().finish();
                    }

                });

                psDialogMsg.cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        psDialogMsg.cancel();

                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                navigationController.navigateToMainActivity(AppLoadingFragment.this.getActivity());
                            }
                        }, DELAY);
                        if (AppLoadingFragment.this.getActivity() != null) {
                            AppLoadingFragment.this.getActivity().finish();
                        }
                    }
                });

                psDialogMsg.getDialog().setCancelable(false);
            }

        }
    }

    private void checkVersionNumber(PSAppInfo psAppInfo) {
        if (!Config.APP_VERSION.equals(psAppInfo.psAppVersion.versionNo)) {

            if (psAppInfo.psAppVersion.versionNeedClearData.equals(Constants.ONE)) {
                psDialogMsg.cancel();
                clearAllDataViewModel.setDeleteAllDataObj();
            }else {
                checkForceUpdate(appLoadingViewModel.psAppInfo);
            }
        } else {
            pref.edit().putBoolean(Constants.APPINFO_PREF_FORCE_UPDATE, false).apply();
            navigationController.navigateToMainActivity(AppLoadingFragment.this.getActivity());
            if (getActivity() != null){
                getActivity().finish();
            }
        }

    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        Date date = new Date();
        return dateFormat.format(date);
    }


}

