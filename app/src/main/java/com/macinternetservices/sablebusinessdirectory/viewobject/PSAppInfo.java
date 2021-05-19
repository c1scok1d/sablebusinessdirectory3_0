package com.macinternetservices.sablebusinessdirectory.viewobject;


import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(primaryKeys = "id")
public class PSAppInfo {

    @NonNull
    public String id;

    @Embedded(prefix = "version_")
    @SerializedName("version")
    public PSAppVersion psAppVersion;

    @SerializedName("user_info")
    @Embedded(prefix = "user_info_")
    public PSUserInfo userInfo;

    @SerializedName("delete_history")
    @Ignore
    public List<DeletedObject> deletedObjects;

    @SerializedName("currency_symbol")
    public String currencySymbol;

    @SerializedName("currency_short_form")
    public String currencyShortForm;

    @SerializedName("oneday")
    public String oneDay;

    @SerializedName("stripe_publishable_key")
    public String stripePublishableKey;

    @SerializedName("stripe_enabled")
    public String stripeEnabled;

    @SerializedName("paypal_enabled")
    public String paypalEnabled;

    @SerializedName("razor_enabled")
    public String razorEnabled;

    @SerializedName("razor_key")
    public String razorKey;

    public PSAppInfo(@NonNull String id, PSAppVersion psAppVersion, String oneDay, String currencySymbol, String currencyShortForm, String stripePublishableKey, String paypalEnabled, String stripeEnabled,String razorEnabled,String razorKey) {
        this.id = id;
        this.psAppVersion = psAppVersion;
        this.oneDay = oneDay;
        this.currencySymbol = currencySymbol;
        this.currencyShortForm = currencyShortForm;
        this.stripePublishableKey = stripePublishableKey;
        this.paypalEnabled = paypalEnabled;
        this.stripeEnabled = stripeEnabled;
        this.razorEnabled = razorEnabled;
        this.razorKey = razorKey;
    }
}
