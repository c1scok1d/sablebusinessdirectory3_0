package com.macinternetservices.sablebusinessdirectory.viewobject;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RatingDetail implements Parcelable {

    @SerializedName("five_star_count")
    public final int fiveStarCount;

    @SerializedName("five_star_percent")
    public final float fiveStarPercent;

    @SerializedName("four_star_count")
    public final int fourStarCount;

    @SerializedName("four_star_percent")
    public final float fourStarPercent;

    @SerializedName("three_star_count")
    public final int threeStarCount;

    @SerializedName("three_star_percent")
    public final float threeStarPercent;

    @SerializedName("two_star_count")
    public final int twoStarCount;

    @SerializedName("two_star_percent")
    public final float twoStarPercent;

    @SerializedName("one_star_count")
    public final int oneStarCount;

    @SerializedName("one_star_percent")
    public final float oneStarPercent;

    @SerializedName("total_rating_count")
    public final int totalRatingCount;

    @SerializedName("total_rating_value")
    public final float totalRatingValue;

    public RatingDetail(int fiveStarCount, float fiveStarPercent, int fourStarCount, float fourStarPercent, int threeStarCount, float threeStarPercent, int twoStarCount, float twoStarPercent, int oneStarCount, float oneStarPercent, int totalRatingCount, float totalRatingValue) {
        this.fiveStarCount = fiveStarCount;
        this.fiveStarPercent = fiveStarPercent;
        this.fourStarCount = fourStarCount;
        this.fourStarPercent = fourStarPercent;
        this.threeStarCount = threeStarCount;
        this.threeStarPercent = threeStarPercent;
        this.twoStarCount = twoStarCount;
        this.twoStarPercent = twoStarPercent;
        this.oneStarCount = oneStarCount;
        this.oneStarPercent = oneStarPercent;
        this.totalRatingCount = totalRatingCount;
        this.totalRatingValue = totalRatingValue;
    }

    protected RatingDetail(Parcel in) {
        fiveStarCount = in.readInt();
        fiveStarPercent = in.readFloat();
        fourStarCount = in.readInt();
        fourStarPercent = in.readFloat();
        threeStarCount = in.readInt();
        threeStarPercent = in.readFloat();
        twoStarCount = in.readInt();
        twoStarPercent = in.readFloat();
        oneStarCount = in.readInt();
        oneStarPercent = in.readFloat();
        totalRatingCount = in.readInt();
        totalRatingValue = in.readFloat();
    }

    public static final Creator<RatingDetail> CREATOR = new Creator<RatingDetail>() {
        @Override
        public RatingDetail createFromParcel(Parcel in) {
            return new RatingDetail(in);
        }

        @Override
        public RatingDetail[] newArray(int size) {
            return new RatingDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(fiveStarCount);
        dest.writeFloat(fiveStarPercent);
        dest.writeInt(fourStarCount);
        dest.writeFloat(fourStarPercent);
        dest.writeInt(threeStarCount);
        dest.writeFloat(threeStarPercent);
        dest.writeInt(twoStarCount);
        dest.writeFloat(twoStarPercent);
        dest.writeInt(oneStarCount);
        dest.writeFloat(oneStarPercent);
        dest.writeInt(totalRatingCount);
        dest.writeFloat(totalRatingValue);
    }
}
