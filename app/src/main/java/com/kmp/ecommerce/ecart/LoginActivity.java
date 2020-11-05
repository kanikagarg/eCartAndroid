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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kmp.ecommerce.ecart.Model.Users;
import com.rey.material.widget.CheckBox;
import com.kmp.ecommerce.ecart.Prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText inputNumber, inputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private CheckBox rememberMeChkb;
    private TextView adminLink;
    private TextView notAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button) findViewById(R.id.login_btn);
        inputNumber = (EditText) findViewById(R.id.login_id_input);
        inputPassword = (EditText) findViewById(R.id.login_password_input);
        rememberMeChkb = (CheckBox) findViewById(R.id.remember_chkb);
        notAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        adminLink = (TextView) findViewById(R.id.admin_panel_link);
        Paper.init(this);


        loadingBar = new ProgressDialog(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Admin Login");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }

    private void loginUser() {

        String number = inputNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "Please enter your Phone Number...", Toast.LENGTH_LONG);
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your Password...", Toast.LENGTH_LONG);
        } else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we check your credentials...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            checkingAccessToAccount(number, password);
        }
    }

    private void checkingAccessToAccount(final String number, final String password) {
        Log.i("INFO","dbname"+parentDbName);
        if(rememberMeChkb.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey,number );
            Paper.book().write(Prevalent.userPasswordKey,password);
        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(number).exists()) {
                    Users usersData = snapshot.child(parentDbName).child(number).getValue(Users.class);

                    if (usersData.getPhone().equals(number)) {
                        if (usersData.getPassword().equals(password)) {
                            Log.i("INFO","username "+number+" \npassword "+password+"\n daabase"+parentDbName);
                            if(parentDbName.equals("Admins")){
                                Log.i("INFO","username "+number+" \npassword "+password+"\n daabase"+parentDbName);

                                Toast.makeText(LoginActivity.this, "Admin Logged in successfully...", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);

                            }else if(parentDbName.equals("Users")){
                                Log.i("INFO","username "+number+" \npassword "+password+"\n daabase"+parentDbName);

                                Toast.makeText(LoginActivity.this, "Logged in successfully...", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect!!! Please enter correct password.", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this,"Account with this number do not exist.",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this,"Please create an account to continue... ",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }}