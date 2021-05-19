package com.macinternetservices.sablebusinessdirectory.ui.item.upload;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ItemItemEntryImageBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundViewHolder;
import com.macinternetservices.sablebusinessdirectory.utils.Objects;
import com.macinternetservices.sablebusinessdirectory.viewobject.Image;

public class ItemEntryImageAdapter  extends DataBoundListAdapter<Image, ItemItemEntryImageBinding> {
    private final androidx.databinding.DataBindingComponent dataBindingComponent;

    private final ItemEntryImageAdapter.ItemImageViewClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface = null;
    private int lastPosition = -1;

    public ItemEntryImageAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                 ItemEntryImageAdapter.ItemImageViewClickCallback callback, DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface=diffUtilDispatchedInterface;
    }

    @Override
    protected ItemItemEntryImageBinding createBinding(ViewGroup parent) {
        ItemItemEntryImageBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_item_entry_image, parent, false,
                        dataBindingComponent);

        binding.getRoot().setOnClickListener(v -> {
            Image imageList = binding.getImage();
            if(imageList != null && callback != null){
                callback.onClick(imageList);
            }
        });

        binding.deleteImageView.setOnClickListener(v -> callback.onDeleteClick(binding.getImage()));

        return binding;
    }

    // For general animation
    @Override
    public void bindView(DataBoundViewHolder<ItemItemEntryImageBinding> holder, int position) {
        super.bindView(holder, position);



    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemItemEntryImageBinding binding, Image item) {
        binding.setImage(item);

    }


    @Override
    protected boolean areItemsTheSame(Image oldItem, Image newItem) {
        return Objects.equals(oldItem.imgId, newItem.imgId)
                && oldItem.imgPath.equals(newItem.imgPath)
                && oldItem.imgDesc.equals(newItem.imgDesc);

    }

    @Override
    protected boolean areContentsTheSame(Image oldItem, Image newItem) {
        return Objects.equals(oldItem.imgId, newItem.imgId)
                && oldItem.imgPath.equals(newItem.imgPath)
                && oldItem.imgDesc.equals(newItem.imgDesc);
    }

    public interface ItemImageViewClickCallback {
        void onClick(Image item);
        void onDeleteClick(Image deleteImage);
    }
}



