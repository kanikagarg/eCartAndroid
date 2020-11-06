package com.kmp.ecommerce.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kmp.ecommerce.ecart.Model.Products;
import com.kmp.ecommerce.ecart.Prevalent.Prevalent;
import com.kmp.ecommerce.ecart.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener{
    private DatabaseReference productsRef;
    private DatabaseReference addToCartProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Paper.init(this);
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        addToCartProductsRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);
        userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productsRef, Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                        productViewHolder.txtProductName.setText(products.getPname());
                        productViewHolder.txtProductDescription.setText(products.getDescription());
                        productViewHolder.txtProductPrice.setText(getString(R.string.price_equals_lbl,products.getPrice()));
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        //To update the qty in home screen as per the qty added in the cart
                        addToCartProductsRef.child(products.getPid())
                                .addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                     if (snapshot.exists()) {
                                         productViewHolder.qtyItemBtn.setNumber((String) snapshot.child("quantity").getValue());
                                     }
                                 }
                                 @Override
                                 public void onCancelled(@NonNull DatabaseError error) {
                                 }});

                        //To show detailed Product on click
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this, ProductDetailedActivity.class);
                                intent.putExtra("pid",products.getPid());
                                startActivity(intent);
                            }
                        });

                        //To add product to the cart on click of add to cart button for that product
                        productViewHolder.addToCartBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addProoductToCartList(products, productViewHolder.qtyItemBtn.getNumber());
                            }
                        });
                    }
                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext() ).inflate(R.layout.product_items_layout, parent, false);
//                       ProductViewHolder holder = new ProductViewHolder(view);
//                       return holder;
                        return new ProductViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.nav_cart){
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        }else if(id == R.id.nav_orders){

            Intent intent = new Intent(HomeActivity.this, UserOrdersActivity.class);
            startActivity(intent);

        }else if(id == R.id.nav_settings){
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);

        }else if(id == R.id.nav_logout){
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            Paper.book().destroy();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addProoductToCartList(final Products product, String qty) {
        String currentDate, currentTime;
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
                                               Toast.makeText(HomeActivity.this, R.string.product_add_to_cart_success_msg,Toast.LENGTH_SHORT).show();
                                                System.out.println( R.string.product_add_to_cart_success_msg);
                                            }
//                                            else{
//                                                Toast.makeText(HomeActivity.this, "Product could not be added to cart!",Toast.LENGTH_SHORT).show();
//                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener(){

                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HomeActivity.this, R.string.product_add_to_cart_err+e.toString(),Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
    }
}