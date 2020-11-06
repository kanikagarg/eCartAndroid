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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kmp.ecommerce.ecart.Model.Cart;
import com.kmp.ecommerce.ecart.Prevalent.Prevalent;
import com.kmp.ecommerce.ecart.ViewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount, txtMsgOrderSuccessful;
    private int totalPrice = 0;
    private ImageView cartItemImage;
    private DatabaseReference cartListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products");

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

        txtTotalAmount.setText(getString(R.string.total_amt_equals, String.valueOf(totalPrice)));
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef, Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {
                cartViewHolder.txtProductQuantitity.setText(getString(R.string.price_equals_lbl,cart.getQuantity()));
                cartViewHolder.txtProductPrice.setText(getString(R.string.price_equals_lbl,cart.getPrice()));
                cartViewHolder.txtProductName.setText(getString(R.string.product_name_equals_lbl,cart.getpName()));
                Picasso.get().load(cart.getImage()).into(cartViewHolder.productImageView);
                totalPrice += Integer.parseInt(cart.getQuantity()) * Integer.parseInt(cart.getPrice());
                txtTotalAmount.setText(getString(R.string.total_amt_equals, String.valueOf(totalPrice)));
                cartViewHolder.qtyBtn.setNumber(cart.getQuantity());
                cartViewHolder.qtyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateQtyinCart(cart.getPid(), cartViewHolder.qtyBtn.getNumber());
                    }

                    private void updateQtyinCart(final String pid,final String number) {

                        String currentDate, currentTime;
                        Calendar calendar = Calendar.getInstance();
                        currentDate =new SimpleDateFormat("ddMMYYYY").format(calendar.getTime());
                        currentTime =new SimpleDateFormat("hh:mm:ss az").format(calendar.getTime());
                        final DatabaseReference cartListRef =
                                FirebaseDatabase.getInstance().getReference().child("Cart List");
                        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                .child("Products").child(pid).child("quantity").setValue(number).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            cartListRef.child("Admin View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products")
                                                    .child(pid).child("quantity")
                                                    .setValue(number).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                }
                                            });
                                        }

                                    }
                                });
                            }
                });
                cartViewHolder.remove_product_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeProductFromCart(cart.getPid());
                    }
                });
                //To update the qty in Cart as per the qty added in the cart
                cartListRef.child(cart.getPid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    cartViewHolder.qtyBtn.setNumber((String) snapshot.child("quantity").getValue());
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return  new CartViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeProductFromCart(String pid) {
        System.out.println(cartListRef.child(pid).child("pName").toString());

        final String[] productName = {""};
        cartListRef.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    productName[0] = snapshot.child("pName").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        cartListRef.child(pid).removeValue().addOnCompleteListener(
                new  OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(CartActivity.this, "Product removed from cart successfully. "+productName[0], Toast.LENGTH_SHORT).show();
            }
        });

    }
}