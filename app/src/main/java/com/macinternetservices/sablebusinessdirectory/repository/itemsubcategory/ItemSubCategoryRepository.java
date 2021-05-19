package com.macinternetservices.sablebusinessdirectory.repository.itemsubcategory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.macinternetservices.sablebusinessdirectory.AppExecutors;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.api.ApiResponse;
import com.macinternetservices.sablebusinessdirectory.api.PSApiService;
import com.macinternetservices.sablebusinessdirectory.db.ItemSubCategoryDao;
import com.macinternetservices.sablebusinessdirectory.db.PSCoreDb;
import com.macinternetservices.sablebusinessdirectory.repository.common.NetworkBoundResource;
import com.macinternetservices.sablebusinessdirectory.repository.common.PSRepository;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemSubCategory;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.util.List;

import javax.inject.Inject;

public class ItemSubCategoryRepository extends PSRepository {

    private final ItemSubCategoryDao itemSubCategoryDao;

    @Inject
    ItemSubCategoryRepository(PSApiService psApiService, AppExecutors appExecutors, PSCoreDb db, ItemSubCategoryDao subCategoryDao) {
        super(psApiService, appExecutors, db);

        Utils.psLog("Inside SubCategoryRepository");

        this.itemSubCategoryDao = subCategoryDao;
    }

    public LiveData<Resource<List<ItemSubCategory>>> getAllItemSubCategoryList(String apiKey, String cityId) {
        return new NetworkBoundResource<List<ItemSubCategory>, List<ItemSubCategory>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<ItemSubCategory> itemList) {

                Utils.psLog("SaveCallResult of getAllSubCategoryList.");

                try {
                    db.runInTransaction(() -> {

                        itemSubCategoryDao.deleteAllSubCategory();

                        itemSubCategoryDao.insertAll(itemList);

                        for (ItemSubCategory item : itemList) {
                            itemSubCategoryDao.insert(new ItemSubCategory(item.id, item.name, item.status, item.addedDate, item.addedUserId, item.updatedDate, item.cityId, item.catId, item.deletedFlag, item.updatedUserId, item.updatedFlag, item.addedDateStr, item.defaultPhoto, item.defaultIcon));
                        }

                    });
                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of getAllSubCategoryList.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ItemSubCategory> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemSubCategory>> loadFromDb() {
                return itemSubCategoryDao.getAllSubCategory();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemSubCategory>>> createCall() {
                return psApiService.getAllSubCategoryList(apiKey, cityId);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> itemSubCategoryDao.deleteAllSubCategory()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    public LiveData<Resource<List<ItemSubCategory>>> getSubCategoryList(String apiKey, String cityId, String catId, String limit, String offset) {
        return new NetworkBoundResource<List<ItemSubCategory>, List<ItemSubCategory>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<ItemSubCategory> itemList) {

                Utils.psLog("SaveCallResult of getSubCategoryList.");

                try {
                    db.runInTransaction(() -> {
                        itemSubCategoryDao.deleteAllSubCategory();

                        itemSubCategoryDao.insertAll(itemList);

                        for (ItemSubCategory item : itemList) {
                            itemSubCategoryDao.insert(new ItemSubCategory(item.id, item.name, item.status, item.addedDate, item.addedUserId, item.updatedDate, item.cityId, item.catId, item.deletedFlag, item.updatedUserId, item.updatedFlag, item.addedDateStr, item.defaultPhoto, item.defaultIcon));
                        }

                    });
                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of recent product list.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ItemSubCategory> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemSubCategory>> loadFromDb() {
                return itemSubCategoryDao.getSubCategoryList(catId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemSubCategory>>> createCall() {
                return psApiService.getSubCategoryList(apiKey, cityId, catId, limit, offset);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemSubCategoryDao().deleteAllSubCategory()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageSubCategory(String cityId, String catId, String limit, String offset) {
        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<ItemSubCategory>>> apiResponse = psApiService.getSubCategoryList(Config.API_KEY, cityId, catId, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {


                    try {
                        db.runInTransaction(() -> {

                            if (response.body != null) {
                                for (ItemSubCategory item : response.body) {
                                    itemSubCategoryDao.insert(new ItemSubCategory(item.id, item.name, item.status, item.addedDate, item.addedUserId, item.updatedDate, item.cityId, item.catId, item.deletedFlag, item.updatedUserId, item.updatedFlag, item.addedDateStr, item.defaultPhoto, item.defaultIcon));
                                }

                                db.itemSubCategoryDao().insertAll(response.body);
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


    public LiveData<Resource<List<ItemSubCategory>>> getSubCategoriesWithCatId(String cityId, String offset, String catId) {
        return new NetworkBoundResource<List<ItemSubCategory>, List<ItemSubCategory>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<ItemSubCategory> itemList) {

                Utils.psLog("SaveCallResult of Sub Category.");

                try {
                    db.runInTransaction(() -> {

                        itemSubCategoryDao.deleteAllSubCategory();
                        itemSubCategoryDao.insertAll(itemList);

                        for (ItemSubCategory item : itemList) {
                            itemSubCategoryDao.insert(new ItemSubCategory(item.id, item.name, item.status, item.addedDate, item.addedUserId, item.updatedDate, item.cityId, item.catId, item.deletedFlag, item.updatedUserId, item.updatedFlag, item.addedDateStr, item.defaultPhoto, item.defaultIcon));
                        }

                    });
                } catch (Exception e) {
                    Utils.psErrorLog("Error in doing transaction of recent product list.", e);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<ItemSubCategory> data) {
                return connectivity.isConnected();
            }

            @NonNull
            @Override
            protected LiveData<List<ItemSubCategory>> loadFromDb() {
                return itemSubCategoryDao.getSubCategoryList(catId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<ItemSubCategory>>> createCall() {
                return psApiService.getSubCategoryListWithCatId(Config.API_KEY, cityId, catId, "", offset);
            }

            @Override
            protected void onFetchFailed(int code, String message) {
                Utils.psLog("Fetch Failed of " + message);

                if (code == Config.ERROR_CODE_10001) {
                    try {
                        appExecutors.diskIO().execute(() -> db.runInTransaction(() -> db.itemSubCategoryDao().deleteAllSubCategory()));

                    } catch (Exception ex) {
                        Utils.psErrorLog("Error at ", ex);
                    }
                }
            }

        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> getNextPageSubCategoriesWithCatId(String cityId, String limit, String offset, String catId) {
        final MediatorLiveData<Resource<Boolean>> statusLiveData = new MediatorLiveData<>();
        LiveData<ApiResponse<List<ItemSubCategory>>> apiResponse = psApiService.getSubCategoryListWithCatId(Config.API_KEY, cityId, catId, limit, offset);

        statusLiveData.addSource(apiResponse, response -> {

            statusLiveData.removeSource(apiResponse);

            //noinspection Constant Conditions
            if (response.isSuccessful()) {

                appExecutors.diskIO().execute(() -> {


                    try {
                        db.runInTransaction(() -> {

                            if (response.body != null) {
                                for (ItemSubCategory item : response.body) {
                                    itemSubCategoryDao.insert(new ItemSubCategory(item.id, item.name, item.status, item.addedDate, item.addedUserId, item.updatedDate, item.cityId, item.catId, item.deletedFlag, item.updatedUserId, item.updatedFlag, item.addedDateStr, item.defaultPhoto, item.defaultIcon));
                                }

                                itemSubCategoryDao.insertAll(response.body);
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
