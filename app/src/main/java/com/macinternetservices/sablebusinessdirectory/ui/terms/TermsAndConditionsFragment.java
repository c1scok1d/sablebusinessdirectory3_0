package com.macinternetservices.sablebusinessdirectory.ui.terms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentTermsAndConditionsBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.ItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;


public class TermsAndConditionsFragment extends PSFragment {

    //region Variables
    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private ItemViewModel itemViewModel;

    @VisibleForTesting
    private AutoClearedValue<FragmentTermsAndConditionsBinding> binding;
    //endregion

    //region Override Methods
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentTermsAndConditionsBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_terms_and_conditions, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }

    @Override
    protected void initUIAndActions() {

    }

    @Override
    protected void initViewModels() {
        itemViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override

    protected void initData() {

        getIntentData();
        itemViewModel.setItemDetailObj(itemViewModel.itemId, selectedCityId, itemViewModel.historyFlag, loginUserId);
        itemViewModel.getItemDetailData().observe(this, resource -> {

            if (resource != null) {

                switch (resource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (resource.data != null) {

                            fadeIn(binding.get().getRoot());

                            binding.get().termsAndConditionTextView.setText(resource.data.terms);
                            setAboutUsData(resource.data);

                        }
                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (resource.data != null) {

                            binding.get().termsAndConditionTextView.setText(resource.data.terms);
                            setAboutUsData(resource.data);


                        }

                        break;
                    case ERROR:
                        // Error State

                        break;
                    default:
                        // Default

                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }


            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (resource != null) {
                Utils.psLog("Got Data Of About Us.");


            } else {
                //noinspection Constant Conditions
                Utils.psLog("No Data of About Us.");
            }
        });
    }

    //endregion

    private void getIntentData() {
        try {
            if (getActivity() != null) {
                if (getActivity().getIntent().getExtras() != null) {
                    itemViewModel.flagType = getActivity().getIntent().getExtras().getString(Constants.FLAG);
                    itemViewModel.itemId = getActivity().getIntent().getExtras().getString(Constants.CITY_ITEM_ID);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("", e);
        }
    }

    private void setAboutUsData(Item item) {
        binding.get().setItem(item);
//        shopViewModel.aboutId = shop.id;

        getIntentData();

//        binding.get().termsAndConditionTextView.setText(city.terms);
        if (itemViewModel.flagType.equals(Constants.CITY_TERMS)) {
            binding.get().termsAndConditionTextView.setText(item.terms);

        } else if (itemViewModel.flagType.equals(Constants.CITY_CANCELLATION)){
            binding.get().termsAndConditionTextView.setText(item.cancelation_policy);
        }else {
            binding.get().termsAndConditionTextView.setText(item.additional_info);
        }

    }
}