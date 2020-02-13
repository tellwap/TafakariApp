package com.blogspot.waptell.tafakari.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.waptell.tafakari.MainActivity;
import com.blogspot.waptell.tafakari.R;
import com.blogspot.waptell.tafakari.databases.User;
import com.blogspot.waptell.tafakari.utils.InternetConnection;
import com.blogspot.waptell.tafakari.utils.SharedPreferenceConfig;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    //widget
    private CircleImageView profileImage;
    private TextView profileUsername;
    private TextView profileStatus;
    private TextView profilePhonenumber;
    private FloatingActionButton changeProfileImage;
    private ProgressBar progressBar;
    private ProgressBar updateUsernameProgressBar;
    private EditText editTextName;
    private ImageView updateUserBtn;
    private ImageView updateDisplayName;
    private String mStatus;

    private FirebaseAuth mAuther;

    //storage
    private StorageReference mStorageRef;

    //sharedPref
    private SharedPreferenceConfig sharedPreferenceConfig;
    private static final String TAG = "ProfileFragment";

    //user Information
    private String mDisplayName;
    private String mPhoneNumber;
    private String mID;
    private String mProfileImage;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //cast widget
        profileImage = view.findViewById(R.id.profile_image);
        profileUsername = view.findViewById(R.id.profile_username);
        editTextName = view.findViewById(R.id.edt_profile_username);
        profileStatus = view.findViewById(R.id.profile_status);
        profilePhonenumber = view.findViewById(R.id.profile_number);
        changeProfileImage = view.findViewById(R.id.change_profile);
        updateUserBtn = view.findViewById(R.id.updateUserBtn);
        updateDisplayName = view.findViewById(R.id.updateDisplayName);

        //sharedpref
        sharedPreferenceConfig = new SharedPreferenceConfig(getContext());

        mAuther = FirebaseAuth.getInstance();

        //storageReference
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //progresBar
        progressBar = view.findViewById(R.id.progressBar);
        updateUsernameProgressBar = view.findViewById(R.id.updateUsernameProgressBar);
        progressBar.setVisibility(View.GONE);
        updateUsernameProgressBar.setVisibility(View.GONE);

        //print userInformation
        List<User> userInfo = MainActivity.myDatabase.myDao().getUserInfo();
        for (User user : userInfo){

            mID = user.getId();
            mPhoneNumber = user.getPhonenumber();
            mProfileImage = user.getProfileImage();
            mDisplayName = user.getUsername();
            mStatus = user.getStatus();

            Log.e(TAG, "onCreateView: ****USERNAME"+user.getUsername() );
            Log.e(TAG, "onCreateView: ****PHONENUMBER"+user.getPhonenumber() );
            Log.e(TAG, "onCreateView: ****PROFILEIMAGE"+user.getProfileImage() );
            Log.e(TAG, "onCreateView: ****ID"+user.getId() );
        }

        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (InternetConnection.checkConnection(getContext())) {
                    updateUserBtn.setVisibility(View.GONE);
                    editTextName.setVisibility(View.VISIBLE);
                    profileUsername.setVisibility(View.GONE);
                    updateDisplayName.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getContext(), "Tafadhali washa data ili kubadilisha jina", Toast.LENGTH_SHORT).show();
                }

            }
        });

        updateDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUsernameProgressBar.setVisibility(View.VISIBLE);
                String updateName = editTextName.getText().toString();
                updateDisplayName.setVisibility(View.GONE);
                updateUserBtn.setVisibility(View.GONE);
                editTextName.setVisibility(View.GONE);
                profileUsername.setVisibility(View.VISIBLE);
                profileUsername.setText(updateName);

                if (InternetConnection.checkConnection(getContext())) {
                    updateUsername(updateName);
                }else{
                    Toast.makeText(getContext(), "Tafadhali washa data ili kubadilisha jina", Toast.LENGTH_SHORT).show();
                }
            }
        });

        init();



        return view;
    }

    private void updateUsername(final String displayName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuther.getCurrentUser().getUid()).update("display_name",displayName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                User user1 = new User();
                user1.setId(mAuther.getCurrentUser().getUid());
                user1.setUsername(displayName);
                user1.setProfileImage(mProfileImage);
                user1.setPhonenumber(mPhoneNumber);
                user1.setStatus(mStatus);
                MainActivity.myDatabase.myDao().updateUserInfo(user1);

                updateUsernameProgressBar.setVisibility(View.GONE);
                updateUserBtn.setVisibility(View.VISIBLE);

                Toast.makeText(getContext(), "Umefanikiwa kubadili jina", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {

        List<User> userInfo = MainActivity.myDatabase.myDao().getUserInfo();
        for (User user : userInfo) {
            String username = user.getPhonenumber();
            //Log.e(TAG, "init: " + username);

            editTextName.setText(user.getUsername());
            if (user.getProfileImage().equals("default")) {

                Glide.with(this).load(R.mipmap.profile_image_default).apply(new RequestOptions().placeholder(R.mipmap.profile_image_default)).into(profileImage);

            } else {
                Glide.with(this).load(user.getProfileImage()).apply(new RequestOptions().placeholder(R.mipmap.profile_image_default)).into(profileImage);
            }
            profileUsername.setText(user.getUsername());
            profileStatus.setText(user.getStatus());
            profilePhonenumber.setText(user.getPhonenumber());

            changeProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (InternetConnection.checkConnection(getContext())) {
                        changeProfilePicture();
                    }else{
                        Toast.makeText(getContext(), "Tafadhali washa data ili kubadilisha profile picture..", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void changeProfilePicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            } else {
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
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(this).load(resultUri).into(profileImage);
                //mProfileImage.setImageURI(resultUri);
                uploadImageToServer(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadImageToServer(Uri uri) {

        progressBar.setVisibility(View.VISIBLE);
        final StorageReference profileReference = mStorageRef.child("profile_image").child(mAuther.getCurrentUser().getUid() + "JPG");

        profileReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                        Log.e(TAG, "onSuccess: Image Uploaded successful");
                        while (!downloadUrl.isComplete()) ;

                        updateUserImage(downloadUrl.getResult().toString());




                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Profile picture haijatumwa, jaribu tena..", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Image not uploaded");
                    }
                });
    }

    private void updateUserImage(final String downUri) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(mAuther.getCurrentUser().getUid()).update("profile_image",downUri).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                User user = new User();
                user.setId(mAuther.getCurrentUser().getUid());
                user.setProfileImage(downUri);
                user.setPhonenumber(mPhoneNumber);
                user.setUsername(mDisplayName);
                user.setStatus(mStatus);
                MainActivity.myDatabase.myDao().updateUserInfo(user);
                Log.e(TAG, "onSuccess: PROFILE UPDATED SUCCESSFULL");
                progressBar.setVisibility(View.GONE);
            }
        });
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
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
