package com.macinternetservices.sablebusinessdirectory.ui.imageupload;

import androidx.lifecycle.ViewModelProvider;

import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.ItemViewModel;

public class ItemEntryImageUploadFragment extends ImageUploadFragment {

    ItemViewModel itemListViewModel;

    @Override
    protected void initViewModels() {
        itemListViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
    }

    @Override
    protected void uploadImage() {
        if(!binding.get().descEditText.getText().toString().isEmpty())
        {
            img_desc = binding.get().descEditText.getText().toString();
        }

        if(getActivity() != null)
        itemListViewModel.uploadImage(imagePath, selectedImage,selectedId,img_id, img_desc,
                (getActivity()).getContentResolver()).observe(this, listResource -> {
            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        progressDialog.get().cancel();
                        PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
                        psDialogMsg.showSuccessDialog(getString(R.string.message__image_upload_complete), getString(R.string.message__ok_close));
                        psDialogMsg.show();

                        if (listResource.data != null) {
                            img_id = listResource.data.imgId;
                        }

                        psDialogMsg.okButton.setOnClickListener(v -> {
                            psDialogMsg.cancel();
                            closeActivity();

                        });

                        break;

                    case LOADING:
                        break;


                    case ERROR:
                        progressDialog.get().cancel();
                }
            }

        });
    }
}

