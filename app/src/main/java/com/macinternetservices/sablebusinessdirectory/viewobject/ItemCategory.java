package com.macinternetservices.sablebusinessdirectory.viewobject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class ItemCategory implements Parcelable {

    public int sorting;

    @NonNull
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("ordering")
    public String ordering;

    @SerializedName("status")
    public String status;

    @SerializedName("added_date")
    public String addedDate;

    @SerializedName("updated_date")
    public String updatedDate;

    @SerializedName("added_user_id")
    public String addedUserId;

    @SerializedName("city_id")
    public String cityId;

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

    public ItemCategory(int sorting, String id, String name, String ordering, String status, String addedDate, String updatedDate, String addedUserId, String cityId, String updatedUserId, String updatedFlag, String addedDateStr, Image defaultPhoto, Image defaultIcon) {

        this.sorting = sorting;
        this.id = id;
        this.name = name;
        this.ordering = ordering;
        this.status = status;
        this.addedDate = addedDate;
        this.updatedDate = updatedDate;
        this.addedUserId = addedUserId;
        this.cityId = cityId;
        this.updatedUserId = updatedUserId;
        this.updatedFlag = updatedFlag;
        this.addedDateStr = addedDateStr;
        this.defaultPhoto = defaultPhoto;
        this.defaultIcon = defaultIcon;
    }

    protected ItemCategory(Parcel in) {
        sorting = in.readInt();
        id = in.readString();
        name = in.readString();
        ordering = in.readString();
        status = in.readString();
        addedDate = in.readString();
        updatedDate = in.readString();
        addedUserId = in.readString();
        cityId = in.readString();
        updatedUserId = in.readString();
        updatedFlag = in.readString();
        addedDateStr = in.readString();
    }

    public static final Creator<ItemCategory> CREATOR = new Creator<ItemCategory>() {
        @Override
        public ItemCategory createFromParcel(Parcel in) {
            return new ItemCategory(in);
        }

        @Override
        public ItemCategory[] newArray(int size) {
            return new ItemCategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sorting);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(ordering);
        dest.writeString(status);
        dest.writeString(addedDate);
        dest.writeString(updatedDate);
        dest.writeString(addedUserId);
        dest.writeString(cityId);
        dest.writeString(updatedUserId);
        dest.writeString(updatedFlag);
        dest.writeString(addedDateStr);
    }
}
