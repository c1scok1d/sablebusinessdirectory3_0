package com.macinternetservices.sablebusinessdirectory.ui.city.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ItemRecentCitiesAdapterBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundViewHolder;
import com.macinternetservices.sablebusinessdirectory.utils.Objects;
import com.macinternetservices.sablebusinessdirectory.viewobject.City;

public class RecentCitiesAdapter extends DataBoundListAdapter<City, ItemRecentCitiesAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final PopularCitiesAdapter.NewsClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;

    public RecentCitiesAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                PopularCitiesAdapter.NewsClickCallback callback,
                                DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }


    @Override
    protected ItemRecentCitiesAdapterBinding createBinding(ViewGroup parent) {
        ItemRecentCitiesAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_recent_cities_adapter, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            City city = binding.getCity();
            if (city != null && callback != null) {
                callback.onClick(city);
            }
        });
        return binding;
    }

    @Override
    public void bindView(DataBoundViewHolder<ItemRecentCitiesAdapterBinding> holder, int position) {
        super.bindView(holder, position);

    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemRecentCitiesAdapterBinding binding, City city) {

        binding.setCity(city);

    }

    @Override
    protected boolean areItemsTheSame(City oldItem, City newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name);
    }

    @Override
    protected boolean areContentsTheSame(City oldItem, City newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name);
    }

    public interface NewsClickCallback {
        void onClick(City city);
    }

//    private void setAnimation(View viewToAnimate, int position) {
//        if (position > lastPosition) {
//            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_in_bottom);
//            viewToAnimate.startAnimation(animation);
//            lastPosition = position;
//        } else {
//            lastPosition = position;
//        }
//    }
}




