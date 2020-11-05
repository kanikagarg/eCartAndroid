package com.kmp.ecommerce.ecart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.kmp.ecommerce.ecart.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.logging.Logger;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity  {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, phoneEditText, addressEditText;
    private TextView closeBtn, saveBtn, profileImageUpdateBtn;

    private Uri imageURI;
    private String myURL = "";
    private StorageReference profilePicStorageRef;
    private StorageTask uploadTask;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profilePicStorageRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        //Retrieve values and mapping variables from the layout file
        profileImageView = (CircleImageView) findViewById(R.id.setings_profileimage);
        fullNameEditText = (EditText)findViewById(R.id.settings_full_name);
        phoneEditText = (EditText)findViewById(R.id.settings_phone_number);
        addressEditText= (EditText)findViewById(R.id.settings_address);
        closeBtn = (TextView)findViewById(R.id.settings_close);
        saveBtn = (TextView)findViewById(R.id.settings_update);
        profileImageUpdateBtn= (TextView)findViewById(R.id.settings_updateImage_btn);

        //Display User Info upon activity creation
        userInfoDisplay(profileImageView, fullNameEditText, phoneEditText, addressEditText);

        //Close button Functionality
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Update Details Button functionality
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( checker.equals("clicked")){
                    userInfoSaved();
                }else {
                    updateOnlyUserInfo();
                }    }
        });

        //Upload Profile Pic upon Updating profile image
        profileImageUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("INFO","Upload Profile Pic Button Clicked");
                checker = "clicked";

                Log.i("INFO","Checker set to Clicked");
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);

                Log.i("INFO","CropImage.activity callled");
            }
        });

    }

    // Saving User Info
    private void userInfoSaved() {

        Log.i("INFO","userInfoSaved function executing");
        if(TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Log.i("INFO","Mandatory name is Empty");
            Toast.makeText(this, "Name is Mandatory!",Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Log.i("INFO","Mandatory phone is Empty");
            Toast.makeText(this, "Phone is Mandatory!",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Log.i("INFO","Mandatory address is Empty");
            Toast.makeText(this, "Address is Mandatory!",Toast.LENGTH_SHORT).show();
        }else if(checker == "clicked"){
            Log.i("INFO","checker = clicked, upload Image Function Called");
            uploadImage();
        }
    }

    //code to upload Image
    private void uploadImage() {
        Log.i("INFO","Upload image function called");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait while we are updating your account information!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Log.i("INFO","imageURI is "+imageURI);
        if (imageURI != null) {
            final StorageReference fileRef = profilePicStorageRef.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageURI);

            Log.i("INFO","upload task is executing");
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {

                        Log.i("EXCeption","Upload task throws exception: "+task.getException().toString());
                        throw task.getException();
                    }
                    Log.i("INFO","upload task is successful");
                    return fileRef.getDownloadUrl();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "ERROR (addOnFailureListener)"+e.toString(),Toast.LENGTH_LONG).show();
                }
            })
            .addOnCompleteListener(new OnCompleteListener<Uri>() {
                 @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                                Uri downloadURI = task.getResult();
                                myURL = downloadURI.toString();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name", fullNameEditText.getText().toString());
                                userMap.put("phoneOrder", phoneEditText.getText().toString());
                                userMap.put("address", addressEditText.getText().toString());
                                userMap.put("image", myURL);
                                reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
                                progressDialog.dismiss();
                                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                Toast.makeText(SettingsActivity.this, "Account Information updated Successfully...", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Error Occurred..Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Logger.getLogger("Exception", "Exception while uploading the image ::" + e.toString());
                }
            });
        } else {
            Toast.makeText(SettingsActivity.this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }

    //execute when user already uploaded a profile image.
    private void updateOnlyUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("phoneOrder", phoneEditText.getText().toString());
        userMap.put("address", addressEditText.getText().toString());
        reference.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Account Information updated Successfully...", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//
//                profileImageView.setImageURI(resultUri);
//                Toast.makeText(SettingsActivity.this, "data "+data + "resultcode "+resultCode,Toast.LENGTH_LONG).show();
//
//                //From here you can load the image however you need to, I recommend using the Glide library
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }

        Toast.makeText(SettingsActivity.this,"old code started",Toast.LENGTH_LONG).show();
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Toast.makeText(SettingsActivity.this, "Cropping Image",Toast.LENGTH_SHORT).show();
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageURI = result.getUri();
            profileImageView.setImageURI(imageURI);
        } else {
            Log.i("INFO","K RequestCode ::"+requestCode);
            Toast.makeText(SettingsActivity.this, "Error! Try Again..."+requestCode+"data "+data + "resultcode "+resultCode,Toast.LENGTH_LONG).show();
//            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
//            finish();
        }
    }

//
//
//on button press or anywhere, this starts the image picking and cropping process
//CropImage.activity()
//        .setGuidelines(CropImageView.Guidelines.ON)
//  .start(this);

    //in your activity where you will get the result of your cropped image
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//
//                //From here you can load the image however you need to, I recommend using the Glide library
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//    }







//
//

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText phoneEditText, final EditText addressEditText) {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("Info","onDataChange called in userInfoDisplay");
                if(snapshot.exists()){
                    //if profile image exists then show image
                    if(snapshot.child("image").exists()){
                        //showing image in the activity
                        String image = snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                    };
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();
                        fullNameEditText.setText(name);
                        phoneEditText.setText(phone);
                        addressEditText.setText(address);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("  Database Error" ,"  Database Error"+error.toString());

            }
        });

    }
}