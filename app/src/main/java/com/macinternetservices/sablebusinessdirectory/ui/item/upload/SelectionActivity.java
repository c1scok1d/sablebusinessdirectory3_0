package com.macinternetservices.sablebusinessdirectory.ui.item.upload;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ActivitySelectionBinding;
import com.macinternetservices.sablebusinessdirectory.ui.category.categoryselection.CategorySelectionFragment;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSAppCompactActivity;
import com.macinternetservices.sablebusinessdirectory.ui.status.StatusSelectionFragment;
import com.macinternetservices.sablebusinessdirectory.ui.subcategory.subcategoryselection.SubCategorySelectionFragment;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.MyContextWrapper;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;

import java.util.Objects;

public class SelectionActivity extends PSAppCompactActivity {

    public int flagType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectionBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_selection);

        flagType = Objects.requireNonNull(getIntent().getIntExtra((Constants.FLAG),1));

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Utils.psLog("Inside Result MainActivity");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        assert fragment != null;
        fragment.onActivityResult(requestCode, resultCode, data);
    }


    //region Private Methods

    private void initUI(ActivitySelectionBinding binding) {

        // setup Fragment
        if(flagType == Constants.SELECT_CATEGORY) {
            CategorySelectionFragment categoryExpFragment = new CategorySelectionFragment();
            setupFragment(categoryExpFragment);
            initToolbar(binding.toolbar, "Category List");

        }
        else if(flagType == Constants.SELECT_SUBCATEGORY){
            SubCategorySelectionFragment subCategoryExpFragment = new SubCategorySelectionFragment();
            setupFragment(subCategoryExpFragment);
            initToolbar(binding.toolbar, "Sub Category List");

        }else if(flagType == Constants.SELECT_STATUS){
            StatusSelectionFragment categoryExpFragment = new StatusSelectionFragment();
            setupFragment(categoryExpFragment);
            initToolbar(binding.toolbar, "Status List");
        }

    }


}
