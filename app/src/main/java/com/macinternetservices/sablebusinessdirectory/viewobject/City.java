package com.macinternetservices.sablebusinessdirectory.viewobject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;

@Entity(primaryKeys = "id")
public class City implements Parcelable {

    @NonNull
    @SerializedName("id")
    public String id;

    @SerializedName("shipping_id")
    public String shippingId;

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("phone")
    public String phone;

    @SerializedName("email")
    public String email;

    @SerializedName("address")
    public String address;

    @SerializedName("coordinate")
    public String coordinate;

    @SerializedName("lat")
    public String lat;

    @SerializedName("lng")
    public String lng;

    @SerializedName("cod_email")
    public String codEmail;

    @SerializedName("sender_email")
    public String senderEmail;

    @SerializedName("added_date")
    public String addedDate;

    @SerializedName("status")
    public String status;

    @SerializedName("is_featured")
    public String isFeatured;

    @SerializedName("terms")
    public String terms;

    @SerializedName("featured_date")
    public String featuredDate;

    @SerializedName("added_user_id")
    public String addedUserId;

    @SerializedName("updated_date")
    public String updatedDate;

    @SerializedName("updated_user_id")
    public String updatedUserId;

    @SerializedName("added_date_str")
    public String addedDateStr;

    @Embedded(prefix = "default_photo_")
    @SerializedName("default_photo")
    public Image defaultPhoto;

    @SerializedName("touch_count")
    public String touchCount;

    public City(@NonNull String id, String shippingId, String name, String description, String phone, String email, String address, String coordinate, String lat, String lng, String codEmail, String senderEmail, String addedDate, String status, String isFeatured, String terms, String featuredDate, String addedUserId, String updatedDate, String updatedUserId, String addedDateStr, Image defaultPhoto, String touchCount) {
        this.id = id;
        this.shippingId = shippingId;
        this.name = name;
        this.description = description;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.coordinate = coordinate;
        this.lat = lat;
        this.lng = lng;
        this.codEmail = codEmail;
        this.senderEmail = senderEmail;
        this.addedDate = addedDate;
        this.status = status;
        this.isFeatured = isFeatured;
        this.terms = terms;
        this.featuredDate = featuredDate;
        this.addedUserId = addedUserId;
        this.updatedDate = updatedDate;
        this.updatedUserId = updatedUserId;
        this.addedDateStr = addedDateStr;
        this.defaultPhoto = defaultPhoto;
        this.touchCount = touchCount;
    }

    protected City(Parcel in) {
        id = in.readString();
        shippingId = in.readString();
        name = in.readString();
        description = in.readString();
        phone = in.readString();
        email = in.readString();
        address = in.readString();
        coordinate = in.readString();
        lat = in.readString();
        lng = in.readString();
        codEmail = in.readString();
        senderEmail = in.readString();
        addedDate = in.readString();
        status = in.readString();
        isFeatured = in.readString();
        terms = in.readString();
        featuredDate = in.readString();
        addedUserId = in.readString();
        updatedDate = in.readString();
        updatedUserId = in.readString();
        addedDateStr = in.readString();
        touchCount = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(shippingId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(address);
        dest.writeString(coordinate);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(codEmail);
        dest.writeString(senderEmail);
        dest.writeString(addedDate);
        dest.writeString(status);
        dest.writeString(isFeatured);
        dest.writeString(terms);
        dest.writeString(featuredDate);
        dest.writeString(addedUserId);
        dest.writeString(updatedDate);
        dest.writeString(updatedUserId);
        dest.writeString(addedDateStr);
        dest.writeString(touchCount);
    }
}
