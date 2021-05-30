package com.macinternetservices.sablebusinessdirectory.viewobject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Panacea-Soft on 12/6/17.
 * Contact Email : teamps.is.cool@gmail.com
 */

@Entity(primaryKeys = "userId")
public class User implements Parcelable {

    @NonNull
    @SerializedName("user_id")
    public String userId;

    @SerializedName("user_is_sys_admin")
    public String userIsSysAdmin;

    @SerializedName("is_city_admin")
    public String isCityAdmin;

    @SerializedName("facebook_id")
    public String facebookId;

    @SerializedName("google_id")
    public String googleId;

    @SerializedName("user_name")
    public String userName;

    @SerializedName("user_email")
    public String userEmail;

    @SerializedName("user_phone")
    public String userPhone;

    @SerializedName("user_password")
    public String userPassword;

    @SerializedName("user_about_me")
    public String userAboutMe;

    @SerializedName("user_cover_photo")
    public String userCoverPhoto;

    @SerializedName("user_profile_photo")
    public String userProfilePhoto;

    @SerializedName("role_id")
    public String roleId;

    @SerializedName("status")
    public String status;

    @SerializedName("is_banned")
    public String isBanned;

    @SerializedName("added_date")
    public String addedDate;

    @SerializedName("device_token")
    public String deviceToken;

    public User(@NonNull String userId, String userIsSysAdmin, String isCityAdmin, String facebookId, String googleId, String userName, String userEmail, String userPhone, String userPassword, String userAboutMe, String userCoverPhoto, String userProfilePhoto, String roleId, String status, String isBanned, String addedDate, String deviceToken) {
        this.userId = userId;
        this.userIsSysAdmin = userIsSysAdmin;
        this.isCityAdmin = isCityAdmin;
        this.facebookId = facebookId;
        this.googleId = googleId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userPassword = userPassword;
        this.userAboutMe = userAboutMe;
        this.userCoverPhoto = userCoverPhoto;
        this.userProfilePhoto = userProfilePhoto;
        this.roleId = roleId;
        this.status = status;
        this.isBanned = isBanned;
        this.addedDate = addedDate;
        this.deviceToken = deviceToken;
    }

    protected User(Parcel in) {
        userId = in.readString();
        userIsSysAdmin = in.readString();
        isCityAdmin = in.readString();
        facebookId = in.readString();
        googleId = in.readString();
        userName = in.readString();
        userEmail = in.readString();
        userPhone = in.readString();
        userPassword = in.readString();
        userAboutMe = in.readString();
        userCoverPhoto = in.readString();
        userProfilePhoto = in.readString();
        roleId = in.readString();
        status = in.readString();
        isBanned = in.readString();
        addedDate = in.readString();
        deviceToken = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userIsSysAdmin);
        dest.writeString(isCityAdmin);
        dest.writeString(facebookId);
        dest.writeString(googleId);
        dest.writeString(userName);
        dest.writeString(userEmail);
        dest.writeString(userPhone);
        dest.writeString(userPassword);
        dest.writeString(userAboutMe);
        dest.writeString(userCoverPhoto);
        dest.writeString(userProfilePhoto);
        dest.writeString(roleId);
        dest.writeString(status);
        dest.writeString(isBanned);
        dest.writeString(addedDate);
        dest.writeString(deviceToken);
    }
}
