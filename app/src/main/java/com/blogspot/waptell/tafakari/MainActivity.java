package com.blogspot.waptell.tafakari;

import android.app.Dialog;
import androidx.room.Room;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blogspot.waptell.tafakari.databases.MyDatabase;
import com.blogspot.waptell.tafakari.fragments.HomeFragment;
import com.blogspot.waptell.tafakari.fragments.ProfileFragment;
import com.blogspot.waptell.tafakari.fragments.StatusFragment;
import com.blogspot.waptell.tafakari.utils.SharedPreferenceConfig;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //RoomDatabase
    public static MyDatabase myDatabase;

    //Toolbar
    private Toolbar mToolbar;

    //adview
    private AdView mAdView;


    //BottomNavigationView
    private BottomNavigationView mBottomNavigationView;

    //shared preference
    private SharedPreferenceConfig sharedPreferenceConfig;

    //Firestore
    FirebaseFirestore db;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shared preference
        sharedPreferenceConfig = new SharedPreferenceConfig(this);

        myDatabase = Room.databaseBuilder(getApplicationContext(),MyDatabase.class,"subjects").allowMainThreadQueries().build();

        if (sharedPreferenceConfig.verfiedUserPhonenumber().equals("")){
            startActivity(new Intent(this, UserAuthenticationActivity.class));
            finish();

        }else if(sharedPreferenceConfig.getDisplayName().equals("")){
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }

        setContentView(R.layout.activity_main);

        //Toolbar
        mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        //FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        //BottomNavigationView
        mBottomNavigationView = findViewById(R.id.mBottomNavigationView);

        init(savedInstanceState);

        // AdMob app ID: Initialization
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));

        //load banner ads
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void init(Bundle savedInstanceState) {


        //loading Home fragment first
        final HomeFragment homeFragment = new HomeFragment();
        if (findViewById(R.id.frame_container) != null) {
            replaceFragment(homeFragment);
//            if (savedInstanceState != null)
//                return;
//            getSupportFragmentManager().beginTransaction().add(R.id.frame_container, homeFragment, null).commit();
        }

        // getFragmentManager().beginTransaction().add(R.id.frame_container,homeFragment,null).commit();
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        // replaceFragment(homeFragment);
                      //  Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.action_status:
                       // Toast.makeText(MainActivity.this, "Status", Toast.LENGTH_SHORT).show();
                        replaceFragment(new StatusFragment());
                        return true;
                    case R.id.action_profile:
                      //  Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                        replaceFragment(new ProfileFragment());
                        return true;
                    default:
                        return false;

                }
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        if (findViewById(R.id.frame_container) != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
               // Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                return true;

            case R.id.action_post_subject:
               // Toast.makeText(this, "start posting", Toast.LENGTH_SHORT).show();
                postingSubject();
                return true;

            case R.id.action_about:
                // Toast.makeText(this, "start posting", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }

    }

    private void postingSubject() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.post_subject_dialog);
        final EditText title = dialog.findViewById(R.id.title);
        final EditText event = dialog.findViewById(R.id.event);
        final EditText description = dialog.findViewById(R.id.description);
        Button post = dialog.findViewById(R.id.post);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(title.getText().toString().trim()) && !TextUtils.isEmpty(event.getText().toString().trim()) && !TextUtils.isEmpty(description.getText().toString().trim())){

                    addDataToDatabase(title.getText().toString().trim(),event.getText().toString().trim(),description.getText().toString().trim());
                    dialog.dismiss();
                }else{
                    Toast.makeText(MainActivity.this, "required filled is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void addDataToDatabase(String sub_title, String sub_event, String sub_desc) {
        long time= System.currentTimeMillis();
        Map<String, Object> docData = new HashMap<>();
        docData.put("subject_title", sub_title);
        docData.put("event", sub_event);
        docData.put("subject_description", sub_desc);
        docData.put("create_at", time);

        db.collection("subjects").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "posted successfull", Toast.LENGTH_LONG).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Failed to add subject");
                    }
                });
    }
}
