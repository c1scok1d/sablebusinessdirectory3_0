package com.macinternetservices.sablebusinessdirectory.viewmodel.item;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.macinternetservices.sablebusinessdirectory.repository.item.ItemRepository;
import com.macinternetservices.sablebusinessdirectory.utils.AbsentLiveData;
import com.macinternetservices.sablebusinessdirectory.viewmodel.common.PSViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.ItemParameterHolder;

import java.util.List;

import javax.inject.Inject;

public class RejectedItemViewModel extends PSViewModel {

    private final LiveData<Resource<List<Item>>> itemListByKeyData;
    private final MutableLiveData<RejectedItemViewModel.ItemTmpDataHolder> itemListByKeyObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageItemListByKeyData;
    private final MutableLiveData<RejectedItemViewModel.ItemTmpDataHolder> nextPageItemListByKeyObj = new MutableLiveData<>();

    public ItemParameterHolder holder = new ItemParameterHolder();
    public String itemId = "";
    public String cityId = "";
    public String locationId = "";
    public String lat, lng;


    public String userId = "";


    @Inject
    RejectedItemViewModel(ItemRepository repository) {

        itemListByKeyData = Transformations.switchMap(itemListByKeyObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getItemListByKey(obj.loginUserId, obj.limit, obj.offset, obj.itemParameterHolder, obj.lat, obj.lng);

        });

        nextPageItemListByKeyData = Transformations.switchMap(nextPageItemListByKeyObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageProductListByKey(obj.itemParameterHolder, obj.loginUserId, obj.limit, obj.offset, obj.lat, obj.lng);

        });


    }

    public void setItemListByKeyObj(String loginUserId, String limit, String offset, ItemParameterHolder parameterHolder, String lat, String lng) {
        if (!isLoading) {
            ItemTmpDataHolder tmpDataHolder = new ItemTmpDataHolder(limit, offset, loginUserId, parameterHolder, lat, lng);

            this.itemListByKeyObj.setValue(tmpDataHolder);
            setLoadingState(true);

        }
    }

    public LiveData<Resource<List<Item>>> getItemListByKeyData() {
        return itemListByKeyData;
    }

    public void setNextPageItemListByKeyObj(String limit, String offset, String loginUserId, ItemParameterHolder parameterHolder, String lat, String lng) {

        if (!isLoading) {
            ItemTmpDataHolder tmpDataHolder = new ItemTmpDataHolder(limit, offset, loginUserId, parameterHolder, lat, lng);

            setLoadingState(true);

            this.nextPageItemListByKeyObj.setValue(tmpDataHolder);
        }
    }

    public LiveData<Resource<Boolean>> getNextPageItemListByKeyData() {
        return nextPageItemListByKeyData;
    }

    //endregion

    static class ItemTmpDataHolder {

        private String limit, offset, loginUserId, lat, lng;
        private ItemParameterHolder itemParameterHolder;

        ItemTmpDataHolder(String limit, String offset, String loginUserId, ItemParameterHolder itemParameterHolder, String lat, String lng) {
            this.limit = limit;
            this.offset = offset;
            this.loginUserId = loginUserId;
            this.itemParameterHolder = itemParameterHolder;
            this.lat = lat;
            this.lng = lng;
        }
    }

}



