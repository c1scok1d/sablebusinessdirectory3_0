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

public class DiscountItemViewModel extends PSViewModel {

    private final LiveData<Resource<List<Item>>> discountItemListByKeyData;
    private final MutableLiveData<DiscountItemViewModel.ItemTmpDataHolder> itemListByKeyObj = new MutableLiveData<>();

    private final LiveData<Resource<Boolean>> nextPageDiscountItemListByKeyData;
    private final MutableLiveData<DiscountItemViewModel.ItemTmpDataHolder> nextPageItemListByKeyObj = new MutableLiveData<>();

    public ItemParameterHolder discountItemParameterHolder = new ItemParameterHolder().getDiscountItem();

    @Inject
    DiscountItemViewModel(ItemRepository repository)
    {

        discountItemListByKeyData = Transformations.switchMap(itemListByKeyObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getItemListByKey(obj.loginUserId, obj.limit, obj.offset, obj.itemParameterHolder, obj.lat, obj.lng);

        });

        nextPageDiscountItemListByKeyData = Transformations.switchMap(nextPageItemListByKeyObj, obj -> {

            if (obj == null) {
                return AbsentLiveData.create();
            }

            return repository.getNextPageProductListByKey(obj.itemParameterHolder, obj.loginUserId, obj.limit, obj.offset, obj.lat, obj.lng);

        });
    }

    //region getItemList

    public void setDiscountItemListByKeyObj(String loginUserId, String limit, String offset, ItemParameterHolder parameterHolder, String lat, String lng) {

        DiscountItemViewModel.ItemTmpDataHolder tmpDataHolder = new DiscountItemViewModel.ItemTmpDataHolder(limit, offset, loginUserId, parameterHolder, lat, lng);

        this.itemListByKeyObj.setValue(tmpDataHolder);

    }

    public LiveData<Resource<List<Item>>> getDiscountItemListByKeyData() {
        return discountItemListByKeyData;
    }

    public void setNextPageDiscountItemListByKeyObj(String limit, String offset, String loginUserId, ItemParameterHolder parameterHolder, String lat, String lng) {

        DiscountItemViewModel.ItemTmpDataHolder tmpDataHolder = new DiscountItemViewModel.ItemTmpDataHolder(limit, offset, loginUserId, parameterHolder, lat, lng);

        this.nextPageItemListByKeyObj.setValue(tmpDataHolder);
    }

    public LiveData<Resource<Boolean>> getNextPageDiscountItemListByKeyData() {
        return nextPageDiscountItemListByKeyData;
    }

    //endregion

    class ItemTmpDataHolder {

        private String limit, offset, loginUserId, lat, lng;
        private ItemParameterHolder itemParameterHolder;

        public ItemTmpDataHolder(String limit, String offset, String loginUserId, ItemParameterHolder itemParameterHolder, String lat, String lng) {
            this.limit = limit;
            this.offset = offset;
            this.loginUserId = loginUserId;
            this.itemParameterHolder = itemParameterHolder;
            this.lat = lat;
            this.lng = lng;
        }
    }
}

