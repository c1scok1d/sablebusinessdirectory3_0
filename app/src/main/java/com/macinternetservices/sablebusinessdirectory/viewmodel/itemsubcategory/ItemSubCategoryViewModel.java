package com.macinternetservices.sablebusinessdirectory.viewmodel.itemsubcategory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.repository.itemsubcategory.ItemSubCategoryRepository;
import com.macinternetservices.sablebusinessdirectory.utils.AbsentLiveData;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.common.PSViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemSubCategory;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class ItemSubCategoryViewModel extends PSViewModel {

    private LiveData<Resource<List<ItemSubCategory>>> allSubCategoryListData;
    private MutableLiveData<TmpDataHolder> allSubCategoryListObj = new MutableLiveData<>();

    private LiveData<Resource<List<ItemSubCategory>>> subCategoryListData;
    private MutableLiveData<TmpDataHolder> subCategoryListObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> nextPageLoadingStateData;
    private MutableLiveData<TmpDataHolder> nextPageLoadingStateObj = new MutableLiveData<>();

    private final LiveData<Resource<List<ItemSubCategory>>> subCategoryListByCatIdData;
    private MutableLiveData<ListByCatIdTmpDataHolder> subCategoryListByCatIdObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageSubCategoryListByCatIdData;
    private MutableLiveData<ListByCatIdTmpDataHolder> nextPageSubCategoryListByCatIdObj = new MutableLiveData<>();

    public String catId = "";

    @Inject
    ItemSubCategoryViewModel(ItemSubCategoryRepository repository) {
        Utils.psLog("Inside SubCategoryViewModel");

        allSubCategoryListData = Transformations.switchMap(allSubCategoryListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("allSubCategoryListData");
            return repository.getAllItemSubCategoryList(Config.API_KEY, obj.cityId);
        });

        subCategoryListData = Transformations.switchMap(subCategoryListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("subCategoryListData");
            return repository.getSubCategoryList(Config.API_KEY, obj.cityId,obj.catId, obj.limit, obj.offset);
        });

        nextPageLoadingStateData = Transformations.switchMap(nextPageLoadingStateObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("nextPageLoadingStateData");
            return repository.getNextPageSubCategory(obj.cityId,obj.catId, obj.limit, obj.offset);
        });

        subCategoryListByCatIdData = Transformations.switchMap(subCategoryListByCatIdObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("ItemCategoryViewModel : categories");
            return repository.getSubCategoriesWithCatId(obj.cityId, obj.offset, obj.catId);
        });

        nextPageSubCategoryListByCatIdData = Transformations.switchMap(nextPageSubCategoryListByCatIdObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            Utils.psLog("Category List.");
            return repository.getNextPageSubCategoriesWithCatId(obj.cityId, obj.limit, obj.offset, obj.catId);
        });
    }

    //list by cat id
    public void setSubCategoryListByCatIdObj(String cityId, String catId, String limit, String offset){

        ListByCatIdTmpDataHolder tmpDataHolder = new ListByCatIdTmpDataHolder(limit, offset, cityId, catId);

        subCategoryListByCatIdObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<List<ItemSubCategory>>> getSubCategoryListByCatIdData()
    {
        return subCategoryListByCatIdData;
    }

    public void setNextPageSubCategoryListByCatIdObj(String cityId, String limit, String offset, String catId)
    {
        ListByCatIdTmpDataHolder tmpDataHolder = new ListByCatIdTmpDataHolder(limit, offset, cityId, catId);

        nextPageSubCategoryListByCatIdObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<Boolean>> getNextPageSubCategoryListByCatIdData() {
        return nextPageSubCategoryListByCatIdData;
    }
    //endregion

    public void setAllSubCategoryListObj(String cityId) {
        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.cityId = cityId;
            allSubCategoryListObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<List<ItemSubCategory>>> getAllSubCategoryListData() {
        return allSubCategoryListData;
    }


    public void setSubCategoryListData(String cityId,String catId, String limit, String offset) {
        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.cityId = cityId;
            tmpDataHolder.catId = catId;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;
            subCategoryListObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<List<ItemSubCategory>>> getSubCategoryListData() {
        return subCategoryListData;
    }

    public void setNextPageLoadingStateObj(String cityId,String catId, String limit, String offset) {

        if (!isLoading) {
            TmpDataHolder tmpDataHolder = new TmpDataHolder();
            tmpDataHolder.cityId = cityId;
            tmpDataHolder.catId = catId;
            tmpDataHolder.offset = offset;
            tmpDataHolder.limit = limit;
            nextPageLoadingStateObj.setValue(tmpDataHolder);

            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageLoadingStateData() {
        return nextPageLoadingStateData;
    }


    class TmpDataHolder {
        public String loginUserId = "";
        public String cityId = "";
        public String offset = "";
        public String limit = "";
        public String catId = "";
        public Boolean isConnected = false;


    }

    class ListByCatIdTmpDataHolder {
        public String limit = "";
        public String offset = "";
        public String cityId = "";
        public String catId = "";

        public ListByCatIdTmpDataHolder(String limit, String offset, String cityId, String catId) {
            this.limit = limit;
            this.offset = offset;
            this.cityId = cityId;
            this.catId = catId;
        }
    }

}
