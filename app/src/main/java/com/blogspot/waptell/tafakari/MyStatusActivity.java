package com.blogspot.waptell.tafakari;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.blogspot.waptell.tafakari.adapters.MyStatusAdapter;
import com.blogspot.waptell.tafakari.databases.User;
import com.blogspot.waptell.tafakari.databases.UserStatus;
import com.blogspot.waptell.tafakari.models.MyStatus;
import com.blogspot.waptell.tafakari.utils.GetTimeAgo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyStatusActivity extends AppCompatActivity {

    //recycle view
    private RecyclerView mRecyclerView;

    //adapter
    private  static   MyStatusAdapter adapter;

    //list
    public static List<MyStatus> statusList;

    //toolbar
    private Toolbar toolbar;

    public static String displayName;
    public static String imageProfile;



    private static final String TAG = "MyStatusActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_status);

        //Toolbar
        toolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //list
        statusList = new ArrayList<>();

        //recyclerView
        mRecyclerView = findViewById(R.id.myStatusRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //adapter
        adapter = new MyStatusAdapter(statusList, this);
        mRecyclerView.setAdapter(adapter);


        List<User> userInfo = MainActivity.myDatabase.myDao().getUserInfo();
        for (User user:userInfo){
             displayName = user.getUsername();
             imageProfile = user.getProfileImage();
            queryStatusDataFromDatabase(this, displayName, imageProfile);
        }

    }

    public static void queryStatusDataFromDatabase(Context context, String name, String image) {

        List<UserStatus> userStatuses = MainActivity.myDatabase.myDao().getMystatus();
        for (UserStatus userStatus : userStatuses) {

            MyStatus status = new MyStatus();
            status.setId(userStatus.getId());
            status.setUsername(name);
            status.setProfile(image);
            status.setDescription(userStatus.getStatusDiscription());
            Log.e(TAG, "queryStatusDataFromDatabase: "+userStatus.getStatusDiscription() );

            status.setTime(GetTimeAgo.getTimeAgo(Long.valueOf(userStatus.getId()), context));
            statusList.add(status);
            adapter.notifyDataSetChanged();
        }
    }


}
