package com.kmp.ecommerce.ecart.utility;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kmp.ecommerce.ecart.Model.Cart;
import com.kmp.ecommerce.ecart.Model.Products;
import com.kmp.ecommerce.ecart.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductUtility {
    public static Boolean ADD_TO_CART_STATUS = false;
//    public static String qtyInCart;
    public static String qtyInCart = "1";

    //Get product Details
    private static Products getProductDetails(String productId) {

        final Products[] product = {null};
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    product[0] = snapshot.getValue(Products.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return product[0];
    }

    //ProductDetailedActivity
    public static void addProoductToCartList(final Products product, int qty) {
        String currentDate, currentTime;
        ADD_TO_CART_STATUS = false;

        System.out.println("Add to cart status @function call ::"+ADD_TO_CART_STATUS+"Before handler");
        Calendar calendar = Calendar.getInstance();
        currentDate =new SimpleDateFormat("ddMMYYYY").format(calendar.getTime());
        currentTime =new SimpleDateFormat("hh:mm:ss az").format(calendar.getTime());
        final DatabaseReference cartListRef =
                FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",product.getPid());
        cartMap.put("pName",product.getPname());
        cartMap.put("price",product.getPrice());
        cartMap.put("description",product.getDescription());
        cartMap.put("date",currentDate);
        cartMap.put("time",currentTime);
        cartMap.put("image",product.getImage());
        cartMap.put("quantity",qty);
        cartMap.put("discount",product.getDiscount());

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(product.getPid())
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cartListRef.child("Admin View")
                                    .child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products")
                                    .child(product.getPid())
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                ADD_TO_CART_STATUS = true;
                                                System.out.println("\n ::: oncomplete :::"+ADD_TO_CART_STATUS);

                                                System.out.println("Add to cart status on complete ::"+ADD_TO_CART_STATUS+"Before handler");
                                            }else{
                                                ADD_TO_CART_STATUS = false;
                                            }
                                        }
                                    });
                        }
                        System.out.println("Add to cart statu in function after oncomplete code ::"+ADD_TO_CART_STATUS+"Before handler");
                    }
                });
    }

    //always use it with 5 sec gap
    public static void getAddtoCartProductQty(String pid) {
         final String qtyInCart = "1";

        DatabaseReference addToCartRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getPhone());
        addToCartRef.child("Products").child(pid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    System.out.println("childern ::"+snapshot.getChildrenCount());

                    System.out.println("Child quantity is "+snapshot.child("quantity").getValue());
                    ProductUtility.qtyInCart = snapshot.child("quantity").getValue().toString();
//                    for (DataSnapshot child : snapshot.getChildren()) {
//                        if(child.getKey().equals("quantity")){
//                    System.out.println("Child is "+child.getValue());
//                            qtyInCart = child.getValue();
//                    }}
//                    System.out.println(cartProduct.getClass().getName());

//                    qtyInCart = snapshot.getValue(Products.class).getQuantity().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

}
