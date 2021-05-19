package com.macinternetservices.sablebusinessdirectory.viewmodel.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.macinternetservices.sablebusinessdirectory.repository.item.ItemRepository;
import com.macinternetservices.sablebusinessdirectory.utils.AbsentLiveData;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.common.PSViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemSpecs;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class SpecsViewModel extends PSViewModel {
    //for product specs list

    public boolean isSpecsData = false;
    public String id = "";
    public String specificationName = "";
    public String specificationDescription = "";
    public String itemId = "";

    private final LiveData<List<ItemSpecs>> specsListData;
    private MutableLiveData<SpecsViewModel.TmpDataHolder> specsObj = new MutableLiveData<>();

    private final LiveData<Resource<List<ItemSpecs>>> itemSpecsListLiveData;
    private MutableLiveData<SpecsViewModel.TmpDataHolder> itemSpecsListObj = new MutableLiveData<>();

    private LiveData<Resource<Boolean>> nextPageLoadingStateItemSpecificationData;
    private MutableLiveData<SpecsViewModel.TmpDataHolder> nextPageLoadingStateItemSpecificationObj = new MutableLiveData<>();

    //Add Specification
    private LiveData<Resource<ItemSpecs>> saveSpecificationData;
    private MutableLiveData<SpecsViewModel.saveTmpDataHolder> saveSpecificationObj = new MutableLiveData<>();

    //Delete Specification
    private LiveData<Resource<Boolean>> deleteSpecificationData;
    private MutableLiveData<SpecsViewModel.deleteTmpDataHolder> deleteSpecificationObj = new MutableLiveData<>();

    //region Constructor

    @Inject
    SpecsViewModel(ItemRepository itemRepository) {
        //  product specs List
        specsListData = Transformations.switchMap(specsObj, (SpecsViewModel.TmpDataHolder obj) -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("product color List.");
            return itemRepository.getAllSpecifications(obj.productId);
        });

        //add specification
        saveSpecificationData = Transformations.switchMap(saveSpecificationObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("Add Specification Repository");
            return itemRepository.uploadSpecification(obj.itemId, obj.name,obj.description, obj.id);
        });

        //delete specification
        deleteSpecificationData = Transformations.switchMap(deleteSpecificationObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("Featured attribute header List.");
            return itemRepository.deleteSpecification(obj.itemId, obj.id);
        });
        //get all specification by item Id
        itemSpecsListLiveData = Transformations.switchMap(itemSpecsListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            return itemRepository.getAllSpecificationList(obj.itemId, obj.limit, obj.offset);
        });

        //next page specification by item id
        nextPageLoadingStateItemSpecificationData = Transformations.switchMap(nextPageLoadingStateItemSpecificationObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }
            Utils.psLog("Specification List.");
            return itemRepository.getNextPageSpecification(obj.itemId, obj.limit, obj.offset);
        });
    }

    //endregion
    //region setSpecsListObj

    public void setSpecsListObj(String productId) {

        SpecsViewModel.TmpDataHolder tmpDataHolder = new SpecsViewModel.TmpDataHolder();
        tmpDataHolder.productId = productId;
        specsObj.setValue(tmpDataHolder);

    }

    public LiveData<List<ItemSpecs>> getSpecsListData() {
        return specsListData;
    }

    //endregion

    //region Delete Specification
    public void setDeleteSpecificationObj(String itemId, String id)
    {
        SpecsViewModel.deleteTmpDataHolder tmpDataHolder = new SpecsViewModel.deleteTmpDataHolder(itemId, id);

        deleteSpecificationObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<Boolean>> getDeleteSpecificationData()
    {
        return deleteSpecificationData;
    }
    //endregion

    //region Methods get all specification by itemId
    public void setSpecificObj(String itemId, String limit, String offset) {
        if(!isLoading) {
            setLoadingState(true);
            SpecsViewModel.TmpDataHolder tmpDataHolder = new SpecsViewModel.TmpDataHolder();
            tmpDataHolder.itemId = itemId;
            tmpDataHolder.limit = limit;
            tmpDataHolder.offset = offset;

            itemSpecsListObj.setValue(tmpDataHolder);


        }
    }

    public LiveData<Resource<List<ItemSpecs>>> getSpecificObj() {

        return itemSpecsListLiveData;
    }
    //endregion

    //Get Specification List Next Page
    public void setNextPageLoadingStateObj(String itemId, String limit, String offset) {

        if (!isLoading) {
            SpecsViewModel.TmpDataHolder tmpDataHolder = new SpecsViewModel.TmpDataHolder();
            tmpDataHolder.offset = offset;
            tmpDataHolder.itemId = itemId;
            tmpDataHolder.limit = limit;

            // start loading
            setLoadingState(true);

            nextPageLoadingStateItemSpecificationObj.setValue(tmpDataHolder);


        }
    }

    public LiveData<Resource<Boolean>> getNextPageLoadingStateData() {
        return nextPageLoadingStateItemSpecificationData;
    }


    //region Add Specification
    public void setAddSpecificationObj(String itemId, String name, String description, String id)
    {
        SpecsViewModel.saveTmpDataHolder tmpDataHolder = new SpecsViewModel.saveTmpDataHolder(itemId, name, description, id);

        saveSpecificationObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<ItemSpecs>> getSaveSpecificationData()
    {
        return saveSpecificationData;
    }



    //region Holder

    class TmpDataHolder {
        public String offset = "";
        public String productId = "";
        public Boolean isConnected = false;
        public String limit = "";
        public String itemId = "";
    }
    //endregion

    //region Add Specification Holder
    class saveTmpDataHolder {
        String itemId, name, description, id;

        private saveTmpDataHolder(String itemId, String name, String description, String id) {
            this.itemId = itemId;
            this.name = name;
            this.description = description;
            this.id = id;
        }
    }
    //endregion

    //region Delete Specification Holder
    class deleteTmpDataHolder {
        String itemId, id;

        private deleteTmpDataHolder(String itemId, String id) {
            this.itemId = itemId;
            this.id = id;
        }
    }
    //endregion
}
