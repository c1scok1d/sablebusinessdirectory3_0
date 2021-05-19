package com.macinternetservices.sablebusinessdirectory.ui.city.selectedcity.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ItemItemCollectionRowAdapterBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.item.adapter.ItemListAdapter;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;
import com.macinternetservices.sablebusinessdirectory.viewobject.ItemCollectionHeader;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ItemCollectionRowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DataBoundListAdapter.DiffUtilDispatchedInterface {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private List<ItemCollectionHeader> itemCollectionHeaderList;
    public ItemClickCallback callback;

    public ItemCollectionRowAdapter(androidx.databinding.DataBindingComponent dataBindingComponent, ItemClickCallback callback) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
    }

    public void replaceCollectionHeader(List<ItemCollectionHeader> itemCollectionHeaders) {
        this.itemCollectionHeaderList = itemCollectionHeaders;
        this.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemItemCollectionRowAdapterBinding binding;

        private MyViewHolder(ItemItemCollectionRowAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ItemCollectionHeader itemCollectionHeader) {
            binding.setItemCollectionHeader(itemCollectionHeader);
            binding.executePendingBindings();
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemItemCollectionRowAdapterBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_item_collection_row_adapter, parent, false, dataBindingComponent);

        return new MyViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {

            ItemCollectionHeader itemCollectionHeader = itemCollectionHeaderList.get(position);

            ((MyViewHolder) holder).binding.titleTextView.setText(itemCollectionHeader.name);

            ((MyViewHolder) holder).binding.setItemCollectionHeader(itemCollectionHeader);

            ((MyViewHolder) holder).binding.viewAllTextView.setOnClickListener(view -> callback.onViewAllClick(itemCollectionHeaderList.get(position)));


            if(itemCollectionHeader.itemList != null)
            {
                if (itemCollectionHeader.itemList.size() > 0) {

//                ItemHorizontalListAdapter homeScreenAdapter = new ItemHorizontalListAdapter(dataBindingComponent, new ItemHorizontalListAdapter.ItemClickCallback() {
//                    @Override
//                    public void onClick(Product product) {
//                        callback.onClick(product);
//                    }
//
//                    @Override
//                    public void onFavLikeClick(Product product, LikeButton likeButton) {
//                        callback.onFavLikeClick(product, likeButton);
//                    }
//
//                    @Override
//                    public void onFavUnlikeClick(Product product, LikeButton likeButton) {
//                        callback.onFavUnlikeClick(product, likeButton);
//                    }
//                });

                    ItemListAdapter itemListAdapter = new ItemListAdapter(dataBindingComponent, item -> {

                        callback.onClick(item);
                    }, this);

                    ((MyViewHolder) holder).binding.collectionList.setAdapter(itemListAdapter);
                    itemListAdapter.replace(itemCollectionHeaderList.get(position).itemList);

                }
            }

        }

    }

    public interface ItemClickCallback {
        void onClick(Item item);

        void onViewAllClick(ItemCollectionHeader itemCollectionHeader);

    }

    @Override
    public int getItemCount() {
        if (itemCollectionHeaderList != null && itemCollectionHeaderList.size() > 0) {
            return itemCollectionHeaderList.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onDispatched() {

    }
}
