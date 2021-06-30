package com.macinternetservices.sablebusinessdirectory.ui.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.MainActivity;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentProfileBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.ui.item.adapter.ItemListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.item.history.adapter.HistoryHorizontalListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.item.promote.adapter.ItemPromoteHorizontalListAdapter;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.PSDialogMsg;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.ItemPaidHistoryViewModel.ItemPaidHistoryViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.DisabledItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.HistoryViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.ItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.PendingItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.item.RejectedItemViewModel;
import com.macinternetservices.sablebusinessdirectory.viewmodel.user.UserViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemHistory;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemPaidHistory;
import com.macinternetservices.sablebusinessdirectory.viewobject.User;
import com.macinternetservices.sablebusinessdirectory.viewobject.common.Resource;

import java.util.List;

/**
 * ProfileFragment
 */
public class ProfileFragment extends PSFragment implements DataBoundListAdapter.DiffUtilDispatchedInterface {


    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private HistoryViewModel historyViewModel;
    private ItemViewModel itemViewModel;
    private UserViewModel userViewModel;
    private DisabledItemViewModel disabledViewModel;
    private RejectedItemViewModel rejectedViewModel;
    private PendingItemViewModel pendingViewModel;
    public PSDialogMsg psDialogMsg;
    private ItemPaidHistoryViewModel itemPaidHistoryViewModel;

    @VisibleForTesting
    private AutoClearedValue<FragmentProfileBinding> binding;
    private AutoClearedValue<HistoryHorizontalListAdapter> adapter;
    private AutoClearedValue<ItemListAdapter> approvedAdapter;
    private AutoClearedValue<ItemListAdapter> pendingAdapter;
    private AutoClearedValue<ItemListAdapter> rejectedAdapter;
    private AutoClearedValue<ItemListAdapter> disabledAdapter;
    private AutoClearedValue<ItemPromoteHorizontalListAdapter> itemPromoteHorizontalListAdapter;


    //endregion


    //region Override Methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentProfileBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false, dataBindingComponent);

        binding = new AutoClearedValue<>(this, dataBinding);

        return binding.get().getRoot();
    }


    @Override
    protected void initUIAndActions() {

        if(getActivity() instanceof MainActivity)  {
            ((MainActivity) this.getActivity()).binding.toolbar.setBackgroundColor(getResources().getColor(R.color.global__primary));
            ((MainActivity)getActivity()).updateToolbarIconColor(Color.WHITE);
            ((MainActivity)getActivity()).updateMenuIconWhite();
        }

        psDialogMsg = new PSDialogMsg(getActivity(), false);

        binding.get().transactionList.setNestedScrollingEnabled(false);
        binding.get().editTextView.setOnClickListener(view -> navigationController.navigateToProfileEditActivity(getActivity()));
        binding.get().seeAllTextView.setOnClickListener(view -> navigationController.navigateToUserHistoryActivity(getActivity(),"",""));
        binding.get().approvedSeeAllTextView.setOnClickListener(v ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, getString(R.string.profile__listing), Constants.ONE));
        binding.get().pendingSeeAllTextView.setOnClickListener(v ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, getString(R.string.profile__pending_listing), Constants.ZERO));
        binding.get().rejectedSeeAllTextView.setOnClickListener(v ->
                navigationController.navigateToItemListActivity(getActivity(),  loginUserId, getString(R.string.profile__rejected_listing), Constants.THREE));
        binding.get().disabledSeeAllTextView.setOnClickListener(v ->
                navigationController.navigateToItemListActivity(getActivity(), loginUserId, getString(R.string.profile__disabled_listing), Constants.TWO));
        binding.get().userHistoryTextView.setOnClickListener(view -> navigationController.navigateToUserHistoryActivity(getActivity(),"",""));
        binding.get().favouriteTextView.setOnClickListener(view -> navigationController.navigateToFavouriteActivity(getActivity()));
        binding.get().heartImageView.setOnClickListener(view -> navigationController.navigateToFavouriteActivity(getActivity()));
        binding.get().settingTextView.setOnClickListener(view -> navigationController.navigateToSettingActivity(getActivity()));
        binding.get().userNotificatinTextView.setOnClickListener(view -> navigationController.navigateToNotificationList(getActivity()));
        binding.get().paidAdViewAllTextView.setOnClickListener(view -> navigationController.navigateToUserHistoryActivity(getActivity(), loginUserId, Constants.FLAGPAID));

    }

    @Override
    protected void initViewModels() {
        historyViewModel = new ViewModelProvider(this, viewModelFactory).get(HistoryViewModel.class);
        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
        itemPaidHistoryViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemPaidHistoryViewModel.class);
        itemViewModel = new ViewModelProvider(this, viewModelFactory).get(ItemViewModel.class);
        disabledViewModel = new ViewModelProvider(this, viewModelFactory).get(DisabledItemViewModel.class);
        rejectedViewModel = new ViewModelProvider(this, viewModelFactory).get(RejectedItemViewModel.class);
        pendingViewModel = new ViewModelProvider(this, viewModelFactory).get(PendingItemViewModel.class);

    }

    @Override
    protected void initAdapters() {

        HistoryHorizontalListAdapter nvAdapter = new HistoryHorizontalListAdapter(dataBindingComponent, historyProduct -> navigationController.navigateToItemDetailActivity(ProfileFragment.this.getActivity(), historyProduct, selectedCityId));
        this.adapter = new AutoClearedValue<>(this, nvAdapter);
        binding.get().transactionList.setAdapter(nvAdapter);

        ItemPromoteHorizontalListAdapter itemPromoteAdapter = new ItemPromoteHorizontalListAdapter(dataBindingComponent, new ItemPromoteHorizontalListAdapter.NewsClickCallback() {
            @Override
            public void onClick(ItemPaidHistory itemPaidHistory) {
                navigationController.navigateToDetailActivity(ProfileFragment.this.getActivity(), itemPaidHistory.item);
            }
        }, this);
        this.itemPromoteHorizontalListAdapter = new AutoClearedValue<>(this, itemPromoteAdapter);
        binding.get().userPaidItemRecyclerView.setAdapter(itemPromoteAdapter);

        ItemListAdapter approvedItemAdapter = new ItemListAdapter(dataBindingComponent, item -> {
            navigationController.navigateToSelectedItemDetail(ProfileFragment.this.getActivity(), item.id, item.name, selectedCityId);
        }, this);
        this.approvedAdapter = new AutoClearedValue<>(this, approvedItemAdapter);
        binding.get().approvedListingRecyclerView.setAdapter(approvedItemAdapter);

        ItemListAdapter pendingAdapter = new ItemListAdapter(dataBindingComponent, item -> {
            navigationController.navigateToSelectedItemDetail(ProfileFragment.this.getActivity(), item.id, item.name, selectedCityId);
        }, this);
        this.pendingAdapter = new AutoClearedValue<>(this, pendingAdapter);
        binding.get().pendingRecyclerView.setAdapter(pendingAdapter);

        ItemListAdapter rejectedAdapter = new ItemListAdapter(dataBindingComponent, item -> {
            navigationController.navigateToSelectedItemDetail(ProfileFragment.this.getActivity(), item.id, item.name, selectedCityId);
        }, this);
        this.rejectedAdapter = new AutoClearedValue<>(this, rejectedAdapter);
        binding.get().rejectedRecyclerView.setAdapter(rejectedAdapter);

        ItemListAdapter disabledAdapter = new ItemListAdapter(dataBindingComponent, item -> {
            navigationController.navigateToSelectedItemDetail(ProfileFragment.this.getActivity(), item.id, item.name, selectedCityId);
        }, this);
        this.disabledAdapter = new AutoClearedValue<>(this, disabledAdapter);
        binding.get().disabledRecyclerView.setAdapter(disabledAdapter);
    }

    @Override
    protected void initData() {

        //load basket
        historyViewModel.offset = Config.HISTORY_COUNT;
        historyViewModel.setHistoryItemListObj(String.valueOf(historyViewModel.offset));
        LiveData<List<ItemHistory>> historyProductList = historyViewModel.getAllHistoryItemList();
        if (historyProductList != null) {
            historyProductList.observe(this, listResource -> {
                if (listResource != null) {
                    hideHistoryList(false);
                    replaceProductHistoryData(listResource);
                }else{
                    hideHistoryList(true);
                }

            });
        }

        //User
        userViewModel.getUser(loginUserId).observe(this, listResource -> {

            if (listResource != null) {

                Utils.psLog("Got Data" + listResource.message + listResource.toString());

                switch (listResource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (listResource.data != null) {
                            //fadeIn Animation
                            fadeIn(binding.get().getRoot());

                            binding.get().setUser(listResource.data);
                            Utils.psLog("Photo : " + listResource.data.userProfilePhoto);

                            replaceUserData(listResource.data);

                        }

                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (listResource.data != null) {

                            //fadeIn Animation
                            //fadeIn(binding.get().getRoot());

                            binding.get().setUser(listResource.data);
                            Utils.psLog("Photo : " + listResource.data.userProfilePhoto);

                            replaceUserData(listResource.data);
                        }

                        break;
                    case ERROR:
                        // Error State

                        psDialogMsg.showErrorDialog(listResource.message, getString(R.string.app__ok));
                        psDialogMsg.show();

                        userViewModel.isLoading = false;

                        break;
                    default:
                        // Default
                        userViewModel.isLoading = false;

                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }

            // we don't need any null checks here for the SubCategoryAdapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (listResource != null && listResource.data != null) {
                Utils.psLog("Got Data");


            } else {
                //noinspection Constant Conditions
                Utils.psLog("Empty Data");

            }
        });

        // Get Paid Item History List
        itemPaidHistoryViewModel.setPaidItemHistory(Utils.checkUserId(loginUserId), String.valueOf(Config.PAID_ITEM_COUNT), String.valueOf(itemPaidHistoryViewModel.offset));

        itemPaidHistoryViewModel.getPaidItemHistory().observe(this, result -> {

            if (result != null) {
                switch (result.status) {
                    case SUCCESS:

                        replacePaidItemHistoryList(result.data);
                        itemPaidHistoryViewModel.setLoadingState(false);
                        break;

                    case LOADING:
                        replacePaidItemHistoryList(result.data);

                        break;
                    case ERROR:
                        itemPaidHistoryViewModel.setLoadingState(false);
                        break;

                    default:
                        break;
                }
            }

        });

        //approved item list
        itemViewModel.holder.added_user_id = loginUserId;
        itemViewModel.holder.status = Constants.ONE;
        itemViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.APPROVED_ITEM_COUNT), String.valueOf(itemViewModel.offset), itemViewModel.holder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));

        LiveData<Resource<List<Item>>> news = itemViewModel.getItemListByKeyData();

        if (news != null) {

            news.observe(this, listResource -> {
                if (listResource != null) {

                    Utils.psLog("Got Data" + listResource.message + listResource.toString());

                    switch (listResource.status) {
                        case LOADING:
                            // Loading State
                            // Data are from Local DB

                            if (listResource.data != null) {
                                //fadeIn Animation
                                fadeIn(binding.get().getRoot());

                                // Update the data
                                replaceApprovedListData(listResource.data);

                            }

                            break;

                        case SUCCESS:
                            // Success State
                            // Data are from Server

                            if (listResource.data != null) {
                                if (listResource.data.size() > 0) {
                                    hideApprovedList(false);
                                    replaceApprovedListData(listResource.data);
                                }else {
                                    hideApprovedList(true);
                                }
                            }

                            itemViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            hideApprovedList(true);

                            itemViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (itemViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        itemViewModel.forceEndLoading = true;
                    }
                }
            });
        }
        //end

        //pending item list
        pendingViewModel.holder.added_user_id = loginUserId;
        pendingViewModel.holder.status = Constants.ZERO;
        pendingViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.PENDING_ITEM_COUNT), String.valueOf(pendingViewModel.offset), pendingViewModel.holder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));

        LiveData<Resource<List<Item>>> pendingViewModelItemListByKeyData = pendingViewModel.getItemListByKeyData();

        if (pendingViewModelItemListByKeyData != null) {

            pendingViewModelItemListByKeyData.observe(this, listResource -> {
                if (listResource != null) {

                    Utils.psLog("Got Data" + listResource.message + listResource.toString());

                    switch (listResource.status) {
                        case LOADING:
                            // Loading State
                            // Data are from Local DB

                            if (listResource.data != null) {
                                //fadeIn Animation
                                fadeIn(binding.get().getRoot());

                                // Update the data
                                replacePendingListData(listResource.data);

                            }

                            break;

                        case SUCCESS:
                            // Success State
                            // Data are from Server

                            if (listResource.data != null) {
                                if (listResource.data.size() > 0) {
                                    hidePendingList(false);
                                    replacePendingListData(listResource.data);
                                }else {
                                    hidePendingList(true);
                                }
                            }

                            pendingViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            hidePendingList(true);

                            pendingViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (pendingViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        pendingViewModel.forceEndLoading = true;
                    }
                }
            });
        }
        //end

        //rejected item list
        rejectedViewModel.holder.added_user_id = loginUserId;
        rejectedViewModel.holder.status = Constants.THREE;
        rejectedViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.REJECTED_ITEM_COUNT), String.valueOf(rejectedViewModel.offset), rejectedViewModel.holder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));

        LiveData<Resource<List<Item>>> rejectedLiveData = rejectedViewModel.getItemListByKeyData();

        if (rejectedLiveData != null) {

            rejectedLiveData.observe(this, listResource -> {
                if (listResource != null) {

                    Utils.psLog("Got Data" + listResource.message + listResource.toString());

                    switch (listResource.status) {
                        case LOADING:
                            // Loading State
                            // Data are from Local DB

                            if (listResource.data != null) {
                                //fadeIn Animation
                                fadeIn(binding.get().getRoot());

                                // Update the data
                                replaceRejectedListData(listResource.data);

                            }

                            break;

                        case SUCCESS:
                            // Success State
                            // Data are from Server

                            if (listResource.data != null) {
                                if (listResource.data.size() > 0) {
                                    hideRejectedList(false);
                                    replaceRejectedListData(listResource.data);
                                }else {
                                    hideRejectedList(true);
                                }
                            }

                            rejectedViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            hideRejectedList(true);

                            rejectedViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (rejectedViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        rejectedViewModel.forceEndLoading = true;
                    }
                }
            });
        }
        //end

        //disabled item list
        disabledViewModel.holder.added_user_id = loginUserId;
        disabledViewModel.holder.status = Constants.TWO;
        disabledViewModel.setItemListByKeyObj(Utils.checkUserId(loginUserId), String.valueOf(Config.DISABLED_ITEM_COUNT), String.valueOf(disabledViewModel.offset), disabledViewModel.holder, String.valueOf(Config.CurrentLocation.getLatitude()), String.valueOf(Config.CurrentLocation.getLongitude()));

        LiveData<Resource<List<Item>>> disabledLiveData = disabledViewModel.getItemListByKeyData();

        if (disabledLiveData != null) {

            disabledLiveData.observe(this, listResource -> {
                if (listResource != null) {

                    Utils.psLog("Got Data" + listResource.message + listResource.toString());

                    switch (listResource.status) {
                        case LOADING:
                            // Loading State
                            // Data are from Local DB

                            if (listResource.data != null) {
                                //fadeIn Animation
                                fadeIn(binding.get().getRoot());

                                // Update the data
                                replaceDisabledListData(listResource.data);

                            }

                            break;

                        case SUCCESS:
                            // Success State
                            // Data are from Server

                            if (listResource.data != null) {
                                if (listResource.data.size() > 0) {
                                    hideDisabledList(false);
                                    replaceDisabledListData(listResource.data);
                                }else {
                                    hideDisabledList(true);
                                }
                            }

                            disabledViewModel.setLoadingState(false);

                            break;

                        case ERROR:
                            // Error State

                            hideDisabledList(true);

                            disabledViewModel.setLoadingState(false);

                            break;
                        default:
                            // Default

                            break;
                    }

                } else {

                    // Init Object or Empty Data
                    Utils.psLog("Empty Data");

                    if (disabledViewModel.offset > 1) {
                        // No more data for this list
                        // So, Block all future loading
                        disabledViewModel.forceEndLoading = true;
                    }
                }
            });
        }
        //end

    }

    @Override
    public void onDispatched() {

    }

    private void replaceApprovedListData(List<Item> itemList) {
        approvedAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replacePendingListData(List<Item> itemList) {
        pendingAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceRejectedListData(List<Item> itemList) {
        rejectedAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replaceDisabledListData(List<Item> itemList) {
        disabledAdapter.get().replace(itemList);
        binding.get().executePendingBindings();
    }

    private void replacePaidItemHistoryList(List<ItemPaidHistory> itemPaidHistories) {
        this.itemPromoteHorizontalListAdapter.get().replace(itemPaidHistories);
        binding.get().executePendingBindings();
    }

    private void hideApprovedList(boolean isTrue) {
        if(isTrue) {
            binding.get().approvedListingRecyclerView.setVisibility(View.GONE);
            binding.get().approvedTitleTextView.setVisibility(View.GONE);
            binding.get().approvedSeeAllTextView.setVisibility(View.GONE);
        }else {
            binding.get().approvedListingRecyclerView.setVisibility(View.VISIBLE);
            binding.get().approvedTitleTextView.setVisibility(View.VISIBLE);
            binding.get().approvedSeeAllTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hideHistoryList(boolean isTrue) {
        if(isTrue) {
            binding.get().transactionList.setVisibility(View.GONE);
            binding.get().historyTextView.setVisibility(View.GONE);
            binding.get().seeAllTextView.setVisibility(View.GONE);
        }else {
            binding.get().transactionList.setVisibility(View.VISIBLE);
            binding.get().historyTextView.setVisibility(View.VISIBLE);
            binding.get().seeAllTextView.setVisibility(View.VISIBLE);
        }
    }

    private void hidePendingList(boolean isTrue) {
        if(isTrue) {
            binding.get().pendingTitleTextView.setVisibility(View.GONE);
            binding.get().pendingSeeAllTextView.setVisibility(View.GONE);
            binding.get().pendingRecyclerView.setVisibility(View.GONE);
        }else {
            binding.get().pendingTitleTextView.setVisibility(View.VISIBLE);
            binding.get().pendingSeeAllTextView.setVisibility(View.VISIBLE);
            binding.get().pendingRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void hideRejectedList(boolean isTrue) {
        if(isTrue) {
            binding.get().rejectedTitleTextView.setVisibility(View.GONE);
            binding.get().rejectedSeeAllTextView.setVisibility(View.GONE);
            binding.get().rejectedRecyclerView.setVisibility(View.GONE);
        }else {
            binding.get().rejectedTitleTextView.setVisibility(View.VISIBLE);
            binding.get().rejectedSeeAllTextView.setVisibility(View.VISIBLE);
            binding.get().rejectedRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void hideDisabledList(boolean isTrue) {
        if(isTrue) {
            binding.get().disabledTitleTextView.setVisibility(View.GONE);
            binding.get().disabledSeeAllTextView.setVisibility(View.GONE);
            binding.get().disabledRecyclerView.setVisibility(View.GONE);
        }else{
            binding.get().disabledTitleTextView.setVisibility(View.VISIBLE);
            binding.get().disabledSeeAllTextView.setVisibility(View.VISIBLE);
            binding.get().disabledRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void replaceProductHistoryData(List<ItemHistory> historyProductList) {
        adapter.get().replace(historyProductList);
        binding.get().executePendingBindings();

    }

    private void replaceUserData(User user) {

        binding.get().editTextView.setText(binding.get().editTextView.getText().toString());
        binding.get().userNotificatinTextView.setText(binding.get().userNotificatinTextView.getText().toString());
        binding.get().userHistoryTextView.setText(binding.get().userHistoryTextView.getText().toString());
        binding.get().favouriteTextView.setText(binding.get().favouriteTextView.getText().toString());
        binding.get().settingTextView.setText(binding.get().settingTextView.getText().toString());
        binding.get().historyTextView.setText(binding.get().historyTextView.getText().toString());
        binding.get().seeAllTextView.setText(binding.get().seeAllTextView.getText().toString());
        binding.get().joinedDateTitleTextView.setText(binding.get().joinedDateTitleTextView.getText().toString());
        binding.get().joinedDateTextView.setText(user.addedDate);
        binding.get().nameTextView.setText(user.userName);
        binding.get().phoneTextView.setText(user.userPhone);
        binding.get().statusTextView.setText(user.userAboutMe);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REQUEST_CODE__PROFILE_FRAGMENT
                && resultCode == Constants.RESULT_CODE__LOGOUT_ACTIVATED) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setToolbarText(((MainActivity) getActivity()).binding.toolbar, getString(R.string.profile__title));
                //navigationController.navigateToUserFBRegister((MainActivity) getActivity());
                navigationController.navigateToUserLogin((MainActivity) getActivity());
            }
        }
    }

}
