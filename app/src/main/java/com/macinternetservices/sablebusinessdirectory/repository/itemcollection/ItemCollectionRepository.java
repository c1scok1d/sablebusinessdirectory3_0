package com.macinternetservices.sablebusinessdirectory.repository.itemcollection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.macinternetservices.sablebusinessdirectory.AppExecutors;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.api.ApiResponse;
import com.macinternetservices.sablebusinessdirectory.api.PSApiService;
import com.macinternetservices.sablebusinessdirectory.db.PSCoreDb;
import com.macinternetservices.sablebusinessdirectory.repository.common.NetworkBoundResource;
import com.macinternetservices.sablebusinessdirectory.repository.common.PSRepository;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCollection;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCollectionHeader;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class ItemCollectionRepository extends PSRepository {
    /**
     * Constructor of PSRepository
     *
     * @param psApiService Panacea-Soft API Service Instance
     * @param appExecutors Executors Instance
     * @param db           Panacea-Soft DB
     */
    @Inject
    ItemCollectionRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db) {
        super(psApiService, appExecutors, db);
    }

    //home
    public LiveData<Resource<List<ItemCollectionHeader>>> getAllCollectionList(String cityId, String limit, String offset) {
        return new NetworkBoundResource<List<ItemCollectionHeader>, List<ItemCollectionHeader>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<ItemCollectionHeader> itemList) {

                try {

                    db.runInTransaction(() -> {

                        db.itemCollectionHeaderDao().deleteAll();

                        db.itemCollectionHeaderDao().insertAll(itemList);

                        for (ItemCollectionHeader header : itemList) {

                            db.itemCollectionHeaderDao().deleteAllBasedOnCollectionId(header.id);
                            if (header.itemList != null) {
                                for (Item item1 : header.itemList) {

                                    db.itemCollectionHeaderDao().insert(new ItemCollection(header.id, item1.id));

                                    db.itemDao().insert(item1);
                                }
                            }
                        }

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getProductionCollectionHeaderListForHome.", e);

                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<ItemCollectionHeader> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemCollectionHeader>> loadFromDb() {

                //return db.itemCollectionHeaderDao().getAll();
                MutableLiveData<List<ItemCollectionHeader>> itemCollectionHeaderList = new MutableLiveData<>();
                appExecutors.diskIO().execute(() -> {
                    List<ItemCollectionHeader> groupList = db.itemCollectionHeaderDao().getAllIncludingItemList(Config.COLLECTION_PRODUCT_LIST_LIMIT, Config.COLLECTION_PRODUCT_LIST_LIMIT,cityId);
                    appExecutors.mainThread().execute(() ->
                            itemCollectionHeaderList.setValue(groupList)
                    );
                });

                return itemCollectionHeaderList;

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemCollectionHeader>>> createCall() {
                return psApiService.getCollectionHeaderByCityId(Config.API_KEY, limit, offset, cityId);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of About Us");

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemCollectionHeaderDao().deleteAll()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }
        }.asLiveData();
    }

    //header
    public LiveData<Resource<List<ItemCollectionHeader>>> getCollectionHeaderList(String cityId, String limit, String offset) {
        return new NetworkBoundResource<List<ItemCollectionHeader>, List<ItemCollectionHeader>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<ItemCollectionHeader> itemList) {
                Utils.psLog("SaveCallResult of getProductionCollectionHeaderListForHome.");



                try {
                    db.runInTransaction(() -> {
                        db.itemCollectionHeaderDao().deleteAll();

                        db.itemCollectionHeaderDao().insertAll(itemList);

                        for (ItemCollectionHeader header : itemList) {

                            db.itemCollectionHeaderDao().deleteAllBasedOnCollectionId(header.id);
                            if (header.itemList != null) {
                                for (Item item : header.itemList) {
                                    db.itemCollectionHeaderDao().insert(new ItemCollection(header.id, item.id));

                                    db.itemDao().insert(item);
                                }
                            }
                        }

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getProductionCollectionHeaderListForHome.", e);

                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<ItemCollectionHeader> data) {

                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemCollectionHeader>> loadFromDb() {
                Utils.psLog("Load Featured ItemCollectionHeader From Db");

                return db.itemCollectionHeaderDao().getCollectionList();

            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemCollectionHeader>>> createCall() {

                return psApiService.getCollectionHeaderByCityId(Config.API_KEY, limit, offset, cityId);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of About Us");

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemCollectionHeaderDao().deleteAll()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }
        }.asLiveData();
    }

    //header next page
    public LiveData<Resource<Boolean>> getNextPageCollectionHeaderList(String limit, String offset, String cityId) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<ItemCollectionHeader>>> apiResponse = psApiService.getCollectionHeaderByCityId(Config.API_KEY, limit, offset, cityId);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {


                    try {
                        db.runInTransaction(() -> {

                            if (response.body != null) {
                                db.itemCollectionHeaderDao().insertAll(response.body);

                                for (ItemCollectionHeader header : response.body) {

                                    //db.itemCollectionHeaderDao().deleteAllBasedOnCollectionId(header.id);
                                    if (header.itemList != null) {
                                        for (Item item : header.itemList) {
                                            db.itemCollectionHeaderDao().insert(new ItemCollection(header.id, item.id));
                                        }
                                    }
                                }

                            }

                        });
                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    }

                    statusLiveData.postValue(Resource.success(true));
                });

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }

        });

        return statusLiveData;
    }

    //view all
    public LiveData<Resource<List<Item>>> getItemsByCollectionId(String limit, String offset, String collectionId) {
        return new NetworkBoundResource<List<Item>, List<Item>>(appExecutors) {

            @Override
            protected void saveCallResult(@NonNull List<Item> itemList) {
                Utils.psLog("SaveCallResult of getProductCollectionProducts.");



                try {
                    db.runInTransaction(() -> {
                        db.itemCollectionHeaderDao().deleteAllBasedOnCollectionId(collectionId);

                        for (Item item : itemList) {

                            db.itemCollectionHeaderDao().insert(new ItemCollection(collectionId, item.id));

                            db.itemDao().insert(item);

                        }

                    });

                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getProductCollectionProducts.", e);

                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Item> data) {

                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                Utils.psLog("Load Featured getProductCollectionProducts From Db");

                return db.itemCollectionHeaderDao().getItemListByCollectionId(collectionId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {

                return psApiService.getCollectionItems(Config.API_KEY, limit, offset, collectionId);

            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of About Us");

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemCollectionHeaderDao().deleteAllBasedOnCollectionId(collectionId)));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    //view all next page
    public LiveData<Resource<Boolean>> getNextPageItemsByCollectionId(String limit, String offset, String collectionid) {

        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<Item>>> apiResponse = psApiService.getCollectionItems(Config.API_KEY, limit, offset, collectionid);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {


                    try {
                        db.runInTransaction(() -> {
                            if (response.body != null) {
                                for (Item item : response.body) {

                                    db.itemCollectionHeaderDao().insert(new ItemCollection(collectionid, item.id));

                                    db.itemDao().insert(item);

                                }
                            }

                        });

                    } catch (NullPointerException ne) {
                        Utils.psErrorLog("Null Pointer Exception : ", ne);
                    } catch (Exception e) {
                        Utils.psErrorLog("Exception : ", e);
                    }

                    statusLiveData.postValue(Resource.success(true));
                });

            } else {
                statusLiveData.postValue(Resource.error(response.errorMessage, null));
            }

        });

        return statusLiveData;

    }

}
