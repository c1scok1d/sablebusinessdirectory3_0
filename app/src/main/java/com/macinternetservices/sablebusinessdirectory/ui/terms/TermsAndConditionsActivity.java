package com.macinternetservices.sablebusinessdirectory.ui.terms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.databinding.DataBindingUtil;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ActivityTermsAndConditionBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSAppCompactActivity;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.MyContextWrapper;

public class TermsAndConditionsActivity extends PSAppCompactActivity {


    //region Override Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityTermsAndConditionBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_and_condition);

        // Init all UI
        initUI(binding);

    }

    @Override
    protected void attachBaseContext(Context newBase) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String CURRENT_LANG_CODE = preferences.getString(Constants.LANGUAGE_CODE, Config.DEFAULT_LANGUAGE);
        String CURRENT_LANG_COUNTRY_CODE = preferences.getString(Constants.LANGUAGE_COUNTRY_CODE, Config.DEFAULT_LANGUAGE_COUNTRY_CODE);

        super.attachBaseContext(MyContextWrapper.wrap(newBase, CURRENT_LANG_CODE, CURRENT_LANG_COUNTRY_CODE, true));
    }
    //endregion


    //region Private Methods

    private void initUI(ActivityTermsAndConditionBinding binding) {

        // Toolbar
//        if(getIntent().getStringExtra(Constants.FLAG).equals(Constants.CITY_CANCELLATION)){
//            initToolbar(binding.toolbar, getResources().getString(R.string.item_detail__policy));
//        }else {
//            initToolbar(binding.toolbar, getResources().getString(R.string.item_detail__term_con));
//        }
        if (getIntent() != null && getIntent().getStringExtra(Constants.FLAG) !=null ) {
            if (getIntent().getStringExtra(Constants.FLAG).equals(Constants.CITY_TERMS)) {
                initToolbar(binding.toolbar, getResources().getString(R.string.item_detail__term_con));

            } else if (getIntent().getStringExtra(Constants.FLAG).equals(Constants.CITY_CANCELLATION)) {
                initToolbar(binding.toolbar, getResources().getString(R.string.item_detail__policy));
            } else {
                initToolbar(binding.toolbar, getResources().getString(R.string.item_detail__add_info));
            }
        }
            // setup Fragment
            setupFragment(new TermsAndConditionsFragment());

        }


    //endregion


}