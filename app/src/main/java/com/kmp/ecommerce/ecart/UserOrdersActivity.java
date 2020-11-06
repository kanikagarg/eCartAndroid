package com.kmp.ecommerce.ecart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserOrdersActivity extends AppCompatActivity {
    private DatabaseReference orderssRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        orderssRef = FirebaseDatabase.getInstance().getReference().child("Orders");

    }
}