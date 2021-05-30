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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

        binding.get().floatingActionButton.setOnClickListener(view ->{
            navigationController.navigateToItemUploadActivity(getActivity(),null,"","",null,
                    null,null,null);
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
                discountItemViewModel.setDiscountItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, discountItemViewModel.discountItemParameterHolder);
                featuredItemViewModel.setFeaturedItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder);
                popularItemViewModel.setPopularItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularItemViewModel.popularItemParameterHolder);
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
                navigationController.navigateToSelectedCityDetail(getActivity(), city.id, city.name,""), this);
        this.popularCitiesAdapter = new AutoClearedValue<>(this, popularCitiesAdapter1);
        binding.get().popularCitiesRecyclerView.setAdapter(popularCitiesAdapter1);

        FeaturedCitiesAdapter featuredCitiesAdapter1 = new FeaturedCitiesAdapter(dataBindingComponent, city ->
                navigationController.navigateToSelectedCityDetail(getActivity(), city.id, city.name,""), this);
        this.featuredCitiesAdapter = new AutoClearedValue<>(this, featuredCitiesAdapter1);
        binding.get().featuredCityRecyclerView.setAdapter(featuredCitiesAdapter1);

        RecentCitiesAdapter recentCitiesAdapter1 = new RecentCitiesAdapter(dataBindingComponent, city ->
                navigationController.navigateToSelectedCityDetail(getActivity(), city.id, city.name,""), this);
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

        discountItemViewModel.setDiscountItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, discountItemViewModel.discountItemParameterHolder);
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

        featuredItemViewModel.setFeaturedItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder);
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

        popularItemViewModel.setPopularItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularItemViewModel.popularItemParameterHolder);
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

        recentItemViewModel.setRecentItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, recentItemViewModel.recentItemParameterHolder);
        recentItemViewModel.getRecentItemListByKeyData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {

                    case LOADING:
                        if (result.data != null) {
                            replaceRecentItemList(result.data);
                        }

                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            //     replaceRecentItemList(result.data);
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

    private double milesDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return (6366000 * tt) / 0.00062137;
    }

    @SuppressLint("NewApi")
    private void replaceFeaturedItem(List<Item> items) {

        if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
            List<Item> list = new ArrayList<>();
            for (Item item : items) {
                if (milesDistanceBetweenPoints(Float.parseFloat(item.lat), Float.parseFloat(item.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                        Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS)) && item.isFeatured.equals("1")) {
                    list.add(item);
                } else if (milesDistanceBetweenPoints(Float.parseFloat(item.lat), Float.parseFloat(item.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                        Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS))) {
                    list.add(item);
                }
            }
            this.featuredItemListAdapter.get().replace(list);
        } else {
            this.featuredItemListAdapter.get().replace(items);
        }
        binding.get().executePendingBindings();

        items.sort((o1, o2) -> Double.compare(milesDistanceBetweenPoints(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng),
                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))),
                milesDistanceBetweenPoints(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude())))));
        this.featuredItemListAdapter.get().replace(items);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceDiscountItem(List<Item> itemList) {
        if (itemList != null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<Item> list = new ArrayList<>();

                for (Item item : itemList) {
                    if (milesDistanceBetweenPoints(Float.parseFloat(item.lat), Float.parseFloat(item.lng),
                            Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                            Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS))) {
                        itemList.add(item);
                    }
                }
                itemList.sort(new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        return Double.compare(milesDistanceBetweenPoints(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng), Float.parseFloat(String.valueOf(gpsTracker.getLatitude())),
                                Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))), milesDistanceBetweenPoints(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))));
                    }
                });
            }
            this.discountItemListAdapter.get().replace(itemList);
            binding.get().executePendingBindings();
        }
    }

    @SuppressLint("NewApi")
    private void replacePopularPlacesList(List<Item> itemList) {
        if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
            List<Item> list = new ArrayList<>();
            for (Item item : itemList) {
                if (milesDistanceBetweenPoints(Float.parseFloat(item.lat), Float.parseFloat(item.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                        Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS)) && item.ratingDetails.totalRatingValue >= 3 && item.isPromotion.equals("1")) {
                    itemList.add(item);
                } else if (milesDistanceBetweenPoints(Float.parseFloat(item.lat), Float.parseFloat(item.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                        Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS)) && item.ratingDetails.totalRatingValue >= 3 && item.isFeatured.equals("1")) {
                    itemList.add(item);
                } else if (milesDistanceBetweenPoints(Float.parseFloat(item.lat), Float.parseFloat(item.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS)))
                itemList.add(item);
            }
            this.popularItemListAdapter.get().replace(list);
        } else {
            this.popularItemListAdapter.get().replace(itemList);
        }
        binding.get().executePendingBindings();
        itemList.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return Double.compare(milesDistanceBetweenPoints(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))),
                        milesDistanceBetweenPoints(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))));
            }
        });
        this.popularItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceRecentItemList(List<Item> itemList) {
      /*  if (itemList!=null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<Item> list = new ArrayList<>();
                for (Item item : itemList) {
                    if (milesDistanceBetweenPoints(Float.parseFloat(item.lat), Float.parseFloat(item.lng),
                            Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                            Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS)) && item.ratingDetails.totalRatingValue >=3 && item.isPromotion.equals("1")) {
                        itemList.add(item);
                    } else if (milesDistanceBetweenPoints(Float.parseFloat(item.lat), Float.parseFloat(item.lng),
                            Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                            Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS)) && item.ratingDetails.totalRatingValue >=3 && item.isFeatured.equals("1")) {
                        itemList.add(item);
                    } else if (milesDistanceBetweenPoints(Float.parseFloat(item.lat), Float.parseFloat(item.lng),
                                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                        Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS)));
                        itemList.add(item);
                }
                this.recentItemListAdapter.get().replace(list);
            } else {
                this.recentItemListAdapter.get().replace(itemList);
            }
            binding.get().executePendingBindings();
            itemList.sort(new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    return Double.compare(milesDistanceBetweenPoints(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng),
                            Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))),
                            milesDistanceBetweenPoints(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng), Float.parseFloat(String.valueOf(gpsTracker.getLatitude())),
                                    Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))));
                }
            });
            this.recentItemListAdapter.get().replace(itemList);
            binding.get().executePendingBindings();
        }*/
    }


    @SuppressLint("NewApi")
    private void replacePopularCitiesList(List<City> cities) {
        if (cities!=null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<City> list = new ArrayList<>();
                for (City citi : cities) {
                    if (milesDistanceBetweenPoints(Float.parseFloat(citi.lat), Float.parseFloat(citi.lng),
                            Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                            Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS)) && citi.isFeatured.equals("1"))
                    list.add(citi);
                }
                this.popularCitiesAdapter.get().replace(list);
            } else {
                this.popularCitiesAdapter.get().replace(cities);
            }
            binding.get().executePendingBindings();
        }
        cities.sort(new Comparator<City>() {
            @Override
            public int compare(City o1, City o2) {
                return Double.compare(milesDistanceBetweenPoints(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))),
                        milesDistanceBetweenPoints(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))));
            }
        });
        this.popularCitiesAdapter.get().replace(cities);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceFeaturedCitiesList(List<City> cities) {
        if (cities!=null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<City> list = new ArrayList<>();
                for (City citi : cities) {
                    if (milesDistanceBetweenPoints(Float.parseFloat(citi.lat), Float.parseFloat(citi.lng),
                            Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                            Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS)) && citi.isFeatured.equals("1")) {
                        list.add(citi);
                    }
                }
                this.featuredCitiesAdapter.get().replace(list);

            } else {
                this.featuredCitiesAdapter.get().replace(cities);
            }
            binding.get().executePendingBindings();
        }
        cities.sort(new Comparator<City>() {
            @Override
            public int compare(City o1, City o2) {
                return Double.compare(milesDistanceBetweenPoints(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())),
                        Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))),
                        milesDistanceBetweenPoints(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))));
            }
        });

        this.featuredCitiesAdapter.get().replace(cities);
        binding.get().executePendingBindings();
    }

    @SuppressLint("NewApi")
    private void replaceRecentCitiesList(List<City> cities) {

        this.recentCitiesAdapter.get().replace(cities);
        binding.get().executePendingBindings();
        if (cities!=null) {
            if (!preferences.getString(Constants.RADIUS_KEY, "").equals("All")) {
                List<City> list = new ArrayList<>();
                for (City citi : cities) {
                    if (milesDistanceBetweenPoints(Float.parseFloat(citi.lat), Float.parseFloat(citi.lng),
                            Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))) <=
                            Double.parseDouble(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS))) {
                        list.add(citi);
                    }
                }
                this.recentCitiesAdapter.get().replace(list);
            } else {
                this.recentCitiesAdapter.get().replace(cities);
            }
            binding.get().executePendingBindings();
        }
        cities.sort((o1, o2) -> Double.compare(milesDistanceBetweenPoints(Float.parseFloat(o1.lat), Float.parseFloat(o1.lng),
                Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude()))),
                milesDistanceBetweenPoints(Float.parseFloat(o2.lat), Float.parseFloat(o2.lng),
                        Float.parseFloat(String.valueOf(gpsTracker.getLatitude())), Float.parseFloat(String.valueOf(gpsTracker.getLongitude())))));
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

    private Runnable runnableCode = new Runnable() {
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
    };
}

