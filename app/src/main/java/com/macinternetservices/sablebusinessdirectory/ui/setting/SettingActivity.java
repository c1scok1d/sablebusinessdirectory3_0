package com.macinternetservices.sablebusinessdirectory.ui.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ActivitySettingBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSAppCompactActivity;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.MyContextWrapper;

import androidx.databinding.DataBindingUtil;

public class SettingActivity extends PSAppCompactActivity {

    //region Override Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySettingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);

        // Init all UI
        initUI(binding);

    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String LANG_CURRENT = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);

        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, LANG_CURRENT, CURRENT_LANG_COUNTRY_CODE, true));

    }
    //endregion


    //region Private Methods

    private void initUI(ActivitySettingBinding binding) {

        // Toolbar
        initToolbar(binding.toolbar, getResources().getString(R.string.edit_setting__title));
        // setup Fragment
        setupFragment(new SettingFragment());

    }

    //endregion


}
