package com.blogspot.waptell.tafakari;

import android.Manifest;
import android.app.ProgressDialog;
import androidx.room.Room;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.waptell.tafakari.databases.MyDatabase;
import com.blogspot.waptell.tafakari.databases.User;
import com.blogspot.waptell.tafakari.utils.SharedPreferenceConfig;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final String TAG = "ProfileActivity";
    //SharedPreference
    SharedPreferenceConfig sharedPreferenceConfig;

    //widget
    private FloatingActionButton changeProfile;
    private EditText mDisplayName;
    private CircleImageView mProfileImage;

    public MyDatabase myDatabase;


    //storage
    private StorageReference mStorageRef;

    //image uri
    private Uri resultUri;

    //FirebaseAuth
    private FirebaseAuth mFirebaseAuth;

    //ProgressDialog
    private ProgressDialog mProgressDialog;
    private  String mAutherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        //sharedPreference
        sharedPreferenceConfig = new SharedPreferenceConfig(this);

        //storageReference
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        myDatabase = Room.databaseBuilder(getApplicationContext(),MyDatabase.class,"subjects").allowMainThreadQueries().build();


        //ProgressDialog
        mProgressDialog = new ProgressDialog(this);

          mAutherId = mFirebaseAuth.getCurrentUser().getUid();


        //widget
        changeProfile = findViewById(R.id.change_profile_picture);
        mDisplayName = findViewById(R.id.display_name);
        mProfileImage = findViewById(R.id.profile_img);

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePicture();
            }
        });
    }



    private void changeProfilePicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//                Log.e(TAG, "changeProfilePicture: ***SHOULD WE SHOW AN EXPLANATION?" );
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    Log.e(TAG, "changeProfilePicture: show Explanation to user");
//                    // Show an explanation to the user *asynchronously* -- don't block
//                    // this thread waiting for the user's response! After the user
//                    // sees the explanation, try again to request the permission.
//                    Log.e(TAG, "changeProfilePicture: *** SHOW AN EXPLANATION TO THE USER" );
//
//                    final Dialog dialog = new Dialog(this);
//                    dialog.setContentView(R.layout.permission_dialog_layout);
//                    Button button = dialog.findViewById(R.id.btn_dialog_ok);
//                    button.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            ActivityCompat.requestPermissions(ProfileActivity.this,
//                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//                        }
//                    });
//                    dialog.show();
//                } else {
//                    // No explanation needed; request the permission
//                    ActivityCompat.requestPermissions(this,
//                            new String[]{Manifest.permission.READ_CONTACTS},
//                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//
//                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                    // app-defined int constant. The callback method gets the
//                    // result of the request.
//                    Log.e(TAG, "changeProfilePicture: NO EXPLANATION NEEDED REQUEST PERMISSION" );
//                }
            } else {
                Log.e(TAG, "changeProfilePicture: PERMISSION HAS ALREADY BEEN GRANTED" );
                // Permission has already been granted
                Log.e(TAG, "changeProfilePicture: permission granted");
                startCropImage();
            }
        } else {
            startCropImage();
        }
    }

    private void startCropImage() {

        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Glide.with(ProfileActivity.this).load(resultUri).into(mProfileImage);
               // mProfileImage.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.e(TAG, "onRequestPermissionsResult: permission was granted, yay! Do the");
                    startCropImage();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.e(TAG, "onRequestPermissionsResult: permission denied, boo! Disable the");

//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
//                    alertDialog.setTitle("Idhini Inahitajika");
//                    alertDialog.setMessage("Tafadhali nenda kwenye setting za simu yako kisha katika ukurasa wa Ruhusa(Permissions),uruhusu " + getResources().getString(R.string.app_name) + " kufikia kamera(Camera)");
//                    alertDialog.setPositiveButton("NENDA KWENYE SETTING ZA ANDROID", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(ProfileActivity.this, "setting za android", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent();
//                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package", getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
//
//                        }
//                    });
//                    alertDialog.show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void createAccount(View view) {
        final String displayName = mDisplayName.getText().toString().trim();
        if (!TextUtils.isEmpty(displayName)) {

            //show progress dialog
            mProgressDialog.setMessage("Tafadhali subiri..");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            // Toast.makeText(this, "displayname" + displayName, Toast.LENGTH_SHORT).show();
            if (resultUri != null) {



                final StorageReference profileReference = mStorageRef.child("profile_image").child(mAutherId + "JPG");

                profileReference.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                                Log.e(TAG, "onSuccess: Image Uploaded successful");
                                while(!downloadUrl.isComplete());

                                registerUserInfo(displayName,downloadUrl.getResult().toString(), mAutherId);


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                mProgressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Profile picture haijatumwa, jaribu tena..", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Image not uploaded");
                            }
                        });
            } else {
               registerUserInfo(displayName,"default", mAutherId);

            }

        } else {
            Toast.makeText(this, "Tafadhali jaza Majina yako kamili.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUserInfo(final String displayName, final String imageProfile, final String userId){


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        final Map<String, Object> user = new HashMap<>();
        user.put("display_name", displayName);
        user.put("profile_image", imageProfile);
        user.put("create_at", FieldValue.serverTimestamp());

        // Add a new document with a generated ID
        db.collection("users")
                .document(userId).set(user)
             .addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {

                     //dismiss dialog
                     mProgressDialog.dismiss();
                     if (task.isSuccessful()){
                         Log.e(TAG, "onComplete: User Added" );
                         sharedPreferenceConfig.insertUserDisplayName(displayName);

                         //save userInformation
                         User user1 = new User();
                         user1.setId(userId);
                         user1.setUsername(displayName);
                         user1.setPhonenumber(mFirebaseAuth.getCurrentUser().getPhoneNumber());
                         user1.setProfileImage(imageProfile);
                         user1.setStatus("Natumia "+getResources().getString(R.string.app_name));
                         myDatabase.myDao().addUser(user1);
                         startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                         finish();
                     }
                 }
             })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //dismiss dialog
                        mProgressDialog.dismiss();
                        Log.e(TAG, "Error adding document", e);
                        Toast.makeText(ProfileActivity.this, "Imeshindikana Tafadhali jaribu tena.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
