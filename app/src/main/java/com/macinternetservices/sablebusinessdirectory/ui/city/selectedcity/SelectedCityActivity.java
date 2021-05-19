package com.macinternetservices.sablebusinessdirectory.ui.city.selectedcity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ActivitySelectedCityBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSAppCompactActivity;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.MyContextWrapper;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.ItemParameterHolder;

import javax.inject.Inject;

public class SelectedCityActivity extends PSAppCompactActivity {

    ActivitySelectedCityBinding binding;
    PSDialogMsg psDialogMsg;
    ItemParameterHolder itemParameterHolder;

    @Inject
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_selected_city);
        itemParameterHolder = new ItemParameterHolder().getRecentItem();

        initUI(binding);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.home_menu:

                        navigationController.navigateToHomeFragment(SelectedCityActivity.this);
                        initToolbar(binding.toolbar, getIntent().getStringExtra(Constants.CITY_NAME));

                        break;
                    case R.id.explorer_menu:
                        itemParameterHolder.city_id = getIntent().getStringExtra(Constants.CITY_ID);
                        navigationController.navigateToSearch(SelectedCityActivity.this,itemParameterHolder);
                        setToolbarText(binding.toolbar, getString(R.string.menu__exployer));

                        break;

                    case R.id.interest_menu:

                        navigationController.navigateToCategoryFragment(SelectedCityActivity.this);
                        setToolbarText(binding.toolbar, SelectedCityActivity.this.getString(R.string.category__list_title));

                        break;

                    case R.id.search_menu:

//                    navigationController.navigateToSearch(this);

                        navigationController.navigateToFilteringFragment(SelectedCityActivity.this);
                        setToolbarText(binding.toolbar, SelectedCityActivity.this.getString(R.string.menu__search));

                        break;

                    case R.id.side_bar_menu:

                        navigationController.navigateToCityMenu(SelectedCityActivity.this);
                        setToolbarText(binding.toolbar, getString(R.string.city_menu__title));

                        break;

                    default:

//                    navigationController.navigateToShopProfile(this);
//                    setToolbarText(binding.toolbar, getString(R.string.app__app_name));

                        break;
                }

                return true;
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String LANG_CURRENT = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);
        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT, CURRENT_LANG_COUNTRY_CODE, true));
    }

    private void initUI(ActivitySelectedCityBinding binding) {

        psDialogMsg = new PSDialogMsg(this, false);

        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {

                pref.edit().putString(getIntent().getStringExtra(Constants.CITY_ID),"").apply();
                initToolbar(binding.toolbar, getIntent().getExtras().getString(Constants.CITY_NAME));

                setupFragment(new SelectedCityFragment());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            psDialogMsg.showConfirmDialog(getString(R.string.city__sure_to_exit), getString(R.string.app__ok), getString(R.string.app__cancel));

            psDialogMsg.show();

            psDialogMsg.okButton.setOnClickListener(view -> finish());
            psDialogMsg.cancelButton.setOnClickListener(view -> psDialogMsg.cancel());

            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if(fragment != null)
        {
            if(fragment instanceof SelectedCityFragment)
            {
                psDialogMsg.showConfirmDialog(getString(R.string.city__sure_to_exit), getString(R.string.app__ok), getString(R.string.app__cancel));

                psDialogMsg.show();

                psDialogMsg.okButton.setOnClickListener(view -> finish());
                psDialogMsg.cancelButton.setOnClickListener(view -> psDialogMsg.cancel());
            }else {

                binding.bottomNavigationView.setSelectedItemId(R.id.home_menu);

                navigationController.navigateToHomeFragment(this);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.psLog("Inside Result MainActivity");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
