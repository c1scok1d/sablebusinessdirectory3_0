package com.macinternetservices.sablebusinessdirectory.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCollection;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCollectionHeader;
import java.util.List;

@Dao
public abstract class ItemCollectionHeaderDao{

    //region Collection Header

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<ItemCollectionHeader> ItemCollectionHeaderList);

    @Query("SELECT * FROM ItemCollectionHeader ORDER BY addedDate DESC")
    public abstract LiveData<List<ItemCollectionHeader>> getCollectionList();

    @Query("SELECT * FROM (SELECT * FROM ItemCollectionHeader ORDER BY addedDate DESC) WHERE cityId =:cityId LIMIT :limit")
    public abstract List<ItemCollectionHeader> getAllListByLimit(int limit,String cityId);

    @Query("SELECT * FROM Item WHERE id in " +
            "(SELECT itemId FROM ItemCollection WHERE collectionId = :collectionId ) " +
            "ORDER BY addedDate DESC")
    public abstract LiveData<List<Item>> getItemListByCollectionId(String collectionId);

    @Query("SELECT * FROM " +
            "(SELECT * FROM Item WHERE id in " +
            "(SELECT itemId FROM ItemCollection WHERE collectionId = :collectionId ) " +
            "ORDER BY addedDate DESC ) " +
            "LIMIT :limit")
    public abstract List<Item> getItemByCollectionIdWithLimit(String collectionId, int limit);

    @Query("DELETE FROM ItemCollectionHeader")
    public abstract void deleteAll();

    public List<ItemCollectionHeader> getAllIncludingItemList(int collectionLimit, int itemLimit, String cityId) {

        List<ItemCollectionHeader> ItemCollectionHeaderList = getAllListByLimit(collectionLimit,cityId);

        for (int i = 0; i < ItemCollectionHeaderList.size(); i++) {
            ItemCollectionHeaderList.get(i).itemList = getItemByCollectionIdWithLimit(ItemCollectionHeaderList.get(i).id, itemLimit);

            int a  = 0;

            Utils.psLog(String.valueOf(ItemCollectionHeaderList.get(i).itemList.size()));
        }

        return ItemCollectionHeaderList;

    }
    //endregion

    //region Collection

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(ItemCollection itemCollection);

    @Query("DELETE FROM ItemCollection WHERE collectionId = :id ")
    public abstract void deleteAllBasedOnCollectionId(String id);

    //endregion

}
