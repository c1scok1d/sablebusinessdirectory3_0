package com.macinternetservices.sablebusinessdirectory.ui.specification.addspecification;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentAddAndEditSpecificationBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.SpecsViewModel;

public class AddAndEditSpecificationFragment  extends PSFragment {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    private SpecsViewModel specsViewModel;
    private PSDialogMsg psDialogMsg;


    @VisibleForTesting
    AutoClearedValue<FragmentAddAndEditSpecificationBinding> binding;
    AutoClearedValue<ProgressDialog> progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentAddAndEditSpecificationBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_and_edit_specification, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();


    }

    @Override
    protected void initUIAndActions() {

        progressDialog = new AutoClearedValue<>(this, new ProgressDialog(getActivity()));
        progressDialog.get().setMessage(getString(R.string.message__please_wait));
        progressDialog.get().setCancelable(false);

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().submitButton.setOnClickListener(v -> {

            if (binding.get().headerNameEditText.getText().toString().equals("") || binding.get().specificationDescriptionEditText.getText().toString().equals("")) {
                psDialogMsg.showErrorDialog(getString(R.string.specification__insert_value), getString(R.string.app__ok));
                psDialogMsg.show();
            } else {

                if (!binding.get().headerNameEditText.getText().toString().equals(specsViewModel.specificationName) || !binding.get().specificationDescriptionEditText.getText().toString().equals(specsViewModel.specificationDescription)) {
                    specsViewModel.setAddSpecificationObj(specsViewModel.itemId, binding.get().headerNameEditText.getText().toString(), binding.get().specificationDescriptionEditText.getText().toString(), specsViewModel.id);

                    progressDialog.get().show();
                } else if (binding.get().headerNameEditText.getText().toString().equals(specsViewModel.specificationName)) {
                    psDialogMsg.showErrorDialog(getString(R.string.specification__already_added), getString(R.string.app__ok));
                    psDialogMsg.show();
                }
            }


        });

    }

    @Override
    protected void initViewModels() {
        specsViewModel = new ViewModelProvider(this, viewModelFactory).get(SpecsViewModel.class);
    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

        if (getActivity() != null) {
            if (getActivity().getIntent().getExtras() != null) {
                specsViewModel.itemId = getActivity().getIntent().getStringExtra(Constants.ITEM_ID);
                specsViewModel.id = getActivity().getIntent().getStringExtra(Constants.SPECIFICATION_ID);
                specsViewModel.specificationName = getActivity().getIntent().getStringExtra(Constants.SPECIFICATION_NAME);
                specsViewModel.specificationDescription = getActivity().getIntent().getStringExtra(Constants.SPECIFICATION_DESCRIPTION);
            }

            if (!specsViewModel.specificationName.equals("")) {
                binding.get().headerNameEditText.setText(specsViewModel.specificationName);
            }
            if (!specsViewModel.specificationDescription.equals("")) {
                binding.get().specificationDescriptionEditText.setText(specsViewModel.specificationDescription);
            }
        }

        specsViewModel.getSaveSpecificationData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        psDialogMsg.showSuccessDialog(getString(R.string.specification__success_saved), getString(R.string.app__ok));
                        progressDialog.get().cancel();
                        psDialogMsg.show();

                        psDialogMsg.okButton.setOnClickListener(v -> {
                            psDialogMsg.cancel();

                            getActivity().finish();

                        });
                        break;

                    case ERROR:
                        progressDialog.get().cancel();
                        psDialogMsg.showErrorDialog(result.message, getString(R.string.app__ok));
                        progressDialog.get().cancel();
                        break;
                }
            }
        });
    }
}

