package com.macinternetservices.sablebusinessdirectory.ui.city.cityList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ActivityCityListBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSAppCompactActivity;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.MyContextWrapper;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;

public class CityListActivity extends PSAppCompactActivity {
    BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null){
                Toast.makeText(context, ""+intent.getStringExtra(Constants.CITY_NAME), Toast.LENGTH_SHORT).show();
/*     Intent i=new Intent("BACK_SEND_DATA");
     i.putExtra(Constants.CITY_ID,intent.getStringExtra(Constants.CITY_ID));
     i.putExtra(Constants.CITY_NAME,intent.getStringExtra(Constants.CITY_NAME));*/
                CityListActivity.this.setResult(Activity.RESULT_OK,intent);
                finish();
/*     sendBroadcast(intent);
     finish();*/
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCityListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_city_list);

        initUI(binding);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,new IntentFilter("New_Data"));
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
    private void initUI(ActivityCityListBinding binding) {

        // Toolbar
        initToolbar(binding.toolbar, getIntent().getStringExtra(Constants.CITY_TITLE));

        if (getIntent().hasExtra("Coming_From_Main")) {
            try {
                CityListFragment fragment=new CityListFragment();
                Bundle bundle=new Bundle();
                bundle.putString("Coming_From_Main",getIntent().getStringExtra("Coming_From_Main"));
                fragment.setArguments(bundle);

                this.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame,fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }else {
            try{
                CityListFragment fragment=new CityListFragment();
                Bundle bundle=new Bundle();
                bundle.putString("Coming_From_Main","");
                fragment.setArguments(bundle);

                this.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame,fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
            // setup Fragment
            //   setupFragment(new CityListFragment());
        }
        // Or you can call like this
        //setupFragment(new NewsListFragment(), R.id.content_frame);
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

    @Override
    protected void onDestroy() {
        /*  LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);*/
        super.onDestroy();
    }
}
