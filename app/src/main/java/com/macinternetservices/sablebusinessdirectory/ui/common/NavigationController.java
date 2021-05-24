package com.macinternetservices.sablebusinessdirectory.ui.common;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.ui.blog.detail.BlogDetailActivity;
import com.macinternetservices.sablebusinessdirectory.ui.blog.list.BlogListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.blog.listbycityid.BlogListByCityIdActivity;
import com.macinternetservices.sablebusinessdirectory.ui.category.list.CategoryListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.category.list.CategoryListFragment;
import com.macinternetservices.sablebusinessdirectory.ui.city.cityList.CityListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.city.cityList.CityListFragment;
import com.macinternetservices.sablebusinessdirectory.ui.city.detail.CityActivity;
import com.macinternetservices.sablebusinessdirectory.ui.city.menu.CityMenuFragment;
import com.macinternetservices.sablebusinessdirectory.ui.city.selectedcity.SelectedCityActivity;
import com.macinternetservices.sablebusinessdirectory.ui.city.selectedcity.SelectedCityFragment;
import com.macinternetservices.sablebusinessdirectory.ui.comment.detail.CommentDetailActivity;
import com.macinternetservices.sablebusinessdirectory.ui.comment.list.CommentListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.dashboard.DashBoardCityListFragment;
import com.macinternetservices.sablebusinessdirectory.ui.dashboard.DashBoardSearchFragment;
import com.macinternetservices.sablebusinessdirectory.ui.dashboard.DashboardSearchByCategoryActivity;
import com.macinternetservices.sablebusinessdirectory.ui.forceupdate.ForceUpdateActivity;
import com.macinternetservices.sablebusinessdirectory.ui.gallery.GalleryActivity;
import com.macinternetservices.sablebusinessdirectory.ui.gallery.detail.GalleryDetailActivity;
import com.macinternetservices.sablebusinessdirectory.ui.imageupload.ImageUploadActivity;
import com.macinternetservices.sablebusinessdirectory.ui.imageupload.itemimagelist.ItemImageListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.collection.header.CollectionHeaderListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.collection.itemCollection.ItemCollectionActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.detail.ItemActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.favourite.FavouriteListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.favourite.FavouriteListFragment;
import com.macinternetservices.sablebusinessdirectory.ui.item.history.HistoryFragment;
import com.macinternetservices.sablebusinessdirectory.ui.item.history.UserHistoryListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.map.MapActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.map.mapFilter.MapFilteringActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.promote.ItemPromoteActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.promote.LoginUserPaidItemFragment;
import com.macinternetservices.sablebusinessdirectory.ui.item.rating.RatingListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.readmore.ReadMoreActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.search.searchlist.SearchListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.search.searchlist.SearchListFragment;
import com.macinternetservices.sablebusinessdirectory.ui.item.search.specialfilterbyattributes.FilteringActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.upload.ItemUploadActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.upload.SelectionActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.upload.map.MapItemEntryActivity;
import com.macinternetservices.sablebusinessdirectory.ui.item.uploaded.ItemUploadedListFragment;
import com.macinternetservices.sablebusinessdirectory.ui.item.user_item.LoginUserItemListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.language.LanguageFragment;
import com.macinternetservices.sablebusinessdirectory.ui.notification.detail.NotificationActivity;
import com.macinternetservices.sablebusinessdirectory.ui.notification.list.NotificationListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.notification.setting.NotificationSettingActivity;
import com.macinternetservices.sablebusinessdirectory.ui.privacypolicy.PrivacyPolicyActivity;
import com.macinternetservices.sablebusinessdirectory.ui.privacypolicy.PrivacyPolicyFragment;
import com.macinternetservices.sablebusinessdirectory.ui.setting.SettingActivity;
import com.macinternetservices.sablebusinessdirectory.ui.setting.SettingFragment;
import com.macinternetservices.sablebusinessdirectory.ui.setting.appinfo.AppInfoActivity;
import com.macinternetservices.sablebusinessdirectory.ui.specification.SpecificationListActivity;
import com.macinternetservices.sablebusinessdirectory.ui.specification.addspecification.AddAndEditSpecificationActivity;
import com.macinternetservices.sablebusinessdirectory.ui.stripe.StripeActivity;
import com.macinternetservices.sablebusinessdirectory.ui.subcategory.SubCategoryActivity;
import com.macinternetservices.sablebusinessdirectory.ui.user.PasswordChangeActivity;
import com.macinternetservices.sablebusinessdirectory.ui.user.ProfileEditActivity;
import com.macinternetservices.sablebusinessdirectory.ui.user.ProfileFragment;
import com.macinternetservices.sablebusinessdirectory.ui.user.UserForgotPasswordActivity;
import com.macinternetservices.sablebusinessdirectory.ui.user.UserForgotPasswordFragment;
import com.macinternetservices.sablebusinessdirectory.ui.user.UserLoginActivity;
import com.macinternetservices.sablebusinessdirectory.ui.user.UserLoginFragment;
import com.macinternetservices.sablebusinessdirectory.ui.user.UserRegisterActivity;
import com.macinternetservices.sablebusinessdirectory.ui.user.UserRegisterFragment;
import com.macinternetservices.sablebusinessdirectory.ui.user.phonelogin.PhoneLoginActivity;
import com.macinternetservices.sablebusinessdirectory.ui.user.phonelogin.PhoneLoginFragment;
import com.macinternetservices.sablebusinessdirectory.ui.user.verifyemail.VerifyEmailActivity;
import com.macinternetservices.sablebusinessdirectory.ui.user.verifyemail.VerifyEmailFragment;
import com.macinternetservices.sablebusinessdirectory.ui.user.verifyphone.VerifyMobileActivity;
import com.macinternetservices.sablebusinessdirectory.ui.user.verifyphone.VerifyMobileFragment;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewobject.Comment;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemHistory;
import com.macinternetservices.sablebusinessdirectory.viewobject.Noti;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.CityParameterHolder;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.ItemParameterHolder;

import java.io.Serializable;

import javax.inject.Inject;

//import com.panaceasoft.psmulticity.ui.city.selectedCity.SelectedCityFragment;

/**
 * Created by Panacea-Soft on 11/17/17.
 * Contact Email : teamps.is.cool@gmail.com
 */

public class NavigationController {

    //region Variables

    private final int containerId;
    private RegFragments currentFragment;

    //endregion


    //region Constructor
    @Inject
    public NavigationController() {

        // This setup is for MainActivity
        this.containerId = R.id.content_frame;
    }

    //endregion


    //region default navigation

    public void navigateToMainActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToUserLogin(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_USER_LOGIN)) {
            try {
                UserLoginFragment fragment = new UserLoginFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToVerifyEmail(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_USER_EMAIL_VERIFY)) {
            try {
                VerifyEmailFragment fragment = new VerifyEmailFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }


    public void navigateToCategoryFragment(SelectedCityActivity selectedCityActivity) {
        if (checkFragmentChange(RegFragments.HOME_CATEGORY)) {
            try {
                CategoryListFragment fragment = new CategoryListFragment();
                selectedCityActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToHomeFragment(SelectedCityActivity selectedCityActivity) {
        if (checkFragmentChange(RegFragments.HOME_HOME)) {
            try {
                SelectedCityFragment fragment = new SelectedCityFragment();
                selectedCityActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToFilteringFragment(FragmentActivity activity) {
        if (checkFragmentChange(RegFragments.HOME_FILTER)) {
            try {
                DashBoardSearchFragment fragment = new DashBoardSearchFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToUserProfile(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_USER_LOGIN)) {
            try {
                ProfileFragment fragment = new ProfileFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToFavourite(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_FAVOURITE)) {
            try {
                FavouriteListFragment fragment = new FavouriteListFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToCityMenu(SelectedCityActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_CITY_MENU)) {
            try {
                CityMenuFragment fragment = new CityMenuFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

//    public void navigateToTransaction(MainActivity mainActivity) {
//        if (checkFragmentChange(RegFragments.HOME_TRANSACTION)) {
//            try {
//                TransactionListFragment fragment = new TransactionListFragment();
//                mainActivity.getSupportFragmentManager().beginTransaction()
//                        .replace(containerId, fragment)
//                        .commitAllowingStateLoss();
//            } catch (Exception e) {
//                Utils.psErrorLog("Error! Can't replace fragment.", e);
//            }
//        }
//    }

    public void navigateToHistory(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_HISTORY)) {
            try {
                HistoryFragment fragment = new HistoryFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }


    public void navigateToUserRegister(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_USER_REGISTER)) {
            try {
                UserRegisterFragment fragment = new UserRegisterFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToUserForgotPassword(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_USER_FOGOT_PASSWORD)) {
            try {
                UserForgotPasswordFragment fragment = new UserForgotPasswordFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToSetting(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_SETTING)) {
            try {
                SettingFragment fragment = new SettingFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }


    public void navigateToLanguageSetting(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_LANGUAGE_SETTING)) {
            try {
                LanguageFragment fragment = new LanguageFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }


//    public void navigateToHome(SelectedCityActivity mainActivity) {
//        if (checkFragmentChange(RegFragments.HOME_HOME)) {
//            try {
//                SelectedCityFragment fragment = new SelectedCityFragment();
//                mainActivity.getSupportFragmentManager().beginTransaction()
//                        .replace(containerId, fragment)
//                        .commitAllowingStateLoss();
//            } catch (Exception e) {
//                Utils.psErrorLog("Error! Can't replace fragment.", e);
//            }
//        }
//    }

    public void navigateToHome(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_HOME)) {
            try {
                DashBoardCityListFragment fragment = new DashBoardCityListFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToCityList(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_CITY_LIST)) {
            try {
                DashBoardCityListFragment fragment = new DashBoardCityListFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }


    public void navigateToSearch(FragmentActivity mainActivity, ItemParameterHolder itemParameterHolder) {
        if (checkFragmentChange(RegFragments.HOME_SEARCH)) {
            try {
                SearchListFragment fragment = new SearchListFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();

                Bundle args = new Bundle();
                args.putSerializable(Constants.ITEM_PARAM_HOLDER_KEY, (Serializable) itemParameterHolder);
                fragment.setArguments(args);

            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToGalleryActivity(Activity activity, String imgType, String imgParentId) {
        Intent intent = new Intent(activity, GalleryActivity.class);

        if (!imgType.equals("")) {
            intent.putExtra(Constants.IMAGE_TYPE, imgType);
        }

        if (!imgParentId.equals("")) {
            intent.putExtra(Constants.IMAGE_PARENT_ID, imgParentId);
        }

        activity.startActivity(intent);

    }

    public void navigateToDetailGalleryActivity(Activity activity, String imgType, String newsId, String imgId) {
        Intent intent = new Intent(activity, GalleryDetailActivity.class);

        if (!imgType.equals("")) {
            intent.putExtra(Constants.IMAGE_TYPE, imgType);
        }

        if (!newsId.equals("")) {
            intent.putExtra(Constants.ITEM_ID, newsId);
        }

        if (!imgId.equals("")) {
            intent.putExtra(Constants.IMAGE_ID, imgId);
        }

        activity.startActivity(intent);

    }

    public void navigateToCommentListActivity(Activity activity, Item item) {
        Intent intent = new Intent(activity, CommentListActivity.class);
        intent.putExtra(Constants.ITEM_ID, item.id);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE__ITEM_FRAGMENT);
    }

    public void navigateToSettingActivity(Activity activity) {
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE__PROFILE_FRAGMENT);
    }

    public void navigateToNotificationSettingActivity(Activity activity) {
        Intent intent = new Intent(activity, NotificationSettingActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToEditProfileActivity(Activity activity) {
        Intent intent = new Intent(activity, ProfileEditActivity.class);
        activity.startActivity(intent);
    }

//    public void navigateToConditionsAndTermsActivity(Activity activity, String flag) {
//        Intent intent = new Intent(activity, TermsAndConditionsActivity.class);
//        intent.putExtra(Constants.SHOP_TERMS_FLAG, flag);
//        activity.startActivity(intent);
//    }

//    public void navigateToConditionsAndTermsActivity(Activity activity, String flag) {
//        Intent intent = new Intent(activity, ReadMoreActivity.class);
//        intent.putExtra(Constants.CITY_TERMS_FLAG, flag);
//        activity.startActivity(intent);
//    }


    public void navigateToAppInfoActivity(Activity activity) {
        Intent intent = new Intent(activity, AppInfoActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToCityActivity(Activity activity, String cityName) {
        Intent intent = new Intent(activity, CityActivity.class);
        intent.putExtra(Constants.CITY_NAME, cityName);
        activity.startActivity(intent);
    }

    public void navigateToProfileEditActivity(Activity activity) {
        Intent intent = new Intent(activity, ProfileEditActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToPrivacyPolicyActivity(Activity activity, String description) {
        Intent intent = new Intent(activity, PrivacyPolicyActivity.class);
        intent.putExtra(Constants.PRIVACY_POLICY_NAME, description);
        activity.startActivity(intent);
    }

    public void navigateToTermsAndConditionsActivity(Activity activity, String terms, String itemid) {
        Intent intent = new Intent(activity, com.macinternetservices.sablebusinessdirectory.ui.terms.TermsAndConditionsActivity.class);
        intent.putExtra(Constants.FLAG, terms);
        intent.putExtra(Constants.CITY_ITEM_ID, itemid);
        activity.startActivity(intent);
    }

    public void navigateToReadMoreActivity(Activity activity, String title, String description, Item item) {
        Intent intent = new Intent(activity, ReadMoreActivity.class);
        intent.putExtra(Constants.ITEM_TITLE, title);
        intent.putExtra(Constants.ITEM_DESCRIPTION, description);
        intent.putExtra(Constants.ITEM_IMAGE_URL, item.defaultPhoto.imgPath);
        activity.startActivity(intent);
    }

    public void navigateToUserLoginActivity(Activity activity) {
        Intent intent = new Intent(activity, UserLoginActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToVerifyEmailActivity(Activity activity) {
        Intent intent = new Intent(activity, VerifyEmailActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToUserRegisterActivity(Activity activity) {
        Intent intent = new Intent(activity, UserRegisterActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToUserForgotPasswordActivity(Activity activity) {
        Intent intent = new Intent(activity, UserForgotPasswordActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToPasswordChangeActivity(Activity activity) {
        Intent intent = new Intent(activity, PasswordChangeActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToNotificationList(Activity activity) {
        Intent intent = new Intent(activity, NotificationListActivity.class);
        activity.startActivity(intent);
    }


    public void navigateToRatingList(Activity activity, String itemId) {
        Intent intent = new Intent(activity, RatingListActivity.class);
        intent.putExtra(Constants.ITEM_ID, itemId);
        activity.startActivity(intent);
    }

    public void navigateToNotificationDetail(Activity activity, Noti noti, String token) {
        Intent intent = new Intent(activity, NotificationActivity.class);
        intent.putExtra(Constants.NOTI_ID, noti.id);
        intent.putExtra(Constants.NOTI_TOKEN, token);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE__NOTIFICATION_LIST_FRAGMENT);
    }

    public void navigateToCommentDetailActivity(Activity activity, Comment comment) {
        Intent intent = new Intent(activity, CommentDetailActivity.class);
        intent.putExtra(Constants.COMMENT_ID, comment.id);

        activity.startActivityForResult(intent, Constants.REQUEST_CODE__COMMENT_LIST_FRAGMENT);

    }

    public void navigateToDetailActivity(Activity activity, Item item) {
        Intent intent = new Intent(activity, ItemActivity.class);
        intent.putExtra(Constants.ITEM_ID, item.id);
        intent.putExtra(Constants.ITEM_NAME, item.name);
        intent.putExtra(Constants.CITY_ID, item.cityId);
        intent.putExtra(Constants.HISTORY_FLAG, Constants.ONE);

        activity.startActivity(intent);
    }

    public void navigateToItemDetailActivity(Activity activity, ItemHistory
            historyProduct, String selectedCityId) {
        Intent intent = new Intent(activity, ItemActivity.class);
        intent.putExtra(Constants.ITEM_ID, historyProduct.id);
        intent.putExtra(Constants.CITY_ID, selectedCityId);
        intent.putExtra(Constants.ITEM_NAME, historyProduct.historyName);
        intent.putExtra(Constants.HISTORY_FLAG, Constants.ZERO);

        activity.startActivity(intent);
    }

    public void navigateToUserHistoryActivity(Activity activity, String userId, String flagPaidOrNot) {
        Intent intent = new Intent(activity, UserHistoryListActivity.class);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra(Constants.FLAGPAIDORNOT, flagPaidOrNot);
        activity.startActivity(intent);
    }


    public void navigateToFavouriteActivity(Activity activity) {
        Intent intent = new Intent(activity, FavouriteListActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToCategoryActivity(Activity activity) {
        Intent intent = new Intent(activity, CategoryListActivity.class);
        activity.startActivity(intent);
    }

    public void navigateToSubCategoryActivity(Activity activity, String catId, String catName) {
        Intent intent = new Intent(activity, SubCategoryActivity.class);
        intent.putExtra(Constants.CATEGORY_ID, catId);
        intent.putExtra(Constants.CATEGORY_NAME, catName);
        activity.startActivity(intent);
    }

    public void navigateToMapActivity(Activity activity, String LNG, String LAT, String itemName) {
        Intent intent = new Intent(activity, MapActivity.class);
        intent.putExtra(Constants.LNG, LNG);
        intent.putExtra(Constants.LAT, LAT);
        intent.putExtra(Constants.ITEM_NAME, itemName);
        activity.startActivity(intent);
    }

    public void navigateToTypeFilterFragment(FragmentActivity mainActivity, String
            catId, String subCatId, ItemParameterHolder itemParameterHolder, String name) {

        if (name.equals(Constants.FILTERING_TYPE_FILTER)) {
            Intent intent = new Intent(mainActivity, FilteringActivity.class);
            intent.putExtra(Constants.CATEGORY_ID, catId);
            if (subCatId == null || subCatId.equals("")) {
                subCatId = Constants.ZERO;
            }
            intent.putExtra(Constants.SUBCATEGORY_ID, subCatId);
            intent.putExtra(Constants.FILTERING_FILTER_NAME, name);

            mainActivity.startActivityForResult(intent, Constants.REQUEST_CODE__ITEM_LIST_FRAGMENT);
        } else if (name.equals(Constants.FILTERING_SPECIAL_FILTER)) {
            Intent intent = new Intent(mainActivity, FilteringActivity.class);
            intent.putExtra(Constants.FILTERING_HOLDER, itemParameterHolder);


            intent.putExtra(Constants.FILTERING_FILTER_NAME, name);

            mainActivity.startActivityForResult(intent, Constants.REQUEST_CODE__ITEM_LIST_FRAGMENT);
        }

    }

    public void navigateBackToCommentListFragment(FragmentActivity
                                                          commentDetailActivity, String comment_headerId) {
        Intent intent = new Intent();
        intent.putExtra(Constants.COMMENT_HEADER_ID, comment_headerId);

        commentDetailActivity.setResult(Constants.RESULT_CODE__REFRESH_COMMENT_LIST, intent);
    }

    public void navigateBackToProductDetailFragment(FragmentActivity
                                                            productDetailActivity, String product_id) {
        Intent intent = new Intent();
        intent.putExtra(Constants.ITEM_ID, product_id);

        productDetailActivity.setResult(Constants.RESULT_CODE__REFRESH_COMMENT_LIST, intent);
    }

    public void navigateBackToHomeFeaturedFragment(FragmentActivity mainActivity, String
            catId, String subCatId) {
        Intent intent = new Intent();

        intent.putExtra(Constants.CATEGORY_ID, catId);
        intent.putExtra(Constants.SUBCATEGORY_ID, subCatId);

        mainActivity.setResult(Constants.RESULT_CODE__CATEGORY_FILTER, intent);
    }

    public void navigateBackToHomeFeaturedFragmentFromFiltering(FragmentActivity mainActivity, ItemParameterHolder itemParameterHolder) {
        Intent intent = new Intent();
        intent.putExtra(Constants.FILTERING_HOLDER, itemParameterHolder);

        mainActivity.setResult(Constants.RESULT_CODE__SPECIAL_FILTER, intent);
    }

    public void navigateToCollectionItemList(FragmentActivity fragmentActivity, String
            id, String Name, String image_path) {
        Intent intent = new Intent(fragmentActivity, ItemCollectionActivity.class);
        intent.putExtra(Constants.COLLECTION_ID, id);
        intent.putExtra(Constants.COLLECTION_NAME, Name);
        intent.putExtra(Constants.COLLECTION_IMAGE, image_path);

        fragmentActivity.startActivity(intent);
    }

    public void navigateToHomeFilteringActivity(FragmentActivity mainActivity, ItemParameterHolder itemParameterHolder, String title, String cityLat, String cityLng, String cityName) {


        if (cityLat != null) {
            itemParameterHolder.cityLat = cityLat;
            itemParameterHolder.cityLng = cityLng;
            itemParameterHolder.cityName = cityName;
        }

        Intent intent = new Intent(mainActivity, SearchListActivity.class);

        intent.putExtra(Constants.ITEM_NAME, title);
        intent.putExtra(Constants.ITEM_PARAM_HOLDER_KEY, itemParameterHolder);

        mainActivity.startActivity(intent);
    }

    public void navigateToItemListActivity(Activity activity, String userId, String title, String status) {
        Intent intent = new Intent(activity, LoginUserItemListActivity.class);
        intent.putExtra(Constants.USER_ID, userId);
        intent.putExtra(Constants.ITEM_LIST_TITLE, title);
        intent.putExtra(Constants.ITEM_STATUS, status);
        activity.startActivity(intent);
    }

    public void navigateToSearchActivityCategoryFragment(FragmentActivity
                                                                 fragmentActivity, String fragName, String catId, String subCatId) {
        Intent intent = new Intent(fragmentActivity, DashboardSearchByCategoryActivity.class);
        intent.putExtra(Constants.CATEGORY_FLAG, fragName);

        if (!catId.equals(Constants.NO_DATA)) {
            intent.putExtra(Constants.CATEGORY_ID, catId);
        }

        if (!subCatId.equals(Constants.NO_DATA)) {
            intent.putExtra(Constants.SUBCATEGORY_ID, subCatId);
        }

        fragmentActivity.startActivityForResult(intent, Constants.REQUEST_CODE__SEARCH_FRAGMENT);
    }

    public void navigateBackToSearchFragment(FragmentActivity fragmentActivity, String
            catId, String cat_Name) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CATEGORY_NAME, cat_Name);
        intent.putExtra(Constants.CATEGORY_ID, catId);

        fragmentActivity.setResult(Constants.RESULT_CODE__SEARCH_WITH_CATEGORY, intent);
    }

    public void navigateBackToSearchFragmentFromSubCategory(FragmentActivity
                                                                    fragmentActivity, String sub_id, String sub_Name) {
        Intent intent = new Intent();
        intent.putExtra(Constants.SUBCATEGORY_NAME, sub_Name);
        intent.putExtra(Constants.SUBCATEGORY_ID, sub_id);

        fragmentActivity.setResult(Constants.RESULT_CODE__SEARCH_WITH_SUBCATEGORY, intent);
    }

    public void navigateBackToProfileFragment(FragmentActivity fragmentActivity) {
        Intent intent = new Intent();

        fragmentActivity.setResult(Constants.RESULT_CODE__LOGOUT_ACTIVATED, intent);
    }

    public void navigateToSelectedItemDetail(FragmentActivity fragmentActivity, String
            itemId, String itemName, String cityId) {

        Intent intent = new Intent(fragmentActivity, ItemActivity.class);

        intent.putExtra(Constants.HISTORY_FLAG, Constants.ONE);
        intent.putExtra(Constants.ITEM_NAME, itemName);
        intent.putExtra(Constants.ITEM_ID, itemId);
        intent.putExtra(Constants.CITY_ID, cityId);

        intent.putExtra(Constants.ITEM_ID, itemId);
        intent.putExtra(Constants.ITEM_NAME, itemName);

        fragmentActivity.startActivity(intent);
    }

    public void navigateToCityList(FragmentActivity fragmentActivity, CityParameterHolder cityParameterHolder, String title) {
        Intent intent = new Intent(fragmentActivity, CityListActivity.class);

        intent.putExtra(Constants.CITY_HOLDER, cityParameterHolder);
        intent.putExtra(Constants.CITY_TITLE, title);

        fragmentActivity.startActivity(intent);
    }


    public void navigateToCityListNew(FragmentActivity fragmentActivity, CityParameterHolder cityParameterHolder, String title) {
        Intent intent = new Intent(fragmentActivity, CityListActivity.class);

        intent.putExtra(Constants.CITY_HOLDER, cityParameterHolder);
        intent.putExtra(Constants.CITY_TITLE, title);
        intent.putExtra("Coming_From_Main", "1");

        fragmentActivity.startActivityForResult(intent,1212);
    }

    public void navigateToAllCityListHomeFragment(MainActivity mainActivity, CityParameterHolder cityParameterHolder) {
        if (checkFragmentChange(RegFragments.HOME_CITIES)) {
            try {
                CityListFragment fragment = new CityListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CITY_HOLDER, cityParameterHolder);
                fragment.setArguments(bundle);
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToPopularCityListHomeFragment(MainActivity mainActivity, CityParameterHolder cityParameterHolder) {
        if (checkFragmentChange(RegFragments.HOME_POPULAR_CITIES)) {
            try {
                CityListFragment fragment = new CityListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CITY_HOLDER, cityParameterHolder);
                fragment.setArguments(bundle);
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToRecommendedCityListHomeFragment(MainActivity mainActivity, CityParameterHolder cityParameterHolder) {
        if (checkFragmentChange(RegFragments.HOME_RECOMMENDED_CITIES)) {
            try {
                CityListFragment fragment = new CityListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CITY_HOLDER, cityParameterHolder);
                fragment.setArguments(bundle);
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToSelectedCityDetail(FragmentActivity fragmentActivity, String
            cityId, String cityName, String isContainValue) {
        if (!TextUtils.isEmpty(isContainValue)) {
            Intent intent = new Intent();
            intent.putExtra(Constants.CITY_ID, cityId);
            intent.putExtra(Constants.CITY_NAME, cityName);
            // fragmentActivity.setResult(Constants.SELECT_CITY, intent);
            LocalBroadcastManager.getInstance(fragmentActivity.getApplicationContext()).sendBroadcast(new Intent("New_Data").putExtra(Constants.CITY_ID, cityId).putExtra(Constants.CITY_NAME, cityName));

            fragmentActivity.finish();

        } else {
            Intent intent = new Intent(fragmentActivity, SelectedCityActivity.class);
            intent.putExtra(Constants.CITY_ID, cityId);
            intent.putExtra(Constants.CITY_NAME, cityName);
            fragmentActivity.startActivity(intent);
        }
    }

    public void navigateToBlogListBySelectedCity(FragmentActivity fragmentActivity, String
            selectedCityId) {

        Intent intent = new Intent(fragmentActivity, BlogListByCityIdActivity.class);
        intent.putExtra(Constants.CITY_ID, selectedCityId);
        fragmentActivity.startActivity(intent);
    }

    public void navigateToBlogList(FragmentActivity fragmentActivity) {

        Intent intent = new Intent(fragmentActivity, BlogListActivity.class);
        fragmentActivity.startActivity(intent);
    }

    public void navigateToBlogDetailActivity(FragmentActivity fragmentActivity, String blogId, String cityId) {

        Intent intent = new Intent(fragmentActivity, BlogDetailActivity.class);

        intent.putExtra(Constants.BLOG_ID, blogId);
        intent.putExtra(Constants.CITY_ID, cityId);

        fragmentActivity.startActivity(intent);
    }

    public void navigateToCollectionHeaderListActivity(FragmentActivity fragmentActivity) {

        Intent intent = new Intent(fragmentActivity, CollectionHeaderListActivity.class);

        fragmentActivity.startActivity(intent);
    }

    public void navigateToForceUpdateActivity(FragmentActivity fragmentActivity, String title, String msg) {

        Intent intent = new Intent(fragmentActivity, ForceUpdateActivity.class);

        intent.putExtra(Constants.APPINFO_FORCE_UPDATE_MSG, msg);

        intent.putExtra(Constants.APPINFO_FORCE_UPDATE_TITLE, title);

        fragmentActivity.startActivity(intent);
    }

    public void navigateToPlayStore(FragmentActivity fragmentActivity) {
//        try {
//            fragmentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.PLAYSTORE_MARKET_URL)));
//        } catch (android.content.ActivityNotFoundException anfe) {
//            fragmentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.PLAYSTORE_HTTP_URL)));
//        }
//    }
        Uri uri = Uri.parse(Config.PLAYSTORE_MARKET_URL_FIX + fragmentActivity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            fragmentActivity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            fragmentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Config.PLAYSTORE_HTTP_URL_FIX + fragmentActivity.getPackageName())));
        }
    }

    public void navigateToMapFiltering(FragmentActivity activity, ItemParameterHolder itemParameterHolder) {
        Intent intent = new Intent(activity, MapFilteringActivity.class);

        intent.putExtra(Constants.ITEM_HOLDER, itemParameterHolder);

        activity.startActivityForResult(intent, Constants.REQUEST_CODE__MAP_FILTERING);
    }

    public void navigateBackToSearchFromMapFiltering(FragmentActivity activity, ItemParameterHolder itemParameterHolder) {
        Intent intent = new Intent();

        intent.putExtra(Constants.ITEM_HOLDER, itemParameterHolder);

        activity.setResult(Constants.RESULT_CODE__MAP_FILTERING, intent);

        activity.finish();
    }

    public void navigateBackFromSearchFilterToAllListing(FragmentActivity activity, ItemParameterHolder itemParameterHolder) {
        Intent intent = new Intent();

        intent.putExtra(Constants.ITEM_HOLDER, itemParameterHolder);

        activity.setResult(Constants.RESULT_CODE__MAP_BACK_ALL_LISTING_FILTERING, intent);

        activity.finish();
    }
//    public void navigateToFilterFragemtnFromDashBoard(FragmentActivity activity, ItemParameterHolder itemParameterHolder)
//    {
//        Intent intent = new Intent(activity, SearchListActivity.class);
//
//        intent.putExtra(Constants.ITEM_PARAM_HOLDER_KEY, itemParameterHolder);
//
//        activity.startActivity(intent);
//    }

    //region Private methods
    private Boolean checkFragmentChange(RegFragments regFragments) {
        if (currentFragment != regFragments) {
            currentFragment = regFragments;
            return true;
        }

        return false;
    }

    public void navigateToItemUpdated(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_ITEM_UPDATED)) {
            try {
                ItemUploadedListFragment fragment = new ItemUploadedListFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToPrivacyPolicy(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_PRIVACY_POLICY
        )) {
            try {
                PrivacyPolicyFragment fragment = new PrivacyPolicyFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.PRIVACY_POLICY_NAME, Constants.EMPTY_STRING);
                fragment.setArguments(bundle);

            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToExpandActivity(Activity activity, int flag, String selectId, String Id,String Edit_Upload,Item item) {
        Intent intent = new Intent(activity, SelectionActivity.class);
        intent.putExtra(Constants.FLAG, flag);
        if (flag == Constants.SELECT_CITY) {
            intent.putExtra(Constants.CITY_ID, selectId);
            intent.putExtra("Coming_From_Main", "1");
            if (!Edit_Upload.equals("")) {
                intent.putExtra("Edit_Upload", item);
            }
        }
        if (flag == Constants.SELECT_CATEGORY) {
            intent.putExtra(Constants.CATEGORY_ID, selectId);
        }
        if (flag == Constants.SELECT_SUBCATEGORY) {
            intent.putExtra(Constants.SUBCATEGORY_ID, selectId);
            intent.putExtra(Constants.CATEGORY_ID, Id);
        }
        if (flag == Constants.SELECT_STATUS) {
            intent.putExtra(Constants.STATUS_ID, selectId);
        }
        activity.startActivityForResult(intent, 1);
    }

    public void navigateBackToEntryItemFragment(FragmentActivity expandActivity, String name, String id, int flag) {
        Intent intent = new Intent();

        if (flag == Constants.SELECT_CATEGORY) {
            intent.putExtra(Constants.CATEGORY_ID, id);
            intent.putExtra(Constants.CATEGORY_NAME, name);
            expandActivity.setResult(Constants.SELECT_CATEGORY, intent);
        } else if (flag == Constants.SELECT_SUBCATEGORY) {
            intent.putExtra(Constants.SUBCATEGORY_ID, id);
            intent.putExtra(Constants.SUBCATEGORY_NAME, name);
            expandActivity.setResult(Constants.SELECT_SUBCATEGORY, intent);
        } else if (flag == Constants.SELECT_STATUS) {
            intent.putExtra(Constants.STATUS_ID, id);
            intent.putExtra(Constants.STATUS_NAME, name);
            expandActivity.setResult(Constants.SELECT_STATUS, intent);
        } else if (flag == Constants.SELECT_CITY) {
            intent.putExtra(Constants.CITY_ID, id);
            intent.putExtra(Constants.CITY_NAME, name);
            expandActivity.setResult(Constants.SELECT_CITY, intent);
        }

        expandActivity.finish();
    }

    public void navigateToSpecificationListActivity(Activity activity, String itemId, String itemName) {
        Intent intent = new Intent(activity, SpecificationListActivity.class);
        intent.putExtra(Constants.ITEM_ID, itemId);
        intent.putExtra(Constants.ITEM_NAME, itemName);
        activity.startActivity(intent);
    }

    public void navigateToAddingSpecification(Activity activity, String itemId, String specificationId, String specificationName, String specificationDescription) {
        Intent intent = new Intent(activity, AddAndEditSpecificationActivity.class);

        intent.putExtra(Constants.ITEM_ID, itemId);
        intent.putExtra(Constants.SPECIFICATION_ID, specificationId);
        intent.putExtra(Constants.SPECIFICATION_NAME, specificationName);
        intent.putExtra(Constants.SPECIFICATION_DESCRIPTION, specificationDescription);
        activity.startActivity(intent);
    }

    public void navigateToImageList(Activity activity, String item_id) {
        Intent intent = new Intent(activity, ItemImageListActivity.class);

        intent.putExtra(Constants.ITEM_ID, item_id);

        activity.startActivity(intent);
    }

    public void navigateToImageUploadActivity(Activity activity, String img_id, String img_path, String img_desc, int flag, String selectId) {
        Intent intent = new Intent(activity, ImageUploadActivity.class);

        intent.putExtra(Constants.IMG_ID, img_id);
        intent.putExtra(Constants.IMGPATH, img_path);
        intent.putExtra(Constants.IMGDESC, img_desc);
        intent.putExtra(Constants.FLAG, flag);
        intent.putExtra(Constants.SELECTEDID, selectId);

        activity.startActivityForResult(intent, Constants.RESULT_GO_TO_IMAGE_UPLOAD);
    }

    public void navigateToGalleryImage(Activity activity) {
        if (Utils.isStoragePermissionGranted(activity)) {
            Intent pickPhoto = new Intent();
            pickPhoto.setType("image/*");
            pickPhoto.setAction(Intent.ACTION_OPEN_DOCUMENT);
            pickPhoto.addCategory(Intent.CATEGORY_OPENABLE);

            activity.startActivityForResult(pickPhoto, Constants.RESULT_LOAD_IMAGE_CATEGORY);
        }

    }

    public void navigateBackToUpdateCategoryActivity(Activity activity, String status, String img_id) {
        Intent intent = new Intent();
        intent.putExtra(Constants.IMAGE_UPLOAD_STATUS, status);
        intent.putExtra(Constants.IMG_ID, img_id);

        activity.setResult(Constants.RESULT_CODE_FROM_IMAGE_UPLOAD, intent);
    }

    public void navigateToLists(Activity activity, String flag, String lat, String lng) {

        if (flag.equals(Constants.MAP)) {
            Intent intent = new Intent(activity, MapItemEntryActivity.class);
            intent.putExtra(Constants.FLAG, flag);
            intent.putExtra(Constants.LNG, lng);
            intent.putExtra(Constants.LAT, lat);

            activity.startActivityForResult(intent, Constants.REQUEST_CODE_TO_MAP_VIEW);

        }

    }

    public void navigateBackFromMapView(Activity activity, String lat, String lng) {
        Intent intent = new Intent();
        intent.putExtra(Constants.LAT, lat);
        intent.putExtra(Constants.LNG, lng);

        activity.setResult(Constants.RESULT_CODE_FROM_MAP_VIEW, intent);
    }

    public void navigateToItemUploadActivity(Activity activity, Item itemList,String city_id,String city_name,String Edit_Upload) {
        Intent intent = new Intent(activity, ItemUploadActivity.class);
        if (itemList != null) {
            intent.putExtra(Constants.ITEM_ID, itemList.id);
            intent.putExtra(Constants.ITEM_NAME, itemList.name);
            intent.putExtra(Constants.CITY_ID,city_id);
            intent.putExtra(Constants.CITY_NAME,city_name);
            if (!Edit_Upload.equals("")) {
                intent.putExtra("Edit_Upload",itemList);
            }

        }else{
            if (!TextUtils.isEmpty(city_id) && !TextUtils.isEmpty(city_name))
                intent.putExtra(Constants.CITY_ID,city_id);
            intent.putExtra(Constants.CITY_NAME,city_name);

        }
        activity.startActivity(intent);
    }

    public void navigateToPhoneVerifyFragment(MainActivity mainActivity, String number, String userName) {
        if (checkFragmentChange(RegFragments.HOME_PHONE_VERIFY)) {
            try {
                VerifyMobileFragment fragment = new VerifyMobileFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();

                Bundle args = new Bundle();
                args.putString(Constants.USER_PHONE, number);
                args.putString(Constants.USER_NAME, userName);
                fragment.setArguments(args);
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToPhoneVerifyActivity(Activity activity, String number, String userName) {
        Intent intent = new Intent(activity, VerifyMobileActivity.class);
        intent.putExtra(Constants.USER_PHONE, number);
        intent.putExtra(Constants.USER_NAME, userName);
        activity.startActivity(intent);
    }


    public void navigateToPhoneLoginFragment(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_PHONE_LOGIN)) {
            try {
                PhoneLoginFragment fragment = new PhoneLoginFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToPhoneLoginActivity(Activity activity) {
        Intent intent = new Intent(activity, PhoneLoginActivity.class);
        activity.startActivity(intent);
    }

    public void navigateBackToCheckoutFragment(Activity activity, String stripeToken) {
        Intent intent = new Intent();

        intent.putExtra(Constants.PAYMENT_TOKEN, stripeToken);

        activity.setResult(Constants.RESULT_CODE__STRIPE_ACTIVITY, intent);
    }

    public void navigateToStripeActivity(Activity fragmentActivity, String stripePublishableKey) {
        Intent intent = new Intent(fragmentActivity, StripeActivity.class);
        intent.putExtra(Constants.STRIPEPUBLISHABLEKEY, stripePublishableKey);
        fragmentActivity.startActivityForResult(intent, Constants.REQUEST_CODE__STRIPE_ACTIVITY);
    }

    public void navigateToTransactions(MainActivity mainActivity) {
        if (checkFragmentChange(RegFragments.HOME_TRANSACTION)) {
            try {
                LoginUserPaidItemFragment fragment = new LoginUserPaidItemFragment();
                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(containerId, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                Utils.psErrorLog("Error! Can't replace fragment.", e);
            }
        }
    }

    public void navigateToItemPromoteActivity(Activity activity, String itemId) {
        Intent intent = new Intent(activity, ItemPromoteActivity.class);
        intent.putExtra(Constants.ITEM_ID, itemId);

        activity.startActivity(intent);
    }

    /**
     * Remark : This enum is only for MainActivity,
     * For the other fragments, no need to register here
     **/
    private enum RegFragments {
        HOME_FRAGMENT,
        HOME_USER_LOGIN,
        HOME_USER_EMAIL_VERIFY,
        HOME_FB_USER_REGISTER,
        HOME_BASKET,
        HOME_USER_REGISTER,
        HOME_USER_FOGOT_PASSWORD,
        HOME_ABOUTUS,
        HOME_CONTACTUS,
        HOME_NOTI_SETTING,
        HOME_APP_INFO,
        HOME_LANGUAGE_SETTING,
        HOME_LATEST_PRODUCTS,
        HOME_DISCOUNT,
        HOME_FEATURED_PRODUCTS,
        HOME_CATEGORY,
        HOME_SUBCATEGORY,
        HOME_HOME,
        HOME_TRENDINGPRODUCTS,
        HOME_COMMENTLISTS,
        HOME_SEARCH,
        HOME_NOTIFICATION,
        HOME_PRODUCT_COLLECTION,
        HOME_TRANSACTION,
        HOME_HISTORY,
        HOME_SETTING,
        HOME_FAVOURITE,
        HOME_CITY_LIST,
        HOME_CITY_MENU,
        HOME_FILTER,
        HOME_CITIES,
        HOME_POPULAR_CITIES,
        HOME_RECOMMENDED_CITIES,
        HOME_ITEM_UPDATED,
        HOME_PRIVACY_POLICY,
        HOME_PHONE_VERIFY,
        HOME_PHONE_LOGIN

    }
}
