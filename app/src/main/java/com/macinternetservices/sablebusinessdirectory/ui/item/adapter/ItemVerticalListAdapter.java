package com.macinternetservices.sablebusinessdirectory.ui.item.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.like.LikeButton;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.databinding.ItemItemVerticalListAdapterBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundListAdapter;
import com.macinternetservices.sablebusinessdirectory.ui.common.DataBoundViewHolder;
import com.macinternetservices.sablebusinessdirectory.utils.Constants;
import com.macinternetservices.sablebusinessdirectory.utils.Objects;
import com.macinternetservices.sablebusinessdirectory.viewobject.Item;

public class ItemVerticalListAdapter extends DataBoundListAdapter<Item, ItemItemVerticalListAdapterBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final NewsClickCallback callback;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;

    public ItemVerticalListAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                                   NewsClickCallback callback, DiffUtilDispatchedInterface diffUtilDispatchedInterface) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
    }

    @Override
    protected ItemItemVerticalListAdapterBinding createBinding(ViewGroup parent) {
        ItemItemVerticalListAdapterBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_item_vertical_list_adapter, parent, false,
                        dataBindingComponent);
        binding.getRoot().setOnClickListener(v -> {
            Item item = binding.getItem();
            if (item != null && callback != null) {
                callback.onClick(item);
            }
        });

//        binding.heartButton.setOnLikeListener(new OnLikeListener() {
//            @Override
//            public void liked(LikeButton likeButton) {
//
//                Product product = binding.getProduct();
//                if (product != null && callback != null) {
//                    callback.onFavLikeClick(product, binding.heartButton);
//                }
//
//            }
//
//            @Override
//            public void unLiked(LikeButton likeButton) {
//
//                Product product = binding.getProduct();
//                if (product != null && callback != null) {
//                    callback.onFavUnlikeClick(product, binding.heartButton);
//                }
//            }
//        });

        return binding;
    }

    // For general animation
    @Override
    public void bindView(DataBoundViewHolder<ItemItemVerticalListAdapterBinding> holder, int position) {
        super.bindView(holder, position);


        //setAnimation(holder.itemView, position);
    }

    @Override
    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @Override
    protected void bind(ItemItemVerticalListAdapterBinding binding, Item item) {
        binding.setItem(item);

        binding.ratingValueTextView.setText(String.valueOf(item.ratingDetails.totalRatingValue));
        binding.reviewValueTextView.setText(String.format("( %s %s )", String.valueOf(item.ratingDetails.totalRatingCount), binding.getRoot().getResources().getString(R.string.dashboard_review)));
//        binding.ratingBar.setRating(item.ratingDetails.totalRatingValue);
//
//        binding.ratingBarTextView.setText((binding.getRoot().getResources().getString(R.string.discount__rating5, String.valueOf(item.ratingDetails.totalRatingValue), String.valueOf(item.ratingDetails.totalRatingCount))));
//
////        binding.priceTextView.setText(String.valueOf(item.unitPrice));
////        String originalPriceStr = item.currencySymbol + Constants.SPACE_STRING + String.valueOf(item.originalPrice);
////        binding.originalPriceTextView.setText(originalPriceStr);
//
////        if (item.isDiscount.equals(Constants.ZERO)) {
////            binding.originalPriceTextView.setVisibility(View.GONE);
////            binding.discountTextView.setVisibility(View.GONE);
////        } else {
////            binding.originalPriceTextView.setPaintFlags(binding.originalPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
////            binding.originalPriceTextView.setVisibility(View.VISIBLE);
////            binding.discountTextView.setVisibility(View.VISIBLE);
////            int discountValue = (int) item.discountPercent;
////            String discountValueStr = "-" + discountValue + "%";
////            binding.discountTextView.setText(discountValueStr);
////        }
//
//        if (item.isFeatured.equals(Constants.ZERO)) {
//            binding.featuredIconImageView.setVisibility(View.GONE);
//        } else {
//            binding.featuredIconImageView.setVisibility(View.VISIBLE);
//        }
//
////        if (product.isFavourited.equals(Constants.RATING_ONE)) {
////            binding.heartButton.setLiked(true);
////        } else {
////            binding.heartButton.setLiked(false);
////        }
        if (item.paidStatus.equals(Constants.ADSPROGRESS)){
            binding.sponsorCardView.setVisibility(View.VISIBLE);
            binding.addedDateStrTextView.setText(R.string.paid__sponsored);
        } else{
            binding.sponsorCardView.setVisibility(View.GONE);
        }
    }

    @Override
    protected boolean areItemsTheSame(Item oldItem, Item newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name)
                && oldItem.isFavourited.equals(newItem.isFavourited)
                && oldItem.likeCount.equals(newItem.likeCount)
                && oldItem.ratingDetails.totalRatingValue == newItem.ratingDetails.totalRatingValue
                && oldItem.ratingDetails.totalRatingCount == newItem.ratingDetails.totalRatingCount;
    }

    @Override
    protected boolean areContentsTheSame(Item oldItem, Item newItem) {
        return Objects.equals(oldItem.id, newItem.id)
                && oldItem.name.equals(newItem.name)
                && oldItem.isFavourited.equals(newItem.isFavourited)
                && oldItem.likeCount.equals(newItem.likeCount)
                && oldItem.ratingDetails.totalRatingValue == newItem.ratingDetails.totalRatingValue
                && oldItem.ratingDetails.totalRatingCount == newItem.ratingDetails.totalRatingCount;
    }

    public interface NewsClickCallback {
        void onClick(Item item);

        void onFavLikeClick(Item item, LikeButton likeButton);

        void onFavUnlikeClick(Item item, LikeButton likeButton);
    }


}
