package com.macinternetservices.sablebusinessdirectory.viewmodel.item;

import android.content.ContentResolver;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.repository.item.ItemRepository;
import com.macinternetservices.sablebusinessdirectory.utils.AbsentLiveData;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.common.PSViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.Image;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.ItemParameterHolder;

import java.util.List;

import javax.inject.Inject;

public class ItemViewModel extends PSViewModel {

    private final LiveData<Resource<List<Item>>> itemListByKeyData;
    private final MutableLiveData<ItemViewModel.ItemTmpDataHolder> itemListByKeyObj = new MutableLiveData<>();

    private final LiveData<Resource<Item>> productDetailListData;
    private MutableLiveData<ItemViewModel.TmpDataHolder> productDetailObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageItemListByKeyData;
    private final MutableLiveData<ItemViewModel.ItemTmpDataHolder> nextPageItemListByKeyObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> deleteOneItemData;
    private MutableLiveData<ItemViewModel.deleteOneTmpDataHolder> deleteOneItemObj = new MutableLiveData<>();

    private LiveData<Resource<Item>> saveOneItemData;
    private MutableLiveData<ItemViewModel.saveOneTmpDataHolder> saveOneItemObj = new MutableLiveData<>();

    // for image upload
    private MutableLiveData<String> imgObj = new MutableLiveData<>();

    private ItemRepository repository;

    public ItemParameterHolder holder = new ItemParameterHolder();

    public String itemDescription = "";
    public String itemId = "";
    public String cityId = "";
    public String LAT = "";
    public String LNG = "";
    public String historyFlag = "";

    public String catSelectId = "";
    public String statusSelectId = "";
    public String subCatSelectId;
    public String itemSelectId;
    public String savedItemName = "";
    public String savedCategoryName = "";
    public String savedSubCategoryName = "";
    public String savedCityId ="";
    public String savedDescription = "";
    public String savedSearchTag = "";
    public String savedHighLightInformation = "";
    public Boolean savedIsFeatured = false;
    public String savedLatitude = "";
    public String savedLongitude = "";
    public String savedOpeningHour = "";
    public String savedClosingHour = "";
    public Boolean savedIsPromotion = false;
    public String savedPhoneOne = "";
    public String savedPhoneTwo = "";
    public String savedPhoneThree = "";
    public String savedEmail = "";
    public String savedAddress = "";
    public String savedFacebook = "";
    public String savedGooglePlus = "";
    public String savedTwitter = "";
    public String savedYoutube = "";
    public String savedInstagram = "";
    public String savedPinterest = "";
    public String savedWebsite = "";
    public String savedWhatsapp = "";
    public String savedMessenger = "";
    public String savedTimeRemark = "";
    public String flagType = "";
    public String savedTerms = "";
    public String savedCancelationPolicy = "";
    public String savedAdditionalInfo = "";
    public String savedStatusSelectedId = "";
    public boolean saved = false;
    public boolean edit_mode = false;
    public String img_id = "";
    public String img_desc = "";
    public String img_path = "";
    public String lat = "48.856452647178386";
    public String lng = "2.3523519560694695";

    public Uri ImageUri = null;

    @Inject
    ItemViewModel(ItemRepository repository)
    {
    this.repository = repository;
        itemListByKeyData = Transformations.switchMap(itemListByKeyObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getItemListByKey(obj.loginUserId, obj.limit, obj.offset, obj.itemParameterHolder);

        });

        nextPageItemListByKeyData = Transformations.switchMap(nextPageItemListByKeyObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageProductListByKey(obj.itemParameterHolder, obj.loginUserId, obj.limit, obj.offset);

        });

        //  item detail List
        productDetailListData = Transformations.switchMap(productDetailObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("product detail List.");
            return repository.getItemDetail(Config.API_KEY, obj.itemId, obj.cityId, obj.historyFlag, obj.userId);
        });

        // item delete
        deleteOneItemData = Transformations.switchMap(deleteOneItemObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.deleteOneItem(obj.itemId, obj.userId);
        });

        // item upload
        saveOneItemData = Transformations.switchMap(saveOneItemObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return repository.saveOneItem(obj.userId,obj.cityId,obj.categoryId,obj.subCategoryId,obj.status,obj.name,obj.description,obj.searchTag,obj.highlightInformation,obj.isFeatured,obj.latitude,obj.longitude,
                    obj.openingHour, obj.closingHour,obj.isPromotion,obj.phoneOne,obj.phoneTwo,obj.phoneThree,obj.email,obj.address,obj.facebook,obj.googlePlus,obj.twitter,obj.youtube,obj.instagram,
                    obj.pinterest, obj.website,obj.whatsapp,obj.messenger,obj.timeRemark,obj.terms,obj.cancelationPolicy,obj.additionalInfo,obj.itemId);
        });

    }

    //region getItemList

    public void setItemListByKeyObj(String loginUserId, String limit, String offset, ItemParameterHolder parameterHolder) {

        ItemTmpDataHolder tmpDataHolder = new ItemTmpDataHolder(limit, offset, loginUserId, parameterHolder);

        this.itemListByKeyObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<List<Item>>> getItemListByKeyData() {
        return itemListByKeyData;
    }

    public void setNextPageItemListByKeyObj(String limit, String offset, String loginUserId, ItemParameterHolder parameterHolder) {

        if(!isLoading)
        {
            ItemTmpDataHolder tmpDataHolder = new ItemTmpDataHolder(limit, offset, loginUserId, parameterHolder);

            setLoadingState(true);

            this.nextPageItemListByKeyObj.setValue(tmpDataHolder);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageItemListByKeyData() {
        return nextPageItemListByKeyData;
    }

    //endregion

    //Delete Item
    public void setDeleteOneItemObj(String itemId, String userId)
    {
        ItemViewModel.deleteOneTmpDataHolder tmpDataHolder = new ItemViewModel.deleteOneTmpDataHolder(itemId,userId);

        deleteOneItemObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<Boolean>> getDeleteOneItemData()
    {
        return deleteOneItemData;
    }
    //end region

    //item image upload

    public LiveData<Resource<Image>> uploadImage(String filePath, Uri uri, String itemId, String imageId,String imageDesc, ContentResolver contentResolver) {

        imgObj.setValue("PS");

        return Transformations.switchMap(imgObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return ItemViewModel.this.repository.uploadItemImage(filePath, uri, imageId,imageDesc, itemId, contentResolver);

        });

    }
    //endregion
    //region getter and setter for item upload
    public void setSaveOneItemObj(String userId, String cityId, String categoryId, String subCategoryId,String status, String name, String description, String searchTag, String highlightInformation,
                                  String isFeatured, String latitude, String longitude, String openHour, String closeHour, String isPromotion, String phoneOne, String phoneTwo, String phoneThree,
                                  String email, String address, String facebook, String googlePlus, String twitter, String youtube, String instagram, String pinterest, String website, String whatesapp,
                                  String messenger, String timeRemark, String terms, String cancelationPolicy, String additionalInfo,String itemId)
    {

        ItemViewModel.saveOneTmpDataHolder tmpDataHolder = new ItemViewModel.saveOneTmpDataHolder(userId,cityId,categoryId,subCategoryId,status,name,description,searchTag,highlightInformation,isFeatured,
                latitude,longitude,openHour,closeHour,isPromotion,phoneOne,phoneTwo,phoneThree,email,address,facebook,googlePlus,twitter,youtube,instagram,pinterest,website,whatesapp,messenger,
                timeRemark,terms,cancelationPolicy,additionalInfo,itemId);

        saveOneItemObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<Item>> getSaveOneItemData(){
        return saveOneItemData;
    }

    //endregion

    class ItemTmpDataHolder {

        private String limit, offset, loginUserId;
        private ItemParameterHolder itemParameterHolder;

        public ItemTmpDataHolder(String limit, String offset, String loginUserId, ItemParameterHolder itemParameterHolder) {
            this.limit = limit;
            this.offset = offset;
            this.loginUserId = loginUserId;
            this.itemParameterHolder = itemParameterHolder;
        }
    }
    //region Getter And Setter for item detail List

    public void setItemDetailObj(String itemId, String cityId, String historyFlag, String userId) {
        if (!isLoading) {
            ItemViewModel.TmpDataHolder tmpDataHolder = new ItemViewModel.TmpDataHolder();
            tmpDataHolder.itemId = itemId;
            tmpDataHolder.historyFlag = historyFlag;
            tmpDataHolder.cityId = cityId;
            tmpDataHolder.userId = userId;
            productDetailObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<Item>> getItemDetailData() {
        return productDetailListData;
    }

    //endregion

    //region Holder
    class TmpDataHolder {
        public String offset = "";
        public String itemId = "";
        public String historyFlag = "";
        public String userId = "";
        public String cityId = "";
        public Boolean isConnected = false;
    }
    //endregion

    class deleteOneTmpDataHolder {

        String itemId, userId;

        private deleteOneTmpDataHolder(String itemId, String userId) {
            this.itemId = itemId;
            this.userId = userId;
        }
    }

    class UploadItemImageTmpDataHolder {

        Uri uri;
        String filePath, itemId, imageId, imageDesc;
        ContentResolver contentResolver;

        private UploadItemImageTmpDataHolder(String filePath, Uri uri, String itemId, String imageId,
                                             String imageDesc, ContentResolver contentResolver) {
            this.filePath = filePath;
            this.uri = uri;
            this.itemId = itemId;
            this.imageId = imageId;
            this.imageDesc = imageDesc;
            this.contentResolver = contentResolver;
        }
    }

    class saveOneTmpDataHolder {

        String userId,cityId,categoryId,subCategoryId,status,name,description,searchTag,highlightInformation,isFeatured,latitude,longitude,openingHour,closingHour,isPromotion,phoneOne,phoneTwo,phoneThree,
                email,address,facebook,googlePlus,twitter,youtube,instagram,pinterest,website,whatsapp,messenger,timeRemark,terms,cancelationPolicy,additionalInfo,itemId;

        private saveOneTmpDataHolder( String userId, String cityId, String categoryId, String subCategoryId, String status, String name, String description, String searchTag, String highlightInformation,
                                      String isFeatured, String latitude, String longitude, String openHour, String closeHour, String isPromotion, String phoneOne, String phoneTwo, String phoneThree,
                                      String email, String address, String facebook, String googlePlus, String twitter, String youtube, String instagram, String pinterest, String website, String whatesapp,
                                      String messenger, String timeRemark, String terms, String cancelationPolicy, String additionalInfo,String itemId) {
            this.userId = userId;
            this.cityId = cityId;
            this.categoryId = categoryId;
            this.subCategoryId = subCategoryId;
            this.name = name;
            this.description = description;
            this.searchTag = searchTag;
            this.highlightInformation = highlightInformation;
            this.isFeatured = isFeatured;
            this.latitude = latitude;
            this.longitude = longitude;
            this.openingHour = openHour;
            this.closingHour = closeHour;
            this.isPromotion = isPromotion;
            this.phoneOne = phoneOne;
            this.phoneTwo = phoneTwo;
            this.phoneThree = phoneThree;
            this.email = email;
            this.address = address;
            this.facebook = facebook;
            this.googlePlus = googlePlus;
            this.twitter = twitter;
            this.youtube = youtube;
            this.instagram = instagram;
            this.pinterest = pinterest;
            this.website = website;
            this.whatsapp = whatesapp;
            this.messenger = messenger;
            this.timeRemark = timeRemark;
            this.terms = terms;
            this.cancelationPolicy = cancelationPolicy;
            this.additionalInfo = additionalInfo;
            this.itemId = itemId;
            this.status = status;
        }
    }
}
