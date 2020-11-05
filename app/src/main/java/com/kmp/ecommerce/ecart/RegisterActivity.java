package com.kmp.ecommerce.ecart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private Button createUserButton;
    private EditText inputName, inputPhoneNumber, inputPassword;
    private ProgressDialog loadingBar;
    private String parentDbname = "Users";
    //private String parentDbname = "Admins";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createUserButton = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_username_input);
        inputPhoneNumber = findViewById(R.id.register_number_input);
        inputPassword = findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });


    }

    private void createAccount() {
        String name = inputName.getText().toString();
        String number = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write your Name...", Toast.LENGTH_LONG);
        } else if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Please write your Phone Number...", Toast.LENGTH_LONG);
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your Password...", Toast.LENGTH_LONG);
        } else {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait while we create your account...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validatePhoneNumber(name, number, password);

        }
    }

    private void validatePhoneNumber(final String name, final String number, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!(snapshot.child(parentDbname).child(number).exists())){
                    HashMap<String, Object>  userDataMap  = new HashMap<>();
                    userDataMap.put("name", name);
                    userDataMap.put("phone", number);
                    userDataMap.put("password", password);
                    RootRef.child(parentDbname).child(number).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this,"Congratulations, your account has been created",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,"Network error! Please try again after some time...",Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                } else {
                    Toast.makeText(RegisterActivity.this,"This "+number+" already exist.",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Please ",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //https://www.youtube.com/watch?v=xQm5zCVaIZg&list=PLxefhmF0pcPlqmH_VfWneUjfuqhreUz-O&index=6 6min
    }

}