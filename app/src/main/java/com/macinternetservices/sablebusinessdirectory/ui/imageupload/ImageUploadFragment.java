package com.macinternetservices.sablebusinessdirectory.ui.imageupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentImageUploadBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.utils.paths.RealPathUtil;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.ItemViewModel;

import java.util.Objects;

public class ImageUploadFragment extends PSFragment {

    protected final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    protected ItemViewModel itemListViewModel;

    @VisibleForTesting
    protected AutoClearedValue<FragmentImageUploadBinding> binding;
    protected AutoClearedValue<ProgressDialog> progressDialog;

    protected int flag;
    protected String img_id = "";
    protected String imagePath = "";
    protected String img_desc;
    protected String selectedId = "";
    protected boolean selected = false;
    Uri selectedImage;
    private PSDialogMsg psDialogMsg;
    private boolean ImageSelected = false;
    public Uri ImageUri = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentImageUploadBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_upload, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);
        getIntentData();
        return binding.get().getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.RESULT_LOAD_IMAGE_CATEGORY && resultCode == Constants.RESULT_OK && null != data) {

            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Glide.with(Objects.requireNonNull(getContext())).load(selectedImage).into(binding.get().selectedImageView);
            selected = true;

            if (RealPathUtil.getRealPath(getContext(), selectedImage).contains(".webp")) {
                psDialogMsg.showErrorDialog(getString(R.string.error_message__webp_image), getString(R.string.app__ok));
                psDialogMsg.show();
                // imagePath = RealPathUtil.getRealPath(getContext(), selectedImage);


//            if (getActivity() != null && selectedImage != null) {
//                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
//                        null, null, null, null);
//
//                if (cursor != null) {
//                    cursor.moveToFirst();
//
////                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////                    imagePath = cursor.getString(columnIndex);
//                    String documentId = cursor.getString(0);
//                    documentId =documentId.substring(documentId.lastIndexOf(":") + 1);
//                    Utils.psLog(documentId);
//                    cursor.close();
//
//                    cursor = getActivity().getContentResolver().query(
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                            null,
//                            MediaStore.Images.Media._ID + " = ? ",
//                            new String[] {documentId}, null);
//                    if (cursor != null) {
//                        cursor.moveToFirst();
//                        imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                        cursor.close();
//                    }
//                }
//
//            }

            } else {
                dataBindingComponent.getFragmentBindingAdapters().bindFullImageUri(binding.get().selectedImageView, selectedImage);
                imagePath = RealPathUtil.getRealPath(getContext(), selectedImage);//convertToImagePath(data, selectedImage, filePathColumn);
                ImageUri = selectedImage;
                ImageSelected = true;
            }
        }
    }

    @Override
    protected void initUIAndActions() {

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        progressDialog = new AutoClearedValue<>(this, new ProgressDialog(getActivity()));
        progressDialog.get().setMessage(getString(R.string.message__please_wait));
        progressDialog.get().setCancelable(false);

        binding.get().chooseFileButton.setOnClickListener(v -> navigationController.navigateToGalleryImage(getActivity()));

        binding.get().uploadPhotoButton.setOnClickListener(v -> {

            if (imgUploadCondition()) {
                uploadImage();
                progressDialog.get().show();
            }else {
                PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(),false);
                psDialogMsg.showErrorDialog("This photo is already added", getString(R.string.app__ok));
//                psDialogMsg.show();

            }
        });
    }

    private void getIntentData() {
        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();

            img_id = intent.getStringExtra(Constants.IMG_ID);
            String img = intent.getStringExtra(Constants.IMGPATH);
            img_desc = intent.getStringExtra(Constants.IMGDESC);
            flag = intent.getIntExtra(Constants.FLAG, 0);
            selectedId = intent.getStringExtra(Constants.SELECTEDID);

            if (!img.equals("") && !img.isEmpty()) {
                binding.get().setImage(img);
                binding.get().descEditText.setText(img_desc);

            }
        }
    }

    @Override
    protected void initViewModels() {


        if (flag == Constants.IMAGE_UPLOAD_ITEM) {
            itemListViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
        }

    }

    @Override
    protected void initAdapters() {

    }

    @Override
    protected void initData() {

    }

    protected void uploadImage() {

    }

    public boolean imgUploadCondition() {
        boolean result = true;

        if (!selected) {
            if (img_id.isEmpty()) {
                PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
                psDialogMsg.showErrorDialog(getString(R.string.error_message__img_not_selected), getString(R.string.message__ok_close));
                psDialogMsg.show();

                result = false;
            } else if (img_desc.equals(binding.get().descEditText.getText().toString())) {
                PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
                psDialogMsg.showErrorDialog("This photo is already added", getString(R.string.app__ok));
                psDialogMsg.show();
                result = false;
            }
        }

        if (!connectivity.isConnected()) {

            PSDialogMsg psDialogMsg = new PSDialogMsg(getActivity(), false);
            psDialogMsg.showErrorDialog(getString(R.string.error_message__no_internet), getString(R.string.message__ok_close));
            psDialogMsg.show();

            result = false;
        }

        return result;
    }

    protected void closeActivity() {

        navigationController.navigateBackToUpdateCategoryActivity(getActivity(), Constants.SUCCESSFUL, img_id);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}

