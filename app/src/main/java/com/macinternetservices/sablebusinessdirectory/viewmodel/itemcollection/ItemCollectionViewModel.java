package com.macinternetservices.sablebusinessdirectory.viewmodel.itemcollection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.macinternetservices.sablebusinessdirectory.repository.itemcollection.ItemCollectionRepository;
import com.macinternetservices.sablebusinessdirectory.utils.AbsentLiveData;
import com.macinternetservices.sablebusinessdirectory.viewmodel.common.PSViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCollectionHeader;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class ItemCollectionViewModel extends PSViewModel {

    private final LiveData<Resource<List<ItemCollectionHeader>>> allItemCollecitonData;
    private final MutableLiveData<ItemCollectionViewModel.CollectionTmpDataHolder> allItemCollectionObj = new MutableLiveData<>();

    //Get Items By Collection Id

    private final LiveData<Resource<List<Item>>> itemsByCollectionIdData;
    private final MutableLiveData<ItemCollectionViewModel.ItemsByCollectionTmpDataHolder> itemsByCollectionIdObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageitemsByCollectionIdData;
    private final MutableLiveData<ItemCollectionViewModel.ItemsByCollectionTmpDataHolder> nextPageitemsByCollectionIdObj = new MutableLiveData<>();

    //header list
    private final LiveData<Resource<List<ItemCollectionHeader>>> collecitonHeaderListData;
    private final MutableLiveData<ItemCollectionViewModel.CollectionTmpDataHolder> collecitonHeaderListObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageCollectionHeaderListData;
    private MutableLiveData<ItemCollectionViewModel.CollectionHeaderTmpDataHolder> nextPageCollectionHeaderListObj = new MutableLiveData<>();


    @Inject
    ItemCollectionViewModel(ItemCollectionRepository repository)
    {

        allItemCollecitonData = Transformations.switchMap(allItemCollectionObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getAllCollectionList(obj.cityId, obj.limit, obj.offset);

        });

        itemsByCollectionIdData = Transformations.switchMap(itemsByCollectionIdObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getItemsByCollectionId(obj.limit, obj.offset, obj.collectionId);

        });

        nextPageitemsByCollectionIdData = Transformations.switchMap(nextPageitemsByCollectionIdObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageItemsByCollectionId(obj.limit, obj.offset, obj.collectionId);

        });

        collecitonHeaderListData = Transformations.switchMap(collecitonHeaderListObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getCollectionHeaderList(obj.cityId, obj.limit, obj.offset);

        });

        nextPageCollectionHeaderListData = Transformations.switchMap(nextPageCollectionHeaderListObj, obj -> {
            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageCollectionHeaderList(obj.limit, obj.offset, obj.cityId);
        });

    }

    public void setAllItemCollectionObj(String cityId, String limit, String offset)
    {
        CollectionTmpDataHolder collectionTmpDataHolder = new CollectionTmpDataHolder(cityId, limit, offset);

        allItemCollectionObj.setValue(collectionTmpDataHolder);
    }

    public LiveData<Resource<List<ItemCollectionHeader>>> getAllItemCollectionHeader(){

        return allItemCollecitonData;
    }

    //Get Items By Collection Id

    public void setItemsByCollectionIdObj(String limit, String offset, String collectionId)
    {
        ItemsByCollectionTmpDataHolder tmpDataHolder = new ItemsByCollectionTmpDataHolder(collectionId, limit, offset);

        itemsByCollectionIdObj.setValue(tmpDataHolder);

        setLoadingState(true);
    }

    public LiveData<Resource<List<Item>>> getItemsByCollectionIdData(){

        return itemsByCollectionIdData;
    }

    public void setNextPageitemsByCollectionIdObj(String limit, String offset, String collectionId)
    {
        if(!isLoading)
        {
            ItemsByCollectionTmpDataHolder tmpDataHolder = new ItemsByCollectionTmpDataHolder(collectionId, limit, offset);

            nextPageitemsByCollectionIdObj.setValue(tmpDataHolder);

            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageitemsByCollectionIdData() {
        return nextPageitemsByCollectionIdData;
    }

    //endregion

    //Get header list

    public void setCollectionHeaderListObj(String cityId, String limit, String offset)
    {
        CollectionTmpDataHolder collectionTmpDataHolder = new CollectionTmpDataHolder(cityId, limit, offset);

        collecitonHeaderListObj.setValue(collectionTmpDataHolder);
    }

    public LiveData<Resource<List<ItemCollectionHeader>>> getCollectionHeaderList(){

        return collecitonHeaderListData;
    }

    public void setNextPageCollectionHeaderListObj(String limit, String offset, String cityId) {

        if (!isLoading) {
            ItemCollectionViewModel.CollectionHeaderTmpDataHolder tmpDataHolder = new ItemCollectionViewModel.CollectionHeaderTmpDataHolder(limit,offset,cityId);

            nextPageCollectionHeaderListObj.setValue(tmpDataHolder);
            // start loading
            setLoadingState(true);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageCollectionHeaderListData() {
        return nextPageCollectionHeaderListData;
    }

    //endregion

    //Get Items By Collection Id

    class CollectionTmpDataHolder{

        String cityId, limit, offset;

        public CollectionTmpDataHolder(String cityId, String limit, String offset) {
            this.cityId = cityId;
            this.limit = limit;
            this.offset = offset;
        }
    }

    class ItemsByCollectionTmpDataHolder{

        String collectionId, limit, offset;

        public ItemsByCollectionTmpDataHolder(String collectionId, String limit, String offset) {
            this.collectionId = collectionId;
            this.limit = limit;
            this.offset = offset;
        }
    }

    class CollectionHeaderTmpDataHolder{

        String cityId, limit, offset;

        public CollectionHeaderTmpDataHolder( String limit, String offset,String cityId) {
            this.cityId = cityId;
            this.limit = limit;
            this.offset = offset;
        }
    }


}
