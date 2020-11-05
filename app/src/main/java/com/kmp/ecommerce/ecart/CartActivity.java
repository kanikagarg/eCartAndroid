package com.kmp.ecommerce.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.kmp.ecommerce.ecart.Model.Cart;
import com.kmp.ecommerce.ecart.Prevalent.Prevalent;
import com.kmp.ecommerce.ecart.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount, txtMsgOrderSuccessful;
    private int totalPrice = 0;
    private ImageView cartItemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = (RecyclerView) findViewById(R.id.cart_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = (Button) findViewById(R.id.cart_next_btn);
        txtTotalAmount = (TextView) findViewById(R.id.cart_total_price);

        txtMsgOrderSuccessful = (TextView) findViewById(R.id.order_successful_msg);
        cartItemImage = (ImageView) findViewById(R.id.cart_prod_item_image);

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("TotalPrice", String.valueOf(totalPrice));
                startActivity(intent);
            }
        });

        txtTotalAmount.setText("Total Amount = Rs. " + String.valueOf(totalPrice));
    }


    @Override
    protected void onStart() {
        super.onStart();


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products"), Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
        adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull Cart cart) {
//                cartViewHolder.txtProductQuantitity.setText("Quantity : " + cart.getQuantity());
//                cartViewHolder.txtProductPrice.setText("Price : " + cart.getPrice() + " Rs.");
//                cartViewHolder.txtProductName.setText("Product : " + cart.getpName());
//                Picasso.get().load(cart.getImage()).into(cartViewHolder.productImageView);
//                totalPrice += Integer.valueOf(cart.getQuantity()) * Integer.valueOf(cart.getPrice());
//                txtTotalAmount.setText("Total Amount = Rs. " + String.valueOf(totalPrice));
                txtTotalAmount.setText( "alalalalala");
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                System.out.println("Error occured err : "+error);
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                CartViewHolder cartViewHolder = new CartViewHolder(view);
                return cartViewHolder;
            }
        };

                recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}