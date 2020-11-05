package com.kmp.ecommerce.ecart.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kmp.ecommerce.ecart.Interface.ItemClickListener;
import com.kmp.ecommerce.ecart.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName, txtProductPrice, txtProductQuantitity;
    public ImageView productImageView;
    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductPrice = itemView.findViewById(R.id.cart_prod_price);
        txtProductName = itemView.findViewById(R.id.cart_prod_name);
        txtProductQuantitity = itemView.findViewById(R.id.cart_prod_qty);
        productImageView = itemView.findViewById(R.id.cart_prod_item_image);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
