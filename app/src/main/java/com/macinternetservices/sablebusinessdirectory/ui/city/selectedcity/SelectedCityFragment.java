package com.macinternetservices.sablebusinessdirectory.ui.city.selectedcity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentSelectedCityBinding;
import com.macinternetservices.sablebusinessdirectory.ui.category.adapter.CityCategoryAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.city.selectedcity.adapter.ItemCollectionRowAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.ui.dashboard.adapter.DashBoardViewPagerAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.item.adapter.ItemListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.item.adapter.ItemPopularListAdapter;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.blog.BlogViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.city.CityViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.DiscountItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.FeaturedItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.PopularItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.RecentItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.TouchCountViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.itemcategory.ItemCategoryViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.itemcollection.ItemCollectionViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.Blog;
import com.macinternetservices.sablebusinessdirectory.viewobject.City;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCategory;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCollectionHeader;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Status;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.ItemParameterHolder;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class SelectedCityFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private CityViewModel cityViewModel;
    private ItemCategoryViewModel itemCategoryViewModel;
    private FeaturedItemViewModel featuredItemViewModel;
    private PopularItemViewModel popularItemViewModel;
    private RecentItemViewModel recentItemViewModel;
    private DiscountItemViewModel discountItemViewModel;
    private TouchCountViewModel touchCountViewModel;
    private BlogViewModel blogViewModel;
    private ItemCollectionViewModel itemCollectionViewModel;
    private ImageView[] dots;
    private boolean layoutDone = false;
    private int loadingCount = 0;

    private Runnable update;
    private int NUM_PAGES = 10;
    private int currentPage = 0;
    private boolean touched = false;
    private Timer unTouchedTimer;
    private Handler handler = new Handler();
    private String blogId;
    private PSDialogMsg psDialogMsg;
    private ItemParameterHolder itemParameterHolder = new ItemParameterHolder();

    @Inject
    protected SharedPreferences pref;

    @VisibleForTesting
    private AutoClearedValue<FragmentSelectedCityBinding> binding;
    private AutoClearedValue<ItemListAdapter> featuredItemListAdapter;
    private AutoClearedValue<ItemPopularListAdapter> popularItemListAdapter;
    private AutoClearedValue<ItemListAdapter> recentItemListAdapter;
    private AutoClearedValue<ItemListAdapter> discountItemListAdapter;
    private AutoClearedValue<DashBoardViewPagerAdapter> dashBoardViewPagerAdapter;
    private AutoClearedValue<CityCategoryAdapter> cityCategoryAdapter;
    private AutoClearedValue<ItemCollectionRowAdapter> verticalRowAdapter;
    private AutoClearedValue<ViewPager> viewPager;
    private AutoClearedValue<LinearLayout> pageIndicatorLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentSelectedCityBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_selected_city, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        binding.get().setLoadingMore(connectivity.isConnected());
        setHasOptionsMenu(true);

        return binding.get().getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.blog_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem blogMenuItem = menu.findItem(R.id.action_blog);
        blogMenuItem.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_blog) {
            navigationController.navigateToBlogListBySelectedCity(getActivity(), cityViewModel.cityParameterHolder.id);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initUIAndActions() {

        if (!Config.ENABLE_ITEM_UPLOAD) {
            binding.get().floatingActionButton.setVisibility(View.GONE);
        } else {
            binding.get().floatingActionButton.setVisibility(View.VISIBLE);
        }

        binding.get().shareImageView.setVisibility(View.GONE);
        psDialogMsg = new PSDialogMsg(getActivity(), false);

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

        binding.get().floatingActionButton.setOnClickListener(view ->
                Utils.navigateOnUserVerificationActivity(userIdToVerify, loginUserId, psDialogMsg, getActivity(), navigationController, () ->
                        navigationController.navigateToItemUploadActivity(getActivity(), null,"","",null,
                                null,null,null)));

        viewPager = new AutoClearedValue<>(this, binding.get().blogViewPager);

        pageIndicatorLayout = new AutoClearedValue<>(this, binding.get().pagerIndicator);


        binding.get().blogViewAllTextView.setOnClickListener(v -> navigationController.navigateToBlogListBySelectedCity(getActivity(), selectedCityId));

        binding.get().knowMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationController.navigateToCityActivity(getActivity(), cityViewModel.cityName);
            }
        });


        binding.get().featuredViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationController.navigateToHomeFilteringActivity(getActivity(), featuredItemViewModel.featuredItemParameterHolder, getString(R.string.dashboard_best_things), cityViewModel.lat, cityViewModel.lng, cityViewModel.cityName);
            }
        });

        binding.get().popularViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationController.navigateToHomeFilteringActivity(getActivity(), popularItemViewModel.popularItemParameterHolder, getString(R.string.dashboard_popular_places), cityViewModel.lat, cityViewModel.lng, cityViewModel.cityName);
            }
        });

        binding.get().recentItemViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navigationController.navigateToHomeFilteringActivity(getActivity(), recentItemViewModel.recentItemParameterHolder, getString(R.string.dashboard_new_places), cityViewModel.lat, cityViewModel.lng, cityViewModel.cityName);

            }
        });

        binding.get().promoViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navigationController.navigateToHomeFilteringActivity(getActivity(), discountItemViewModel.discountItemParameterHolder, getString(R.string.dashboard_promo_list), cityViewModel.lat, cityViewModel.lng, cityViewModel.cityName);

            }
        });

        binding.get().shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.get().categoryViewAllTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationController.navigateToCategoryActivity(getActivity());
            }
        });

        if (viewPager.get() != null && viewPager.get() != null && viewPager.get() != null) {
            viewPager.get().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    currentPage = position;

                    if (pageIndicatorLayout.get() != null) {

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
        }

        startPagerAutoSwipe();
        setCityTouchCount();

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            cityViewModel.loadingDirection = Utils.LoadingDirection.top;

            // reset itemCategoryViewModel.offset
            cityViewModel.offset = 0;

            // reset itemCategoryViewModel.forceEndLoading
            cityViewModel.forceEndLoading = false;

            // update live data

            blogViewModel.setBlogByIdObj(String.valueOf(Config.LIST_NEW_FEED_COUNT_PAGER), String.valueOf(blogViewModel.offset));
            discountItemViewModel.setDiscountItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, discountItemViewModel.discountItemParameterHolder);
            featuredItemViewModel.setFeaturedItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder);
            popularItemViewModel.setPopularItemListByKeyObj(loginUserId, String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularItemViewModel.popularItemParameterHolder);
            touchCountViewModel.setTouchCountPostDataObj(loginUserId, cityViewModel.cityParameterHolder.id, Constants.CITY, cityViewModel.cityParameterHolder.id);
            itemCollectionViewModel.setAllItemCollectionObj(cityViewModel.cityParameterHolder.id, String.valueOf(Config.COLLECTION_PRODUCT_LIST_LIMIT), Constants.ZERO);
            cityViewModel.setCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, cityViewModel.cityParameterHolder);
            recentItemViewModel.setRecentItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, recentItemViewModel.recentItemParameterHolder);
            itemCategoryViewModel.setCategoryListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, cityViewModel.cityParameterHolder.id);
        });
    }

    @Override
    protected void initViewModels() {

        cityViewModel = new ViewModelProvider(this, viewModelFactory).get(CityViewModel.class);
        itemCategoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemCategoryViewModel.class);
        featuredItemViewModel= new ViewModelProvider(this, viewModelFactory).get(FeaturedItemViewModel.class);
        popularItemViewModel = new ViewModelProvider(this, viewModelFactory).get(PopularItemViewModel.class);
        recentItemViewModel = new ViewModelProvider(this, viewModelFactory).get(RecentItemViewModel.class);
        popularItemViewModel = new ViewModelProvider(this, viewModelFactory).get(PopularItemViewModel.class);
        discountItemViewModel = new ViewModelProvider(this, viewModelFactory).get(DiscountItemViewModel.class);
        blogViewModel = new ViewModelProvider(this, viewModelFactory).get(BlogViewModel.class);
        touchCountViewModel = new ViewModelProvider(this, viewModelFactory).get(TouchCountViewModel.class);
        itemCollectionViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemCollectionViewModel.class);

    }

    @Override
    protected void initAdapters() {

        DashBoardViewPagerAdapter nvAdapter3 = new DashBoardViewPagerAdapter(dataBindingComponent, blog -> {
            navigationController.navigateToBlogDetailActivity(SelectedCityFragment.this.getActivity(), blog.id, blog.cityId);
        });

        this.dashBoardViewPagerAdapter = new AutoClearedValue<>(this, nvAdapter3);
        viewPager.get().setAdapter(dashBoardViewPagerAdapter.get());

        CityCategoryAdapter cityCategoryAdapter = new CityCategoryAdapter(dataBindingComponent,
                new CityCategoryAdapter.CityCategoryClickCallback() {
                    @Override
                    public void onClick(ItemCategory category) {

                        if (Config.SHOW_SUBCATEGORY) {
                            navigationController.navigateToSubCategoryActivity(SelectedCityFragment.this.getActivity(), category.id, category.name);
                        }
                        else {
                            itemParameterHolder.cat_id = category.id;
                            itemParameterHolder.city_id= selectedCityId;
                            navigationController.navigateToHomeFilteringActivity(SelectedCityFragment.this.getActivity(), itemParameterHolder, category.name, selectedCityLat, selectedCityLng, selectedCityName);
                        }
                    }
                }, this);

        this.cityCategoryAdapter = new AutoClearedValue<>(this, cityCategoryAdapter);
        binding.get().cityCategoryRecyclerView.setAdapter(cityCategoryAdapter);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ItemCollectionRowAdapter verticalRowAdapter1 = new ItemCollectionRowAdapter(dataBindingComponent, new ItemCollectionRowAdapter.ItemClickCallback() {
            @Override
            public void onClick(Item item) {
                navigationController.navigateToSelectedItemDetail(getActivity(), item.id, item.name, item.cityId);
            }

            @Override
            public void onViewAllClick(ItemCollectionHeader itemCollectionHeader) {
                navigationController.navigateToCollectionItemList(getActivity(), itemCollectionHeader.id, itemCollectionHeader.name, itemCollectionHeader.defaultPhoto.imgPath);
            }
        });

        this.verticalRowAdapter = new AutoClearedValue<>(this, verticalRowAdapter1);
        binding.get().collectionRecyclerView.setAdapter(verticalRowAdapter1);
        binding.get().collectionRecyclerView.setNestedScrollingEnabled(false);


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        ItemListAdapter featuredItemListAdapter1 = new ItemListAdapter(dataBindingComponent, item -> navigationController.navigateToSelectedItemDetail(SelectedCityFragment.this.getActivity(), item.id, item.name, cityViewModel.cityParameterHolder.id), this);

        this.featuredItemListAdapter = new AutoClearedValue<>(this, featuredItemListAdapter1);
        binding.get().featuredItemRecyclerView.setAdapter(featuredItemListAdapter1);

        ItemPopularListAdapter popularAdapter = new ItemPopularListAdapter(dataBindingComponent, item -> navigationController.navigateToSelectedItemDetail(SelectedCityFragment.this.getActivity(), item.id, item.name, cityViewModel.cityParameterHolder.id), this);

        this.popularItemListAdapter = new AutoClearedValue<>(this, popularAdapter);
        binding.get().popularItemRecyclerView.setAdapter(popularAdapter);

        ItemListAdapter recentAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, cityViewModel.cityParameterHolder.id), this);

        this.recentItemListAdapter = new AutoClearedValue<>(this, recentAdapter);
        binding.get().recentItemRecyclerView.setAdapter(recentAdapter);

        ItemListAdapter discountAdapter = new ItemListAdapter(dataBindingComponent, item ->
                navigationController.navigateToSelectedItemDetail(this.getActivity(), item.id, item.name, cityViewModel.cityParameterHolder.id), this);

        this.discountItemListAdapter = new AutoClearedValue<>(this, discountAdapter);
        binding.get().promoRecyclerView.setAdapter(discountAdapter);


    }

    private void replaceDiscountItemList(List<Item> itemList) {
        this.discountItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceRecentItemList(List<Item> itemList) {
        this.recentItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replacePopularItemList(List<Item> itemList) {
        this.popularItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceFeaturedItemList(List<Item> itemList) {

        this.featuredItemListAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceCityCategory(List<ItemCategory> categories) {
        cityCategoryAdapter.get().replace(categories);
        binding.get().executePendingBindings();
    }

    private void replaceCollection(List<ItemCollectionHeader> itemCollectionHeaders) {
        verticalRowAdapter.get().replaceCollectionHeader(itemCollectionHeaders);
        binding.get().executePendingBindings();
    }


    @Override
    protected void initData() {

        getIntentData();

        loadProducts();

        cityViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(cityViewModel.isLoading);
            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }

        });
    }

    private void getIntentData() {

        if (getActivity() != null) {

            cityViewModel.cityParameterHolder.id = getActivity().getIntent().getStringExtra(Constants.CITY_ID);
            featuredItemViewModel.featuredItemParameterHolder.city_id = getActivity().getIntent().getStringExtra(Constants.CITY_ID);
            popularItemViewModel.popularItemParameterHolder.city_id = getActivity().getIntent().getStringExtra(Constants.CITY_ID);
            recentItemViewModel.recentItemParameterHolder.city_id = getActivity().getIntent().getStringExtra(Constants.CITY_ID);
            popularItemViewModel.popularItemParameterHolder.city_id = getActivity().getIntent().getStringExtra(Constants.CITY_ID);
            discountItemViewModel.discountItemParameterHolder.city_id = getActivity().getIntent().getStringExtra(Constants.CITY_ID);

            pref.edit().putString(Constants.CITY_ID, cityViewModel.cityParameterHolder.id).apply();


        }
    }

    private void loadProducts() {

        //Blog

        if (getActivity() != null) {
            blogId = getActivity().getIntent().getStringExtra(Constants.BLOG_ID);
            selectedCityId = getActivity().getIntent().getStringExtra(Constants.CITY_ID);
        }

        blogViewModel.setNewsFeedByCityIdObj(selectedCityId, String.valueOf(Config.LIST_NEW_FEED_COUNT_BY_CITY_ID), String.valueOf(blogViewModel.offset));

        blogViewModel.getNewsFeedByCityIdData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:
                        replaceNewsFeedList(result.data);
                        blogViewModel.setLoadingState(false);
                        break;

                    case LOADING:
                        replaceNewsFeedList(result.data);
                        break;

                    case ERROR:

                        blogViewModel.setLoadingState(false);
                        break;
                }
            }

        });

        //Blog

        //City Detail

        cityViewModel.setCityListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, cityViewModel.cityParameterHolder);

        cityViewModel.getCityListData().observe(this, result -> {

            if (result != null) {

                switch (result.status) {

                    case ERROR:
                        break;

                    case LOADING:

                        if (result.data != null) {
                            if (result.data.size() > 0) {
                                binding.get().cityNameTextView.setText(result.data.get(0).name);
                                cityViewModel.cityName = result.data.get(0).name;
                                binding.get().cityDescriptionTextView.setText(result.data.get(0).description);
                                cityViewModel.lat = result.data.get(0).lat;
                                cityViewModel.lng = result.data.get(0).lng;
                                cityViewModel.cityName = result.data.get(0).name;
                                dataBindingComponent.getFragmentBindingAdapters().bindFullImage(binding.get().cityImageView, result.data.get(0).defaultPhoto.imgPath);

                                updateCityPref(result.data.get(0));
                            }
                        }

                        break;

                    case SUCCESS:

                        if (result.data != null) {
                            if (result.data.size() > 0) {
                                binding.get().cityNameTextView.setText(result.data.get(0).name);
                                cityViewModel.cityName = result.data.get(0).name;
                                binding.get().cityDescriptionTextView.setText(result.data.get(0).description);
                                cityViewModel.lat = result.data.get(0).lat;
                                cityViewModel.lng = result.data.get(0).lng;
                                cityViewModel.cityName = result.data.get(0).name;
                                dataBindingComponent.getFragmentBindingAdapters().bindFullImage(binding.get().cityImageView, result.data.get(0).defaultPhoto.imgPath);

                                updateCityPref(result.data.get(0));
                                cityViewModel.setLoadingState(false);
                            }
                        }

                        break;

                }
            }
        });

        //City Detail

        //City Category

        itemCategoryViewModel.setCategoryListObj(String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, cityViewModel.cityParameterHolder.id);

        itemCategoryViewModel.getCategoryListData().observe(this, listResource -> {

            if (listResource != null) {

                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {

                            if (listResource.data.size() > 0) {
                                replaceCityCategory(listResource.data);
                            }

                        }

                        break;

                    case LOADING:

                        if (listResource.data != null) {

                            if (listResource.data.size() > 0) {
                                replaceCityCategory(listResource.data);
                            }

                        }
                        itemCategoryViewModel.setLoadingState(false);
                        break;

                    case ERROR:
                        break;
                }
            }
        });

        //City Category

        //Featured Item

        featuredItemViewModel.setFeaturedItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, featuredItemViewModel.featuredItemParameterHolder);

        featuredItemViewModel.getFeaturedItemListByKeyData().observe(this, result -> {

            if (result != null) {
                switch (result.status) {

                    case ERROR:

                        break;

                    case LOADING:

                        if (result.data != null) {
                            if (result.data.size() > 0) {
                                replaceFeaturedItemList(result.data);
                            }
                        }

                        break;
                    case SUCCESS:

                        if (result.data != null) {
                            if (result.data.size() > 0) {
                                replaceFeaturedItemList(result.data);
                            }
                        }
                        featuredItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        //Featured Item

        //Popular Item

        popularItemViewModel.setPopularItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, popularItemViewModel.popularItemParameterHolder);

        popularItemViewModel.getPopularItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replacePopularItemList(listResource.data);
                            }
                        }
                        popularItemViewModel.setLoadingState(false);
                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replacePopularItemList(listResource.data);
                            }
                        }

                        break;

                    case ERROR:
                        popularItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        //Popular Item

        //Recent Item

        recentItemViewModel.setRecentItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, recentItemViewModel.recentItemParameterHolder);

        recentItemViewModel.getRecentItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceRecentItemList(listResource.data);
                            }
                        }
                        recentItemViewModel.setLoadingState(false);
                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceRecentItemList(listResource.data);
                            }
                        }

                        break;

                    case ERROR:
                        recentItemViewModel.setLoadingState(false);
                        break;
                }
            }
        });

        //Recent Item

        //Discount Item

        discountItemViewModel.setDiscountItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.LIMIT_FROM_DB_COUNT), Constants.ZERO, discountItemViewModel.discountItemParameterHolder);

        discountItemViewModel.getDiscountItemListByKeyData().observe(this, listResource -> {

            if (listResource != null) {
                switch (listResource.status) {
                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceDiscountItemList(listResource.data);
                            } else {
                                binding.get().promoTitleTextView.setVisibility(View.GONE);
                                binding.get().promoViewAllTextView.setVisibility(View.GONE);
                                binding.get().textView12.setVisibility(View.GONE);
                                binding.get().promoRecyclerView.setVisibility(View.GONE);
                            }
                        }
                        discountItemViewModel.setLoadingState(false);

                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceDiscountItemList(listResource.data);
                            } else {
                                binding.get().promoTitleTextView.setVisibility(View.GONE);
                                binding.get().promoViewAllTextView.setVisibility(View.GONE);
                                binding.get().textView12.setVisibility(View.GONE);
                                binding.get().promoRecyclerView.setVisibility(View.GONE);
                            }
                        }


                        break;

                    case ERROR:
                        break;
                }
            }
        });

        //Discount Item

        //ItemCollection

        itemCollectionViewModel.setAllItemCollectionObj(cityViewModel.cityParameterHolder.id, String.valueOf(Config.COLLECTION_PRODUCT_LIST_LIMIT), Constants.ZERO);

        itemCollectionViewModel.getAllItemCollectionHeader().observe(this, listResource -> {

            if (listResource != null) {

                switch (listResource.status) {

                    case ERROR:
                        Utils.psLog("Error is " + listResource.message);
                        break;

                    case LOADING:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceCollection(listResource.data);
                            }
                        }
                        itemCollectionViewModel.setLoadingState(false);
                        break;

                    case SUCCESS:

                        if (listResource.data != null) {
                            if (listResource.data.size() > 0) {
                                replaceCollection(listResource.data);
                            }
                        }

                        break;
                }
            }

        });

        //ItemCollection

        viewPager.get().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {


                if (binding.get() != null && viewPager.get() != null) {
                    if (viewPager.get().getChildCount() > 0) {
                        layoutDone = true;
                        loadingCount++;
                        hideLoading();
                        viewPager.get().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    @Override
    public void onDispatched() {

//        if (homeLatestProductViewModel.loadingDirection == Utils.LoadingDirection.top) {
//
//            LinearLayoutManager layoutManager = (LinearLayoutManager)
//                    binding.get().productList.getLayoutManager();
//
//            if (layoutManager != null) {
//                layoutManager.scrollToPosition(0);
//            }
//
//        }
//
//        if (homeSearchProductViewModel.loadingDirection == Utils.LoadingDirection.top) {
//
//            GridLayoutManager layoutManager = (GridLayoutManager)
//                    binding.get().discountList.getLayoutManager();
//
//            if (layoutManager != null) {
//                layoutManager.scrollToPosition(0);
//            }
//
//        }
//
//        if (homeTrendingProductViewModel.loadingDirection == Utils.LoadingDirection.top) {
//
//            GridLayoutManager layoutManager = (GridLayoutManager)
//                    binding.get().trendingList.getLayoutManager();
//
//            if (layoutManager != null) {
//                layoutManager.scrollToPosition(0);
//            }
//
//        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setupSliderPagination() {

        int dotsCount = dashBoardViewPagerAdapter.get().getCount();

        if (dotsCount > 0 && dots == null) {

            dots = new ImageView[dotsCount];

            if (binding.get() != null) {
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

            dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

        }

    }

    private void hideLoading() {

        if (loadingCount == 3 && layoutDone) {

            binding.get().loadingView.setVisibility(View.GONE);
            binding.get().loadHolder.setVisibility(View.GONE);
        }
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

        if (unTouchedTimer == null) {
            unTouchedTimer = new Timer();
            unTouchedTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    touched = false;

                    handler.post(update);
                }
            }, 3000, 6000);
        } else {
            unTouchedTimer.cancel();
            unTouchedTimer.purge();

            unTouchedTimer = new Timer();
            unTouchedTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    touched = false;

                    handler.post(update);
                }
            }, 3000, 6000);
        }
    }

    private void updateCityPref(City city) {
        pref.edit().putString(Constants.CITY_NAME, city.name).apply();
        pref.edit().putString(Constants.CITY_LAT, city.lat).apply();
        pref.edit().putString(Constants.CITY_LNG, city.lng).apply();
    }

    private void setCityTouchCount() {
        touchCountViewModel.setTouchCountPostDataObj(loginUserId, cityViewModel.cityParameterHolder.id, Constants.CITY, cityViewModel.cityParameterHolder.id);

        touchCountViewModel.getTouchCountPostData().observe(this, result -> {
            if (result != null) {
                if (result.status == Status.SUCCESS) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }

                } else if (result.status == Status.ERROR) {
                    if (this.getActivity() != null) {
                        Utils.psLog(result.status.toString());
                    }
                }
            }
        });
    }

    private void replaceNewsFeedList(List<Blog> blogs) {
        this.dashBoardViewPagerAdapter.get().replaceNewsFeedList(blogs);
        binding.get().executePendingBindings();
    }

    @Override
    public void onResume() {
        loadLoginUserId();
        super.onResume();
    }
}
