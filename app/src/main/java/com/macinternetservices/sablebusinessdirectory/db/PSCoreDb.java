package com.macinternetservices.sablebusinessdirectory.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.macinternetservices.sablebusinessdirectory.db.common.Converters;
import com.macinternetservices.sablebusinessdirectory.viewobject.AboutUs;
import com.macinternetservices.sablebusinessdirectory.viewobject.Blog;
import com.macinternetservices.sablebusinessdirectory.viewobject.City;
import com.macinternetservices.sablebusinessdirectory.viewobject.CityMap;
import com.macinternetservices.sablebusinessdirectory.viewobject.Comment;
import com.macinternetservices.sablebusinessdirectory.viewobject.CommentDetail;
import com.macinternetservices.sablebusinessdirectory.viewobject.DeletedObject;
import com.macinternetservices.sablebusinessdirectory.viewobject.Image;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCategory;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCollection;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCollectionHeader;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemFavourite;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemHistory;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemMap;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemPaidHistory;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemSpecs;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemStatus;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemSubCategory;
import com.macinternetservices.sablebusinessdirectory.viewobject.Noti;
import com.macinternetservices.sablebusinessdirectory.viewobject.PSAppInfo;
import com.macinternetservices.sablebusinessdirectory.viewobject.PSAppVersion;
import com.macinternetservices.sablebusinessdirectory.viewobject.Rating;
import com.macinternetservices.sablebusinessdirectory.viewobject.User;
import com.macinternetservices.sablebusinessdirectory.viewobject.UserLogin;


/**
 * Created by Panacea-Soft on 11/20/17.
 * Contact Email : teamps.is.cool@gmail.com
 */

@Database(entities = {
        Image.class,
        User.class,
        UserLogin.class,
        AboutUs.class,
        ItemFavourite.class,
        Comment.class,
        CommentDetail.class,
        Noti.class,
        ItemHistory.class,
        Blog.class,
        Rating.class,
        PSAppInfo.class,
        PSAppVersion.class,
        DeletedObject.class,
        City.class,
        CityMap.class,
        Item.class,
        ItemMap.class,
        ItemCategory.class,
        ItemCollectionHeader.class,
        ItemCollection.class,
        ItemSubCategory.class,
        ItemSpecs.class,
        ItemStatus.class,
        ItemPaidHistory.class

}, version = 8, exportSchema = false)
//2.1 = 8
//2.0 = 7
//1.9 = 6
//1.8 = 5
//1.6 = 4
//1.5 = 4
//1.4 = 4
//1.3 = 3
//1.2 = 2


@TypeConverters({Converters.class})

public abstract class PSCoreDb extends RoomDatabase {

    abstract public UserDao userDao();

    abstract public HistoryDao historyDao();

    abstract public SpecsDao specsDao();

    abstract public AboutUsDao aboutUsDao();

    abstract public ImageDao imageDao();

    abstract public RatingDao ratingDao();

    abstract public CommentDao commentDao();

    abstract public CommentDetailDao commentDetailDao();

    abstract public NotificationDao notificationDao();

    abstract public BlogDao blogDao();

    abstract public PSAppInfoDao psAppInfoDao();

    abstract public PSAppVersionDao psAppVersionDao();

    abstract public DeletedObjectDao deletedObjectDao();

    abstract public CityDao cityDao();

    abstract public CityMapDao cityMapDao();

    abstract public ItemDao itemDao();

    abstract public ItemMapDao itemMapDao();

    abstract public ItemCategoryDao itemCategoryDao();

    abstract public ItemCollectionHeaderDao itemCollectionHeaderDao();

    abstract public ItemSubCategoryDao itemSubCategoryDao();

    abstract public ItemStatusDao itemStatusDao();

    abstract public ItemPaidHistoryDao itemPaidHistoryDao();
//    /**
//     * Migrate from:
//     * version 1 - using Room
//     * to
//     * version 2 - using Room where the {@link } has an extra field: addedDateStr
//     */
//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE news "
//                    + " ADD COLUMN addedDateStr INTEGER NOT NULL DEFAULT 0");
//        }
//    };

    /* More migration write here */
}