package com.kmp.ecommerce.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kmp.ecommerce.ecart.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText shipmentName,shipmentPhone,shipmentAddress;
    private Button confirmBtn;
    private String totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        shipmentName =(EditText)findViewById(R.id.shipment_name);
        shipmentAddress =(EditText)findViewById(R.id.shipment_address);
        shipmentPhone = (EditText)findViewById(R.id.shipment_phone_number);
        confirmBtn =(Button)findViewById(R.id.confirm_button);
        totalPrice = getIntent().getStringExtra("TotalPrice");
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateShipmentDetails()){
                    confirmOrder();
                }
            }
        });
    }

    private void confirmOrder() {

       final String currentDate, currentTime;
        Calendar calendar = Calendar.getInstance();
        currentDate =new SimpleDateFormat("DDMMYYYY").format(calendar.getTime());
        currentTime =new SimpleDateFormat("HH:MM:SS a").format(calendar.getTime());
        DatabaseReference orderDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String,Object> orderMap =new HashMap<>();
        orderMap.put("totalAmount",totalPrice);
        orderMap.put("name",shipmentName.getText().toString());
        orderMap.put("phone",shipmentPhone.getText().toString());
        orderMap.put("address",shipmentAddress.getText().toString());
        orderMap.put("date",currentDate);
        orderMap.put("time",currentTime);
        orderMap.put("state","Not Shipped");
        orderDatabaseReference.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ConfirmFinalOrderActivity.this, "Your Order has been placed successfully! Voila! ",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }

    private boolean validateShipmentDetails() {
        if(TextUtils.isEmpty(shipmentName.getText().toString())){
            Toast.makeText(this, "Please provide Name for Shipment!",Toast.LENGTH_LONG).show();
            return  false;
        } else if(TextUtils.isEmpty(shipmentPhone.getText().toString())){
            Toast.makeText(this, "Please provide Shipment Phone number, it is Mandatory!",Toast.LENGTH_LONG).show();
            return  false;
        }else if(TextUtils.isEmpty(shipmentAddress.getText().toString())){
            Toast.makeText(this, "Please provide your shipment Address!",Toast.LENGTH_LONG).show();
            return  false;
        } else{
            return true;
        }
    }
}