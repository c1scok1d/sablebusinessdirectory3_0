package com.macinternetservices.sablebusinessdirectory.viewobject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * Created by Panacea-Soft on 11/25/17.
 * Contact Email : teamps.is.cool@gmail.com
 */

@Entity(primaryKeys = "imgId")
public class Image implements Parcelable {

    @SerializedName("img_id")
    @NonNull
    public  String imgId;

    @SerializedName("img_parent_id")
    public  String imgParentId;

    @SerializedName("img_type")
    public  String imgType;

    @SerializedName("img_path")
    public  String imgPath;

    @SerializedName("img_width")
    public  String imgWidth;

    @SerializedName("img_height")
    public  String imgHeight;

    @SerializedName("img_desc")
    public  String imgDesc;

    public Image(@NonNull String imgId, String imgParentId, String imgType, String imgPath, String imgWidth, String imgHeight, String imgDesc) {
        this.imgId = imgId;
        this.imgParentId = imgParentId;
        this.imgType = imgType;
        this.imgPath = imgPath;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.imgDesc = imgDesc;

    }

    protected Image(Parcel in) {
        imgId = in.readString();
        imgParentId = in.readString();
        imgType = in.readString();
        imgPath = in.readString();
        imgWidth = in.readString();
        imgHeight = in.readString();
        imgDesc = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imgId);
        dest.writeString(imgParentId);
        dest.writeString(imgType);
        dest.writeString(imgPath);
        dest.writeString(imgWidth);
        dest.writeString(imgHeight);
        dest.writeString(imgDesc);
    }
}
