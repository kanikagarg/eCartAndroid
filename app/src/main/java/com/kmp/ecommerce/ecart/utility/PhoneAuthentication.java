package com.kmp.ecommerce.ecart.utility;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.kmp.ecommerce.ecart.MainActivity;
import com.kmp.ecommerce.ecart.R;

public class PhoneAuthentication {

    Spinner spinnerCountryCode;
    EditText phoneNumber;
    private PhoneAuthentication(){

    }
//
//    public static Boolean SendCode(String phone_id){
//
//        phoneNumber = findViewById(phone_id);
//        findViewById(R.id.buttonContinue).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String mobile = editTextMobile.getText().toString().trim();
//                if(mobile.isEmpty() || mobile.length() < 10){
//                    editTextMobile.setError("Enter a valid mobile");
//                    editTextMobile.requestFocus();
//                    return;
//                }
//
//                Intent intent = new Intent(MainActivity.this, VerifyPhoneActivity.class);
//                intent.putExtra("mobile", mobile);
//                startActivity(intent);
//            }
//        });
//
//
//    }
//
//    public static Boolean CheckValidation(Spinner spinnerCountryCode, EditText phoneNumber){
//
//        return true;
//    }
//
//
//
}
