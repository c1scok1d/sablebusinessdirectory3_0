package com.macinternetservices.sablebusinessdirectory.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentDashboardCityListBinding;
import com.macinternetservices.sablebusinessdirectory.ui.city.adapter.FeaturedCitiesAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.city.adapter.PopularCitiesAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.city.adapter.RecentCitiesAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.ui.dashboard.adapter.DashBoardViewPagerAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.item.adapter.ItemListAdapter;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.GPSTracker;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.blog.BlogViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.city.CityViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.city.FeaturedCitiesViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.city.PopularCitiesViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.city.RecentCitiesViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.clearalldata.ClearAllDataViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.DiscountItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.FeaturedItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.PopularItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.RecentItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.City;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.ItemParameterHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class DashBoardCityListFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private FeaturedItemViewModel featuredItemViewModel;
    private DiscountItemViewModel discountItemViewModel;
    private PopularItemViewModel popularItemViewModel;
    private RecentItemViewModel recentItemViewModel;
    private PopularCitiesViewModel popularCitiesViewModel;
    private FeaturedCitiesViewModel featuredCitiesViewModel;
    private RecentCitiesViewModel recentCitiesViewModel;
    private BlogViewModel blogViewModel;
    private ClearAllDataViewModel clearAllDataViewModel;
    private CityViewModel cityViewModel;
    private static final int FRAME_TIME_MS = 8000;
    private PSDialogMsg psDialogMsg;

    private ImageView[] dots;
    private Handler handler = new Handler();
    private Runnable update;
    private int NUM_PAGES = 10;
    private int currentPage = 0;
    private boolean touched = false;
    private Timer unTouchedTimer;

    @Inject
    protected SharedPreferences pref;
    private String startDate = Constants.ZERO;
    private String endDate = Constants.ZERO;

    @VisibleForTesting
    private AutoClearedValue<FragmentDashboardCityListBinding> binding;
    private AutoClearedValue<ItemListAdapter> featuredItemListAdapter;
    private AutoClearedValue<ItemListAdapter> popularItemListAdapter;
    private AutoClearedValue<ItemListAdapter> discountItemListAdapter;
    private AutoClearedValue<ItemListAdapter> recentItemListAdapter;
    private AutoClearedValue<PopularCitiesAdapter> popularCitiesAdapter;
    private AutoClearedValue<FeaturedCitiesAdapter> featuredCitiesAdapter;
    private AutoClearedValue<RecentCitiesAdapter> recentCitiesAdapter;
    private AutoClearedValue<DashBoardViewPagerAdapter> dashBoardViewPagerAdapter;
    private AutoClearedValue<LinearLayout> pageIndicatorLayout;
    private AutoClearedValue<ViewPager> viewPager;
    private Handler imageSwitchHandler;
    public String selectedCityId;
    GPSTracker gpsTracker;

    SharedPreferences preferences;
    //selectedCityId = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentDashboardCityListBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard_city_list, container, false, dataBindingComponent);

        //selectedCityId = "";
        binding = new AutoClearedValue<>(this, dataBinding);
        gpsTracker = new GPSTracker(getContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        gpsTracker.getLocation();
        //Constants.LAT = String.valueOf(gpsTracker.getLatitude());
        pref.edit().putString(Constants.LAT, String.valueOf(gpsTracker.getLatitude())).apply();
        pref.edit().putString(Constants.LNG, String.valueOf(gpsTracker.getLongitude())).apply();
        loadDates();
        if ((this.getActivity()) != null) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.md_white_1000));
        }
        return binding.get().getRoot();
    }

    private void loadDates() {
        try {

            if (getActivity() != null && getActivity().getBaseContext() != null) {
                startDate = pref.getString(Constants.CITY_START_DATE, Constants.ZERO);
                endDate = pref.getString(Constants.CITY_END_DATE, Constants.ZERO);
            }

        } catch (NullPointerException ne) {
            Utils.psErrorLog("Null Pointer Exception.", ne);
        } catch (Exception e) {
            Utils.psErrorLog("Error in getting notification flag data.", e);
        }
    }

    @Override
    protected void initUIAndActions() {

        if (!Config.ENABLE_ITEM_UPLOAD) {
            binding.get().floatingActionButton.setVisibility(View.GONE);
        } else {
            binding.get().floatingActionButton.setVisibility(View.VISIBLE);
        }

        binding.get().headerText.setVisibility(View.GONE);
        binding.get().wavingImageView.setVisibility(View.GONE);
        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().floatingActionButton.setOnClickListener(view -> {
            navigationController.navigateToItemUploadActivity(getActivity(), null, "", "", null,
                    null, null, null);
            //   navigationController.navigateToItemUpdated((MainActivity)getActivity());
       /*     pref.edit().putString(Constants.CITY_ID,Constants.EMPTY_STRING).apply();
            Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, () ->
                        navigationController.navigateToItemUploadActivity(getActivity(), null));*/
        });

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.layout__primary_background));
            ((MainActivity) getActivity()).updateToolbarIconColor(Color.GRAY);
            ((MainActivity) getActivity()).updateMenuIconGrey();
        }

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);

            AdRequest adRequest2 = new AdRequest.Builder()
                    .build();
            binding.get().adView2.loadAd(adRequest2);

        } else {
            binding.get().adView.setVisibility(View.GONE);
            binding.get().adView2.setVisibility(View.GONE);
        }

        viewPager = new AutoClearedValue<>(this, binding.get().blogViewPager);

        pageIndicatorLayout = new AutoClearedValue<>(this, binding.get().pagerIndicator);

        if (viewPager.get() != null && viewPager.get() != null && viewPager.get() != null) {
            viewPager.get().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    currentPage = position;

                    if (pageIndicatorLayout != null) {
                        // setupSliderPagination(binding.getRoot());
                        setupSliderPagination();
                    }

                    for (ImageView dot : dots) {
                        if (dots != null) {
                            dot.setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
                        }
                    }

                    if (dots != null && dots.length > position) {
                        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
                    }

                    touched = true;

                    handler.removeCallbacks(update);

                    setUnTouchedTimer();

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
            binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
            binding.get().swipeRefresh.setOnRefreshListener(() -> {

                cityViewModel.loadingDirection = Utils.LoadingDirection.top;

                // reset itemCategoryViewModel.offset
                cityViewModel.offset = 0;

                // reset itemCategoryViewModel.forceEndLoading
                cityViewModel.forceEndLoading = false;

                // update live data
                featuredCitiesViewModel.setFeaturedCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredCitiesViewModel.featuredCitiesParameterHolder);
                popularCitiesViewModel.setPopularCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularCitiesViewModel.popularCitiesParameterHolder);
                recentCitiesViewModel.setRecentCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, recentCitiesViewModel.recentCitiesParameterHolder);
                blogViewModel.setBlogByIdObj(String.valueOf(Config.LIST_NEW_FEED_COUNT_PAGER), String.valueOf(blogViewModel.offset));
                discountItemViewModel.setDiscountItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, discountItemViewModel.discountItemParameterHolder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));
                featuredItemViewModel.setFeaturedItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));

                popularItemViewModel.setPopularItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularItemViewModel.popularItemParameterHolder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));
            });
        }

        startPagerAutoSwipe();

        binding.get().searchBoxEditText.setOnClickListener(v -> binding.get().searchBoxEditText.setFocusable(true));

        binding.get().searchImageButton.setOnClickListener(v -> {

            ItemParameterHolder itemParameterHolder = new ItemParameterHolder();
            itemParameterHolder.keyword = binding.get().searchBoxEditText.getText().toString();

            navigationController.navigateToHomeFilteringActivity(getActivity(), itemParameterHolder, itemParameterHolder.keyword, "", "", "");
        });

        binding.get().nestedScrollView.setOnTouchListener((v, event) -> {

            if (binding.get().searchBoxEditText.hasFocus()) {
                binding.get().searchBoxEditText.clearFocus();
            }

            return false;
        });

        binding.get().bestThingsViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(DashBoardCityListFragment.this.getActivity(), featuredItemViewModel.featuredItemParameterHolder, DashBoardCityListFragment.this.getString(R.string.dashboard_best_things), "", "", ""));

        binding.get().popularPlacesViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(DashBoardCityListFragment.this.getActivity(), popularItemViewModel.popularItemParameterHolder, DashBoardCityListFragment.this.getString(R.string.dashboard_popular_places), "", "", ""));

        binding.get().newPlacesViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(DashBoardCityListFragment.this.getActivity(), recentItemViewModel.recentItemParameterHolder, DashBoardCityListFragment.this.getString(R.string.dashboard_new_places), "", "", ""));

        binding.get().blogViewAllTextView.setOnClickListener(v -> navigationController.navigateToBlogList(getActivity()));

        binding.get().promoListViewAllTextView.setOnClickListener(v -> navigationController.navigateToHomeFilteringActivity(DashBoardCityListFragment.this.getActivity(), discountItemViewModel.discountItemParameterHolder, DashBoardCityListFragment.this.getString(R.string.dashboard_promo_list), "", "", ""));

        binding.get().popularCitiesViewAllTextView.setOnClickListener(v -> navigationController.navigateToCityList(getActivity(), popularCitiesViewModel.popularCitiesParameterHolder, getString(R.string.dashboard_popular_cities)));

        binding.get().featuredViewAllTextView.setOnClickListener(v -> navigationController.navigateToCityList(getActivity(), featuredCitiesViewModel.featuredCitiesParameterHolder, getString(R.string.dashboard_best_cities)));

        binding.get().newCitiesViewAllTextView.setOnClickListener(v -> navigationController.navigateToCityList(getActivity(), recentCitiesViewModel.recentCitiesParameterHolder, getString(R.string.dashboard_new_cities)));

        if (force_update) {
            navigationController.navigateToForceUpdateActivity(this.getActivity(), force_update_title, force_update_msg);
        }
    }

    private void setupSliderPagination() {

        int dotsCount = dashBoardViewPagerAdapter.get().getCount();


        if (dotsCount > 0) {

            dots = new ImageView[dotsCount];

            if (pageIndicatorLayout != null) {
                if (pageIndicatorLayout.get().getChildCount() > 0) {
                    pageIndicatorLayout.get().removeAllViewsInLayout();
                }
            }

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(getContext());
                dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(4, 0, 4, 0);

                pageIndicatorLayout.get().addView(dots[i], params);
            }

            int currentItem = viewPager.get().getCurrentItem();
            if (currentItem > 0 && currentItem < dots.length) {
                dots[currentItem].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            } else {
                dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
            }
        }
    }

    @Override
    protected void initViewModels() {
        blogViewModel = new ViewModelProvider(this, viewModelFactory).get(BlogViewModel.class);
        clearAllDataViewModel = new ViewModelProvider(this, viewModelFactory).get(ClearAllDataViewModel.class);
        featuredItemViewModel = new ViewModelProvider(this, viewModelFactory).get(FeaturedItemViewModel.class);
        discountItemViewModel = new ViewModelProvider(this, viewModelFactory).get(DiscountItemViewModel.class);
        popularItemViewModel = new ViewModelProvider(this, viewModelFactory).get(PopularItemViewModel.class);
        recentItemViewModel = new ViewModelProvider(this, viewModelFactory).get(RecentItemViewModel.class);
        popularCitiesViewModel = new ViewModelProvider(this, viewModelFactory).get(PopularCitiesViewModel.class);
        featuredCitiesViewModel = new ViewModelProvider(this, viewModelFactory).get(FeaturedCitiesViewModel.class);
        recentCitiesViewModel = new ViewModelProvider(this, viewModelFactory).get(RecentCitiesViewModel.class);
        cityViewModel = new ViewModelProvider(this, viewModelFactory).get(CityViewModel.class);
    }

    @Override
    protected void initAdapters() {


        DashBoardViewPagerAdapter nvAdapter3 = new DashBoardViewPagerAdapter(dataBindingComponent, blog ->
                navigationController.navigateToBlogDetailActivity(DashBoardCityListFragment.this.getActivity(), blog.id, blog.cityId));
        this.dashBoardViewPagerAdapter = new AutoClearedValue<>(this, nvAdapter3);
        viewPager.get().setAdapter(dashBoardViewPagerAdapter.get());


        ItemListAdapter featuredAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, item.cityId), this);
        this.featuredItemListAdapter = new AutoClearedValue<>(this, featuredAdapter);
        binding.get().featuredItemRecyclerView.setAdapter(featuredAdapter);

        ItemListAdapter discountAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, item.cityId), this);
        this.discountItemListAdapter = new AutoClearedValue<>(this, discountAdapter);
        binding.get().promoListRecyclerView.setAdapter(discountAdapter);

        ItemListAdapter popularAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, item.cityId), this);
        this.popularItemListAdapter = new AutoClearedValue<>(this, popularAdapter);
        binding.get().popularPlacesRecyclerView.setAdapter(popularAdapter);

        ItemListAdapter recentAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, item.cityId), this);
        this.recentItemListAdapter = new AutoClearedValue<>(this, recentAdapter);
        binding.get().newPlacesRecyclerView.setAdapter(recentAdapter);

        PopularCitiesAdapter popularCitiesAdapter1 = new PopularCitiesAdapter(dataBindingComponent, city ->
                navigationController.navigateToSelectedCityDetail(getActivity(), city.id, city.name, ""), this);
        this.popularCitiesAdapter = new AutoClearedValue<>(this, popularCitiesAdapter1);
        binding.get().popularCitiesRecyclerView.setAdapter(popularCitiesAdapter1);

        FeaturedCitiesAdapter featuredCitiesAdapter1 = new FeaturedCitiesAdapter(dataBindingComponent, city ->
                navigationController.navigateToSelectedCityDetail(getActivity(), city.id, city.name, ""), this);
        this.featuredCitiesAdapter = new AutoClearedValue<>(this, featuredCitiesAdapter1);
        binding.get().featuredCityRecyclerView.setAdapter(featuredCitiesAdapter1);

        RecentCitiesAdapter recentCitiesAdapter1 = new RecentCitiesAdapter(dataBindingComponent, city ->
                navigationController.navigateToSelectedCityDetail(getActivity(), city.id, city.name, ""), this);
        this.recentCitiesAdapter = new AutoClearedValue<>(this, recentCitiesAdapter1);
        binding.get().newCitiesRecyclerView.setAdapter(recentCitiesAdapter1);

    }

    @Override
    protected void initData() {
        if (connectivity.isConnected()) {
            if (startDate.equals(Constants.ZERO)) {

                startDate = getDateTime();
                Utils.setDatesToShared(startDate, endDate, pref);
            }

        }

        clearAllDataViewModel.getDeleteAllDataData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case ERROR:

                    case SUCCESS:
                        break;
                }
            }
        });

        blogViewModel.setNewsFeedObj(String.valueOf(Config.LIST_NEW_FEED_COUNT_PAGER), String.valueOf(blogViewModel.offset));
        blogViewModel.getNewsFeedData().observe(this, listResource -> {

            if (listResource != null) {

                switch (listResource.status) {

                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (listResource.data != null) {

                            // Update the data
                            dashBoardViewPagerAdapter.get().replaceNewsFeedList(listResource.data);

                        }

                        break;

                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {
                            // Update the data

                            dashBoardViewPagerAdapter.get().replaceNewsFeedList(listResource.data);

                        }

                        blogViewModel.setLoadingState(false);

                        break;

                    case ERROR:
                        // Error State

                        blogViewModel.setLoadingState(false);

                        break;
                    default:
                        // Default

                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

                if (blogViewModel.offset > 1) {
                    // No more data for this list
                    // So, Block all future loading
                    blogViewModel.forceEndLoading = true;
                }

            }

        });

        discountItemViewModel.setDiscountItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, discountItemViewModel.discountItemParameterHolder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));
        discountItemViewModel.getDiscountItemListByKeyData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {
                    case LOADING:
                        if (result.data != null) {
                            replaceDiscountItem(result.data);
                        }
                        break;
                    case SUCCESS:

                        if (result.data != null) {
                            //    replaceDiscountItem(result.data);
                        }
                        discountItemViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        discountItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        featuredItemViewModel.setFeaturedItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));
        featuredItemViewModel.getFeaturedItemListByKeyData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {

                    case LOADING:
                        if (result.data != null) {
                            replaceFeaturedItem(result.data);
                        }

                        break;

                    case SUCCESS:
                        if (result.data != null) {
                            replaceFeaturedItem(result.data);
                        }
                        featuredItemViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        featuredItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        popularItemViewModel.setPopularItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularItemViewModel.popularItemParameterHolder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));
        popularItemViewModel.getPopularItemListByKeyData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {
                    case LOADING:

                        if (result.data != null) {
                            replacePopularPlacesList(result.data);
                        }

                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            replacePopularPlacesList(result.data);
                        }
                        popularItemViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        popularItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        recentItemViewModel.setRecentItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, recentItemViewModel.recentItemParameterHolder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));
        recentItemViewModel.getRecentItemListByKeyData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {

                    case LOADING:
                        if (result.data != null) {
                            try {
                                replaceRecentItemList(result.data);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            try {
                                replaceRecentItemList(result.data);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        recentItemViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        recentItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        popularCitiesViewModel.setPopularCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularCitiesViewModel.popularCitiesParameterHolder);
        popularCitiesViewModel.getPopularCityListData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {

                    case LOADING:

                        if (result.data != null) {
                            replacePopularCitiesList(result.data);
                        }
                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            replacePopularCitiesList(result.data);
                        }
                        popularCitiesViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        popularCitiesViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        featuredCitiesViewModel.setFeaturedCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredCitiesViewModel.featuredCitiesParameterHolder);
        featuredCitiesViewModel.getFeaturedCityListData().observe(this, result -> {
            if (result != null) {

                switch (result.status) {
                    case LOADING:
                        if (result.data != null) {
                            replaceFeaturedCitiesList(result.data);
                        }

                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            replaceFeaturedCitiesList(result.data);
                        }
                        featuredCitiesViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        featuredCitiesViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        recentCitiesViewModel.setRecentCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, recentCitiesViewModel.recentCitiesParameterHolder);
        recentCitiesViewModel.getRecentCityListData().observe(this, result -> {
            if (result != null) {

                switch (result.status) {
                    case LOADING:
                        if (result.data != null) {
                            replaceRecentCitiesList(result.data);
                        }

                        break;
                    case SUCCESS:
                        if (result.data != null) {
                            replaceRecentCitiesList(result.data);
                        }
                        recentCitiesViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        recentCitiesViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        recentCitiesViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(recentCitiesViewModel.isLoading);
            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        return dist; // output distance, in MILES
    }

    private int date(String itemDate) throws ParseException {
        SimpleDateFormat dates = new SimpleDateFormat("MM/dd/yyyy");
        //Date date2 = dates.parse(itemDate);
        //Date currentTime = Calendar.getInstance().getTime();
        Date currentDate = dates.parse(String.valueOf(Calendar.getInstance().getTime()));
        long difference = Math.abs(currentDate.getTime() - dates.parse(itemDate).getTime());
        long differenceDates = difference / (24 * 60 * 60 * 1000);
        //String dayDifference = Long.toString(differenceDates);
        return (int) differenceDates;
    }
    @SuppressLint("NewApi")
    private void replaceFeaturedItem(List<Item> itemList) {
                /*List<Item> featuredList = new ArrayList<>();
                for (Item item : itemList) {
                    if (item.isPromotion.equals("1") || item.isFeatured.equals("1")&& distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                            Double.parseDouble(item.lat), Double.parseDouble(item.lng)) <= Double.parseDouble(Constants.CONST_RADIUS)) {
                        featuredList.add(item);
                    }
                    distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                            Double.parseDouble(item.lat), Double.parseDouble(item.lng));
                }
                if(featuredList.size() > 0){
                    itemList.addAll(0, featuredList);
                }*/
        this.featuredItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceDiscountItem(List<Item> itemList) {
       /* if (itemList != null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<Item> discountList = new ArrayList<>();
                for (Item item : itemList) {
                    if (item.isPromotion.equals("1") || item.isFeatured.equals("1")&& distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                            Double.parseDouble(item.lat), Double.parseDouble(item.lng)) <= Double.parseDouble(Constants.CONST_RADIUS)) {
                        discountList.add(item);
                      /*  distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                                Double.parseDouble(item.lat), Double.parseDouble(item.lng));
                    }
                }
                itemList.sort((o1, o2) -> Double.compare(distance(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng), Float.parseFloat(String.valueOf(gpsTracker.getLatitude())),
                        Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))), distance(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude())))));
                if(discountList.size() > 0) {
                    itemList.addAll(0, discountList);
                }
            }
        } */
        this.discountItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replacePopularPlacesList(List<Item> itemList) {
       /* if (itemList != null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<Item> popularList = new ArrayList<>();
                for (Item item : itemList) {
                    if (item.ratingDetails.totalRatingValue >= 3 && distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                            Double.parseDouble(item.lat), Double.parseDouble(item.lng)) <= Double.parseDouble(Constants.CONST_RADIUS)) {
                        popularList.add(item);
                        distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                                Double.parseDouble(item.lat), Double.parseDouble(item.lng));
                    }
                }
                itemList.sort((o1, o2) -> Double.compare(distance(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng), Float.parseFloat(String.valueOf(gpsTracker.getLatitude())),
                        Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))), distance(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude())))));

                if(popularList.size() > 0) {
                    itemList.addAll(0, popularList);
                }
            }
        } */
        this.popularItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceRecentItemList(List<Item> itemList) throws ParseException {
        /*if (itemList != null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<Item> recentList = new ArrayList<>();
               // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

                for (Item item : itemList) {
                    if (item.isPromotion.equals("1") || item.isFeatured.equals("1") && distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                            Double.parseDouble(item.lat), Double.parseDouble(item.lng)) <= Double.parseDouble(Constants.CONST_RADIUS) && date(item.addedDate) <= 15) {
                        recentList.add(item);
                    }
                }
                itemList.sort((o1, o2) -> Double.compare(distance(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng), Float.parseFloat(String.valueOf(gpsTracker.getLatitude())),
                                Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))), distance(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude())))));

                if (recentList.size() > 0) {
                    itemList.addAll(0, recentList);
                }
            }
        } */
        this.recentItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replacePopularCitiesList(List<City> citiesList) {
        /*if (citiesList != null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<City> popularCitiesList = new ArrayList<>();
                for (City city : citiesList) {
                    if (city.addedDate.equals("1")) {
                        popularCitiesList.add(city);
                        distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                                Double.parseDouble(city.lat), Double.parseDouble(city.lng));
                    }
                }
                citiesList.sort((o1, o2) -> Double.compare(distance(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))),
                        distance(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude())))));

                if (popularCitiesList.size() > 0) {
                    citiesList.addAll(0, popularCitiesList);
                }
            }
        } */
        this.popularCitiesAdapter.get().replace(citiesList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceFeaturedCitiesList(List<City> cities) {
        /*if (cities != null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<City> featuredCitiesList = new ArrayList<>();
                for (City city : cities) {
                    if (city.isFeatured.equals("1")) {
                        featuredCitiesList.add(city);
                        distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                                Double.parseDouble(city.lat), Double.parseDouble(city.lng));
                    }
                }
                featuredCitiesList.sort((o1, o2) -> Double.compare(distance(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))),
                        distance(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude())))));

                if(featuredCitiesList.size() > 0) {
                    cities.addAll(0, featuredCitiesList);
                }
            }
        } */
        this.featuredCitiesAdapter.get().replace(cities);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceRecentCitiesList(List<City> cities) {
        /*if (cities != null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<City> recentCitiesList = new ArrayList<>();
                for (City city : cities) {
                    if (city.addedDate.equals("1")) {
                        recentCitiesList.add(city);
                        distance(gpsTracker.getLatitude(), gpsTracker.getLongitude(),
                                Double.parseDouble(city.lat), Double.parseDouble(city.lng));
                    }
                }
                if(recentCitiesList.size() > 0) {
                    cities.addAll(0, recentCitiesList);
                }
            }
        } */
        this.recentCitiesAdapter.get().replace(cities);
        binding.get().executePendingBindings();
    }

    @Override
    public void onDispatched() {

    }


    private void startPagerAutoSwipe() {

        update = () -> {
            if (!touched) {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }

                if (viewPager.get() != null) {
                    viewPager.get().setCurrentItem(currentPage++, true);
                }

            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 1000, 3000);
    }

    private void setUnTouchedTimer() {

        if (unTouchedTimer != null) {
            unTouchedTimer.cancel();
            unTouchedTimer.purge();

        }
        unTouchedTimer = new Timer();
        unTouchedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                touched = false;

                handler.post(update);
            }
        }, 3000, 6000);
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    /*private Runnable runnableCode = new Runnable() {
        int count = 0;

        String[] text;

        @Override
        public void run() {
            if (!loginUserId.isEmpty()) {
                String name = pref.getString(Constants.USER_NAME, Constants.EMPTY_STRING);
                String firstName, lastName;

                if (name.split("\\w+").length > 1) ;
                {
                    //lastName = name.substring((name.lastIndexOf(""+1)));
                    firstName = name.substring(0, name.lastIndexOf(' '));
                }
                text = new String[]{
                        "Hello " + firstName + ",\nWelcome to The Sable Business Directory!",

                        "The Sable Business Directory is designed to help users find black owned businesses and service providers.",

                        "We provide a one of a kind online platform that alerts users when they are near a black owned business.",

                        "We are combining geo-search, social media and e-commerce technologies to make it easier to find, rate " +
                                "and review black owned businesses and service providers.",

                        "We promote high quality products and services by encouraging, customers maintain the directory by adding, rating " +
                                " and reviewing the black owned businesses and service providers they frequent.",

                        "Our combined technologies then compile those listings, ratings and reviews to " +
                                "provide a directory that alerts users of black owned business and service providers " +
                                "near their current location.",

                        "Owners and providers benefit because 88% of people trust online reviews. Online reviews are an important way you can increase " +
                                "sales. This is especially important for local businesses and service providers.",

                        "Adding and reviewing listings is free and easy. To protect the privacy of our users and insure high quality feedback " +
                                "we require users to identify themselves before adding or reviewing a listing.",

                        //"It appears that you've already identified yourself via your Facebook account, " +Constants.USER_NAME+". You are now able to add, rate and review" +
                        //      " black owned businesses registered with Sable."

                };
            } else {
                text = new String[]{
                        "Welcome to The Sable Business Directory!",

                        "The Sable Business Directory is designed to help users find black owned businesses and service providers.",

                        "We provide a one of a kind online platform that alerts users when they are near a black owned business.",

                        "We are combining geo-search, social media and e-commerce technologies to make it easier to find, rate " +
                                "and review black owned businesses and service providers.",

                        "We promote high quality products and services by encouraging, customers maintain the directory by adding, rating " +
                                " and reviewing the black owned businesses and service providers they frequent.",

                        "Our combined technologies then compile those listings, ratings and reviews to " +
                                "provide a directory that alerts users of black owned business and service providers " +
                                "near their current location.",

                        "Owners and providers benefit because 88% of people trust online reviews. Online reviews are an important way you can increase " +
                                "sales. This is especially important for local businesses and service providers.",

                        "Adding and reviewing listings is free and easy. To protect the privacy of our users and insure high quality feedback " +
                                "we require users to identify themselves before adding or reviewing a listing.",

                        //"Tap the button below to begin adding and reviewing black owned businesses using your Facebook account."
                };
            }

            int[] images = {R.mipmap.waving_foreground, R.mipmap.showing_right_foreground,
                    R.mipmap.one_of_akind_foreground, R.mipmap.showing_tablet_foreground, R.mipmap.holding_phone_foreground, R.mipmap.making_thumbs_up_foreground,
                    R.mipmap.showing_laptop_foreground, R.mipmap.showing_with_left_hand_foreground, R.mipmap.smiling_peace_foreground};

            binding.get().headerText.setText(text[count]);
            binding.get().wavingImageView.setImageResource(images[count]);
            imageSwitchHandler.postDelayed(this, FRAME_TIME_MS);
            count++;

            if (count == text.length) {
                count = 0;
            }
        }
    }; */
}

