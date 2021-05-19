package com.macinternetservices.sablebusinessdirectory.ui.item.search.searchlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentItemListBinding;
import com.macinternetservices.sablebusinessdirectory.ui.city.selectedcity.SelectedCityActivity;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.GPSTracker;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.ItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.City;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Status;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.ItemParameterHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class SearchListFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private boolean typeClicked = false;
    private boolean filterClicked = false;
    private boolean mapFilterClicked = false;
    private List<Item> clearRecyclerView = new ArrayList<>();
    private ItemViewModel itemViewModel;
    SharedPreferences preferences;

    @VisibleForTesting
    private AutoClearedValue<FragmentItemListBinding> binding;
    private AutoClearedValue<SearchListAdapter> adapter;

    GPSTracker gpsTracker;

    //region Override Methods
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        FragmentItemListBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_list, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        setHasOptionsMenu(true);
        gpsTracker=new GPSTracker(getContext());
        gpsTracker.getLocation();

        return binding.get().getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Constants.REQUEST_CODE__ITEM_LIST_FRAGMENT
                && resultCode == Constants.RESULT_CODE__CATEGORY_FILTER) {

            itemViewModel.holder.cat_id = data.getStringExtra(Constants.CATEGORY_ID);
            itemViewModel.holder.sub_cat_id = data.getStringExtra(Constants.SUBCATEGORY_ID);

            typeClicked = (itemViewModel.holder.cat_id != null && !itemViewModel.holder.cat_id.equals(""))
                    || (itemViewModel.holder.sub_cat_id != null && !itemViewModel.holder.sub_cat_id.equals(""));

            typeButtonClicked(typeClicked);

            replaceData(clearRecyclerView);

            loadItemList();

        } else if (requestCode == Constants.REQUEST_CODE__ITEM_LIST_FRAGMENT
                && resultCode == Constants.RESULT_CODE__SPECIAL_FILTER) {

            // Result From Filter
//
            if (data.getSerializableExtra(Constants.FILTERING_HOLDER) != null) {

                itemViewModel.holder = (ItemParameterHolder) data.getSerializableExtra(Constants.FILTERING_HOLDER);

            }

            filterClicked = !itemViewModel.holder.keyword.equals("") || !itemViewModel.holder.order_by.equals(Constants.FILTERING_FEATURE) ||
                    !itemViewModel.holder.is_promotion.equals("") || !itemViewModel.holder.order_type.equals(Constants.FILTERING_DESC) ||
                    !itemViewModel.holder.is_featured.equals("");

            tuneButtonClicked(filterClicked);

            replaceData(clearRecyclerView);

            loadItemList();

        } else if (requestCode == Constants.REQUEST_CODE__MAP_FILTERING
                && resultCode == Constants.RESULT_CODE__MAP_FILTERING) {

            if (data.getSerializableExtra(Constants.ITEM_HOLDER) != null) {

                itemViewModel.holder = (ItemParameterHolder) data.getSerializableExtra(Constants.ITEM_HOLDER);

            }

            mapFilterClicked = !itemViewModel.holder.lat.equals("") || !itemViewModel.holder.lng.equals("") ||
                    !itemViewModel.holder.miles.equals("");

            mapFilterButtonClicked(mapFilterClicked);

            replaceData(clearRecyclerView);

            loadItemList();

        }

    }


    @Override
    protected void initUIAndActions() {

        binding.get().typeButton.setOnClickListener(this::ButtonClick);

        binding.get().tuneButton.setOnClickListener(this::ButtonClick);

        binding.get().sortButton.setOnClickListener(this::ButtonClick);

        binding.get().newsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager layoutManager = (GridLayoutManager)
                        recyclerView.getLayoutManager();

                if (layoutManager != null) {

                    int lastPosition = layoutManager
                            .findLastVisibleItemPosition();

                    if (lastPosition == adapter.get().getItemCount() - 1) {

                        if (!binding.get().getLoadingMore() && !itemViewModel.forceEndLoading) {

                            itemViewModel.loadingDirection = Utils.LoadingDirection.bottom;

                            int limit = Config.ITEM_COUNT;

                            itemViewModel.offset = itemViewModel.offset + limit;

                            loadNextPageItemList(String.valueOf(itemViewModel.offset));

                        }
                    }
                }
            }
        });

        binding.get().swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.view__primary_line));
        binding.get().swipeRefresh.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.global__primary));
        binding.get().swipeRefresh.setOnRefreshListener(() -> {

            itemViewModel.forceEndLoading = false;

            itemViewModel.loadingDirection = Utils.LoadingDirection.top;

            loadItemList();

        });
    }


    @Override
    protected void initViewModels() {
        // ViewModel need to get from ViewModelProviders
        itemViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);

    }

    @Override
    protected void initAdapters() {

        SearchListAdapter nvAdapter = new SearchListAdapter(dataBindingComponent, item -> navigationController.navigateToSelectedItemDetail(getActivity(), item.id, item.name, item.cityId), this);

        this.adapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().newsList.setAdapter(nvAdapter);
    }

    @Override
    protected void initData() {

        if (getActivity() != null) {

            if (this.getActivity() instanceof SearchListActivity) {
                itemViewModel.holder = (ItemParameterHolder) getActivity().getIntent().getSerializableExtra(Constants.ITEM_PARAM_HOLDER_KEY);

            }else if(this.getActivity() instanceof SelectedCityActivity){
                if(getArguments() != null) {
                    itemViewModel.holder = (ItemParameterHolder) getArguments().getSerializable(Constants.ITEM_PARAM_HOLDER_KEY);
                }
            }

            if(itemViewModel.holder != null) {
                if (itemViewModel.holder.city_id.isEmpty() && itemViewModel.holder.keyword.isEmpty()) {
                  //  binding.get().buttonLayout.setVisibility(View.GONE);
                    binding.get().buttonLayout.setVisibility(View.VISIBLE);
                }
                else if (!itemViewModel.holder.keyword.isEmpty()) {
                  //  binding.get().buttonLayout.setVisibility(View.GONE);
                    binding.get().buttonLayout.setVisibility(View.VISIBLE);
                }
                else {
                    binding.get().buttonLayout.setVisibility(View.VISIBLE);
                }
            }

            initItemData();

//            setTouchCount();

            filterClicked = !itemViewModel.holder.keyword.equals(Constants.FILTERING_INACTIVE) ||
                    !itemViewModel.holder.is_featured.equals(Constants.FILTERING_INACTIVE) ||
                    !itemViewModel.holder.is_promotion.equals(Constants.FILTERING_INACTIVE) ||
                    !itemViewModel.holder.rating_value.equals(Constants.FILTERING_INACTIVE) ||
                    !itemViewModel.holder.order_by.equals(Constants.FILTERING_INACTIVE) ||
                    !itemViewModel.holder.order_type.equals(Constants.FILTERING_INACTIVE);

            typeClicked = !itemViewModel.holder.cat_id.equals(Constants.FILTERING_INACTIVE) || !itemViewModel.holder.sub_cat_id.equals(Constants.FILTERING_INACTIVE);

            mapFilterClicked = !itemViewModel.holder.lat.equals("") || !itemViewModel.holder.lng.equals("") ||
                    !itemViewModel.holder.miles.equals("");

            typeButtonClicked(typeClicked);

            tuneButtonClicked(filterClicked);

            mapFilterButtonClicked(mapFilterClicked);

        }

        // touch count post method
//        if (connectivity.isConnected()) {
//
//            touchCountViewModel.setTouchCountPostDataObj(loginUserId, itemViewModel.holder.cat_id, Constants.FILTERING_TYPE_NAME_CAT, selectedCityId);
//
//        }
//        touchCountViewModel.getTouchCountPostData().observe(this, result -> {
//            if (result != null) {
//                if (result.status == Status.SUCCESS) {
//                    if (SearchListFragment.this.getActivity() != null) {
//                        Utils.psLog(result.status.toString());
//                    }
//
//                } else if (result.status == Status.ERROR) {
//                    if (SearchListFragment.this.getActivity() != null) {
//                        Utils.psLog(result.status.toString());
//                    }
//                }
//            }
//        });
    }

    //endregion


    //region Private Methods

    private void initItemData() {

        loadItemList();

        LiveData<Resource<List<Item>>> news = itemViewModel.getItemListByKeyData();

        if (news != null) {

            news.observe(this, listResource -> {
                if (listResource != null) {
                    switch (listResource.status) {
                        case SUCCESS:

                            itemViewModel.setLoadingState(false);

                            if (listResource.data != null) {
                                if (listResource.data.size() == 0) {

                                    if (!binding.get().getLoadingMore()) {
                                        binding.get().noItemConstraintLayout.setVisibility(View.VISIBLE);
                                    }

                                } else {

                                    binding.get().noItemConstraintLayout.setVisibility(View.INVISIBLE);

                                }

                                fadeIn(binding.get().getRoot());

                                replaceData(listResource.data);

                                onDispatched();
                            }
                            break;

                        case LOADING:
                            if (listResource.data != null) {
//                                if (listResource.data.size() == 0) {
////
////                                    if (!binding.get().getLoadingMore()) {
////                                        binding.get().noItemConstraintLayout.setVisibility(View.VISIBLE);
////                                    }
////
////                                } else {
////                                    binding.get().noItemConstraintLayout.setVisibility(View.INVISIBLE);
////
////                                }

                                binding.get().noItemConstraintLayout.setVisibility(View.INVISIBLE);

                                fadeIn(binding.get().getRoot());

                                replaceData(listResource.data);

                                itemViewModel.setLoadingState(true);

                                if (itemViewModel.forceEndLoading) {
                                    itemViewModel.forceEndLoading = false;
                                }

                                onDispatched();
                            }
                            break;

                        case ERROR:

                            itemViewModel.setLoadingState(false);
                            itemViewModel.forceEndLoading = true;

                            if (itemViewModel.getItemListByKeyData() != null) {
                                if (itemViewModel.getItemListByKeyData().getValue() != null) {
                                    if (itemViewModel.getItemListByKeyData().getValue().data != null) {
                                        if (!binding.get().getLoadingMore() && itemViewModel.getItemListByKeyData().getValue().data.size() == 0) {
                                            binding.get().noItemConstraintLayout.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }

                            break;

                        default:
                            break;
                    }
                }
            });
        }

        itemViewModel.getNextPageItemListByKeyData().observe(this, state -> {

            if (state != null) {
                if (state.status == Status.ERROR) {

                    itemViewModel.setLoadingState(false);
                    itemViewModel.forceEndLoading = true;

                }
            }
        });

        itemViewModel.getLoadingState().observe(this, loadingState -> {

            binding.get().setLoadingMore(itemViewModel.isLoading);

            if (loadingState != null && !loadingState) {
                binding.get().swipeRefresh.setRefreshing(false);
            }
        });

    }
    private double miledsDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return (6366000 * tt)/0.00062137;
    }
    @SuppressLint("NewApi")
    private void replaceData(List<Item> newsList) {
/*if (!preferences.getString(Constants.RADIUS_KEY,"").equals("All")) {
    List<Item> itemList = new ArrayList<>();

    for (Item item : newsList) {


        if (miledsDistanceBetweenPoints(Float.valueOf(item.lat), Float.valueOf(item.lng), Float.valueOf(String.valueOf(gpsTracker.getLatitude())), Float.valueOf(String.valueOf(gpsTracker.getLongitude()))) <= Double.valueOf(preferences.getString(Constants.RADIUS_KEY, Constants.CONST_RADIUS))) {
            itemList.add(item);
        }
    }
    adapter.get().replace(itemList);
    binding.get().executePendingBindings();
}else{
    adapter.get().replace(newsList);
    binding.get().executePendingBindings();
}*/

        newsList.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return Double.valueOf(miledsDistanceBetweenPoints(Float.valueOf(o1.lat), Float.valueOf(o1.lng), Float.valueOf(String.valueOf(gpsTracker.getLatitude())), Float.valueOf(String.valueOf(gpsTracker.getLongitude()))))
                        .compareTo(Double.valueOf(miledsDistanceBetweenPoints(Float.valueOf(o2.lat), Float.valueOf(o2.lng), Float.valueOf(String.valueOf(gpsTracker.getLatitude())), Float.valueOf(String.valueOf(gpsTracker.getLongitude())))));
            }
        });

        adapter.get().replace(newsList);
        binding.get().executePendingBindings();
    }


    private void typeButtonClicked(Boolean b) {
        if (b) {
            binding.get().typeButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_list_with_check_black_24), null, null);
        } else {
            binding.get().typeButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_list_black_24), null, null);
        }
    }

    private void tuneButtonClicked(Boolean b) {
        if (b) {

            binding.get().tuneButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_tune_with_check_black_24), null, null);

        } else {
            binding.get().tuneButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_tune_black_24), null, null);
        }
    }

    private void mapFilterButtonClicked(Boolean b) {
        if (b) {

            binding.get().sortButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_filter_map_with_check_black_24), null, null);

        } else {
            binding.get().sortButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baseline_filter_map_black_24), null, null);
        }
    }

    private void ButtonClick(View view) {

        switch (view.getId()) {
            case R.id.typeButton:

                navigationController.navigateToTypeFilterFragment(SearchListFragment.this.getActivity(), itemViewModel.holder.cat_id,
                        itemViewModel.holder.sub_cat_id, itemViewModel.holder, Constants.FILTERING_TYPE_FILTER);

                typeButtonClicked(typeClicked);

                break;

            case R.id.tuneButton:

                navigationController.navigateToTypeFilterFragment(SearchListFragment.this.getActivity(), itemViewModel.holder.cat_id,
                        itemViewModel.holder.sub_cat_id, itemViewModel.holder, Constants.FILTERING_SPECIAL_FILTER);

                tuneButtonClicked(filterClicked);

                break;

            case R.id.sortButton:

//                mBottomSheetDialog.get().show();
//                ButtonSheetClick();

                navigationController.navigateToMapFiltering(this.getActivity(), itemViewModel.holder);

                break;

            default:
                Utils.psLog("No ID for Buttons");
        }
    }

    @Override
    public void onDispatched() {

        if (itemViewModel.loadingDirection == Utils.LoadingDirection.top) {

            if (binding.get() != null) {
                GridLayoutManager layoutManager = (GridLayoutManager)
                        binding.get().newsList.getLayoutManager();

                if (layoutManager != null) {
                    layoutManager.scrollToPositionWithOffset(0, 0);
                }
            }
        }
    }

    private void resetLimitAndOffset() {
        itemViewModel.offset = 0;
    }

    private void loadNextPageItemList(String offset) {

        checkAndUpdateFeatureSorting();

        itemViewModel.setNextPageItemListByKeyObj(String.valueOf(Config.ITEM_COUNT), offset, Utils.checkUserId(loginUserId), itemViewModel.holder);


    }

    private void loadItemList() {

        resetLimitAndOffset();

        checkAndUpdateFeatureSorting();

        itemViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.ITEM_COUNT), Constants.ZERO, itemViewModel.holder);

    }

    private void checkAndUpdateFeatureSorting() {

        if (itemViewModel.holder.is_featured.equals(Constants.ONE)
                && itemViewModel.holder.order_by.equals(Constants.FILTERING_ADDED_DATE)) {
            itemViewModel.holder.order_by = Constants.FILTERING_FEATURE;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        loadLoginUserId();
    }
}

