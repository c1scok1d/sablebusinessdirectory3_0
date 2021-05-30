package com.macinternetservices.sablebusinessdirectory.viewobject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(primaryKeys = "id")
public class ItemSpecs implements Parcelable {
    @SerializedName("id")
    @NonNull
    public final String id;

    @SerializedName("item_id")
    public final String itemId;

    @SerializedName("name")
    public final String name;

    @SerializedName("description")
    public final String description;

    @SerializedName("added_date")
    public final String addedDate;

    @SerializedName("added_user_id")
    public final String addedUserId;

    @SerializedName("updated_date")
    public final String updatedDate;

    @SerializedName("updated_user_id")
    public final String updatedUserId;

    @SerializedName("updated_flag")
    public final String updatedFlag;

    @SerializedName("is_empty_object")
    public final String isEmptyObject;

    public ItemSpecs(@NonNull String id, String itemId, String name, String description, String addedDate, String addedUserId, String updatedDate, String updatedUserId, String updatedFlag, String isEmptyObject) {
        this.id = id;
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.addedDate = addedDate;
        this.addedUserId = addedUserId;
        this.updatedDate = updatedDate;
        this.updatedUserId = updatedUserId;
        this.updatedFlag = updatedFlag;
        this.isEmptyObject = isEmptyObject;
    }

    protected ItemSpecs(Parcel in) {
        id = in.readString();
        itemId = in.readString();
        name = in.readString();
        description = in.readString();
        addedDate = in.readString();
        addedUserId = in.readString();
        updatedDate = in.readString();
        updatedUserId = in.readString();
        updatedFlag = in.readString();
        isEmptyObject = in.readString();
    }

    public static final Creator<ItemSpecs> CREATOR = new Creator<ItemSpecs>() {
        @Override
        public ItemSpecs createFromParcel(Parcel in) {
            return new ItemSpecs(in);
        }

        @Override
        public ItemSpecs[] newArray(int size) {
            return new ItemSpecs[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(itemId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(addedDate);
        dest.writeString(addedUserId);
        dest.writeString(updatedDate);
        dest.writeString(updatedUserId);
        dest.writeString(updatedFlag);
        dest.writeString(isEmptyObject);
    }
}
