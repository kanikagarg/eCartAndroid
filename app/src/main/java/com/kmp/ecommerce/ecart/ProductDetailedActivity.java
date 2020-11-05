package com.kmp.ecommerce.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kmp.ecommerce.ecart.Model.Products;
import com.kmp.ecommerce.ecart.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailedActivity extends AppCompatActivity {

    private  String productId = "";
    ImageView productImageView ;
    TextView productName, prodDetailedDescription, prodDetailedPrice;//,itemQtyAddBtn, itemQtyMinusBtn;
//    EditText itemQtyNum;
    ElegantNumberButton quantityBtn;
    FloatingActionButton addToCartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detailed);

        productId = getIntent().getStringExtra("pid");
        productImageView = (ImageView)findViewById(R.id.product_detailed_image);
        productName = (TextView) findViewById(R.id.product_detailed_name);
        prodDetailedDescription = (TextView) findViewById(R.id.product_detailed_description);
        prodDetailedPrice = (TextView) findViewById(R.id.product_detailed_price);
        quantityBtn = (ElegantNumberButton)findViewById(R.id.quantity_btn);
        addToCartBtn = (FloatingActionButton)findViewById(R.id.add_to_cart_btn);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProoductToCartList();
            }
        });

        getProductDetails(productId);

    }

    private void addProoductToCartList() {
        String currentDate, currentTime;
        Calendar calendar = Calendar.getInstance();
        currentDate =new SimpleDateFormat("DDMMYYYY").format(calendar.getTime());
        currentTime =new SimpleDateFormat("HH:MM:SS a").format(calendar.getTime());
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object> cartMap = new HashMap<>();
        cartMap.put("pid",productId);
        cartMap.put("pName",productName.getText().toString());
        cartMap.put("price",prodDetailedPrice.getText().toString());
        cartMap.put("desciption",prodDetailedDescription.getText().toString());
        cartMap.put("date",currentDate);
        cartMap.put("time",currentTime);
        cartMap.put("image",productImageView.toString());
        cartMap.put("quantity",quantityBtn.getNumber());
        cartMap.put("discount","");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ProductDetailedActivity.this, "Added to Cart Successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ProductDetailedActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getProductDetails(String productId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products product = snapshot.getValue(Products.class);
                    productName.setText(product.getPname());
                    prodDetailedDescription.setText(product.getDescription());
                    prodDetailedPrice.setText(product.getPrice());
                    Picasso.get().load(product.getImage()).into(productImageView); //picasso lib loading image into image view from uri in product.getImage() value
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}