package com.macinternetservices.sablebusinessdirectory.viewobject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class ItemSubCategory implements Parcelable {

    @NonNull
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("status")
    public String status;

    @SerializedName("added_date")
    public String addedDate;

    @SerializedName("added_user_id")
    public String addedUserId;

    @SerializedName("updated_date")
    public String updatedDate;

    @SerializedName("city_id")
    public String cityId;

    @SerializedName("cat_id")
    public String catId;

    @SerializedName("deleted_flag")
    public String deletedFlag;

    @SerializedName("updated_user_id")
    public String updatedUserId;

    @SerializedName("updated_flag")
    public String updatedFlag;

    @SerializedName("added_date_str")
    public String addedDateStr;

    @Embedded(prefix = "default_photo_")
    @SerializedName("default_photo")
    public Image defaultPhoto;

    @Embedded(prefix = "default_icon_")
    @SerializedName("default_icon")
    public Image defaultIcon;

    public ItemSubCategory(String id, String name, String status, String addedDate, String addedUserId, String updatedDate, String cityId, String catId, String deletedFlag, String updatedUserId, String updatedFlag, String addedDateStr, Image defaultPhoto, Image defaultIcon) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.addedDate = addedDate;
        this.addedUserId = addedUserId;
        this.updatedDate = updatedDate;
        this.cityId = cityId;
        this.catId = catId;
        this.deletedFlag = deletedFlag;
        this.updatedUserId = updatedUserId;
        this.updatedFlag = updatedFlag;
        this.addedDateStr = addedDateStr;
        this.defaultPhoto = defaultPhoto;
        this.defaultIcon = defaultIcon;
    }

    protected ItemSubCategory(Parcel in) {
        id = in.readString();
        name = in.readString();
        status = in.readString();
        addedDate = in.readString();
        addedUserId = in.readString();
        updatedDate = in.readString();
        cityId = in.readString();
        catId = in.readString();
        deletedFlag = in.readString();
        updatedUserId = in.readString();
        updatedFlag = in.readString();
        addedDateStr = in.readString();
    }

    public static final Creator<ItemSubCategory> CREATOR = new Creator<ItemSubCategory>() {
        @Override
        public ItemSubCategory createFromParcel(Parcel in) {
            return new ItemSubCategory(in);
        }

        @Override
        public ItemSubCategory[] newArray(int size) {
            return new ItemSubCategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(status);
        dest.writeString(addedDate);
        dest.writeString(addedUserId);
        dest.writeString(updatedDate);
        dest.writeString(cityId);
        dest.writeString(catId);
        dest.writeString(deletedFlag);
        dest.writeString(updatedUserId);
        dest.writeString(updatedFlag);
        dest.writeString(addedDateStr);
    }
}
