package com.macinternetservices.sablebusinessdirectory.viewobject.holder;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;

import java.io.Serializable;

public class ItemParameterHolder implements Serializable {

    public String keyword, city_id, cat_id, sub_cat_id, order_by, order_type, rating_value, is_featured, is_promotion, lat, lng, miles, cityLat, cityLng, cityName,added_user_id,userId,isPaid, status;

    public ItemParameterHolder() {
        this.keyword = "";
        this.city_id = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.order_by = Constants.FILTERING_ADDED_DATE;
        this.order_type = Constants.FILTERING_DESC;
        this.rating_value = "";
        this.is_featured = "";
        this.is_promotion = "";
        this.lat = "";
        this.lng = "";
        this.miles = "";
        this.cityLat = "";
        this.cityLng = "";
        this.cityName = "";
        this.added_user_id = "";
        this.userId = "";
        this.isPaid = "";
        this.status = "1";
    }

    public ItemParameterHolder getFeaturedItem() {
        this.keyword = "";
        this.city_id = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.order_by = Constants.FILTERING_FEATURE;
        this.order_type = Constants.FILTERING_DESC;
        this.rating_value = "";
        this.is_featured = Constants.ONE;
        this.is_promotion = "";
        this.lat = "";
        this.lng = "";
        this.miles = "";
        this.cityLat = "";
        this.cityLng = "";
        this.cityName = "";
        this.added_user_id = "";
        this.isPaid = "";
        this.status = "1";

        return this;
    }

    public ItemParameterHolder getDiscountItem() {
        this.keyword = "";
        this.city_id = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.order_by = Constants.FILTERING_ADDED_DATE;
        this.order_type = Constants.FILTERING_DESC;
        this.rating_value = "";
        this.is_featured = "";
        this.is_promotion = Constants.ONE;
        this.lat = "";
        this.lng = "";
        this.miles = "";
        this.cityLat = "";
        this.cityLng = "";
        this.cityName = "";
        this.added_user_id = "";
        this.isPaid = "";
        this.status = "1";

        return this;
    }

    public ItemParameterHolder getPopularItem() {
        this.keyword = "";
        this.city_id = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.order_by = Constants.FILTERING_TRENDING;
        this.order_type = Constants.FILTERING_DESC;
        this.rating_value = "";
        this.is_featured = "";
        this.is_promotion = "";
        this.lat = "";
        this.lng = "";
        this.miles = "";
        this.cityLat = "";
        this.cityLng = "";
        this.cityName = "";
        this.added_user_id = "";
        this.isPaid = "";
        this.status = "1";

        return this;
    }

    public ItemParameterHolder getRecentItem() {
        this.keyword = "";
        this.city_id = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.order_by = Constants.FILTERING_ADDED_DATE;
        this.order_type = Constants.FILTERING_DESC;
        this.rating_value = "";
        this.is_featured = "";
        this.is_promotion = "";
        this.lat = "";
        this.lng = "";
        this.miles = "";
        this.cityLat = "";
        this.cityLng = "";
        this.cityName = "";
        this.added_user_id = "";
        this.isPaid =Constants.PAIDITEMFIRST;
        this.status = "1";

        return this;
    }

    public void resetTheHolder() {

        this.city_id = "";
        this.keyword = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.is_featured = "";
        this.is_promotion = "";
        this.order_by = Constants.FILTERING_ADDED_DATE;
        this.order_type = Constants.FILTERING_DESC;
        this.lat = "";
        this.lng = "";
        this.miles = "";
        this.cityLat = "";
        this.cityLng = "";
        this.cityName = "";
        this.added_user_id = "";
        this.isPaid = "";
        this.status = "1";

    }

    public ItemParameterHolder getUploadedItemClickMenu(){

        this.keyword = "";
        this.city_id = "";
        this.cat_id = "";
        this.sub_cat_id = "";
        this.order_by = Constants.FILTERING_ADDED_DATE;
        this.order_type = Constants.FILTERING_DESC;
        this.rating_value = "";
        this.is_featured = "";
        this.is_promotion = "";
        this.lat = "";
        this.lng = "";
        this.miles = "";
        this.cityLat = "";
        this.cityLng = "";
        this.cityName = "";
        this.added_user_id = "";
        this.isPaid = "";
        this.status = "1";

        return this;
    }

    public String getItemMapKey() {

        final String promotion = "promotion";
        final String featured = "featured";
        final String ratingValue = "rating_value";

        String result = "";

        if (!keyword.isEmpty()) {
            result += keyword + ":";
        }

        if (!city_id.isEmpty()) {
            result += city_id + ":";
        }

        if (!cat_id.isEmpty()) {
            result += cat_id + ":";
        }

        if (!sub_cat_id.isEmpty()) {
            result += sub_cat_id + ":";
        }

        if (!order_by.isEmpty()) {
            result += order_by + ":";
        }

        if (!order_type.isEmpty()) {
            result += order_type + ":";
        }

        if (!rating_value.isEmpty()) {
            result += ratingValue + rating_value + ":";
        }

        if (!is_featured.isEmpty()) {
            result += featured + ":";
        }

        if (!is_promotion.isEmpty()) {
            result += promotion + ":";
        }

        if (!lat.isEmpty()) {
            result += lat + ":";
        }

        if (!lng.isEmpty()) {
            result += lng + ":";
        }

        if (!miles.isEmpty()) {
            result += miles + ":";
        }

        if (!added_user_id.isEmpty()){
            result += added_user_id + ":";
        }

        if (!userId.isEmpty()) {
            result += userId + ":";
        }

        if (!isPaid.isEmpty()){
            result += isPaid + ":";
        }

        if (!status.isEmpty()){
            result += status + ":";
        }
        return result;
    }
}
