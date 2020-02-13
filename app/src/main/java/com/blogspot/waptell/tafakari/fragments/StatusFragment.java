package com.blogspot.waptell.tafakari.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.waptell.tafakari.MainActivity;
import com.blogspot.waptell.tafakari.MyStatusActivity;
import com.blogspot.waptell.tafakari.R;
import com.blogspot.waptell.tafakari.adapters.StatusAdapter;
import com.blogspot.waptell.tafakari.databases.StatusEntity;
import com.blogspot.waptell.tafakari.databases.User;
import com.blogspot.waptell.tafakari.databases.UserStatus;
import com.blogspot.waptell.tafakari.models.Status;
import com.blogspot.waptell.tafakari.utils.GetTimeAgo;
import com.blogspot.waptell.tafakari.utils.InternetConnection;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    //recycle view
    private RecyclerView mRecyclerView;

    //adapter
    private StatusAdapter adapter;

    //list
    private List<Status> statusList;

    //FloatingActionButton
    private FloatingActionButton mCreateStatus;

    private TextView profName;
    private CircleImageView profImage;


    public StatusFragment() {
        // Required empty public constructor
    }

    //FirebaseAuth
    private FirebaseAuth mAuth;

    //Firestore
    private FirebaseFirestore db;

    //layout
    private LinearLayout mParentLayout;

    private static final String TAG = "StatusFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        //list
        statusList = new ArrayList<>();

        //FloatingActionButton
        mCreateStatus = view.findViewById(R.id.createStatus);

        //recyclerView
        mRecyclerView = view.findViewById(R.id.statusRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //adapter
        adapter = new StatusAdapter(statusList, getContext());
        mRecyclerView.setAdapter(adapter);

        //firebaseAuth
        mAuth = FirebaseAuth.getInstance();
        //firebasefirestore
        db = FirebaseFirestore.getInstance();

        //linearlayout
        mParentLayout = view.findViewById(R.id.linearLayout);

        if (InternetConnection.checkConnection(getContext())){
            doInBackground();
        }

        mCreateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCreateStatus();
            }
        });

        mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //check if have status
                List<UserStatus> mystatus = MainActivity.myDatabase.myDao().getMystatus();
                if (mystatus.isEmpty()){
                    mCreateStatus();
                }else {
                    startActivity(new Intent(getContext(), MyStatusActivity.class));
                }
            }
        });

        profImage = view.findViewById(R.id.pro_image);
        profName = view.findViewById(R.id.prof_name);

        List<User> userInfo = MainActivity.myDatabase.myDao().getUserInfo();
        for (User user: userInfo){
            profName.setText(user.getUsername());
            if (user.getProfileImage().equals("default")) {

                Glide.with(this).load(R.mipmap.profile_image_default).apply(new RequestOptions().placeholder(R.mipmap.profile_image_default)).into(profImage);

            } else {
                String profileImage = user.getProfileImage();
                Glide.with(this).load(profileImage).apply(new RequestOptions().placeholder(R.mipmap.profile_image_default)).into(profImage);
            }
        }


        getDataFromDatabase();

        return view;
    }

    private void getDataFromDatabase() {
        List<StatusEntity> allStatus = MainActivity.myDatabase.myDao().getAllStatus();
        if (!allStatus.isEmpty()) {
            for (StatusEntity statusEntity : allStatus) {

                Status status = new Status();

                Log.e(TAG, "getDataFromDatabase: " + statusEntity.getUsername());
                status.setUsername(statusEntity.getUsername());
                status.setDescription(statusEntity.getDescription());
                status.setTime(GetTimeAgo.getTimeAgo(Long.valueOf(statusEntity.getTime()), getContext()));
                status.setImage(statusEntity.getProfileImage());
                statusList.add(status);
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(getContext(), "Hamna ujumbe wa kuonesha", Toast.LENGTH_SHORT).show();
        }
    }

    private void doInBackground() {
        Log.e(TAG, "doInBackground: DOWNLOADING STATUS");
        new Thread(new Runnable() {
            @Override
            public void run() {
                queryStatusDataFromDatabase();
                Log.e(TAG, "run: END DOWNLOADING ");
            }
        }).start();
    }

    private void queryStatusDataFromDatabase() {

        db.collection("status").orderBy("create_at", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // Log.e(TAG, document.getId() + " => " + document.get("user_id"));

                        final String description = document.get("post_content").toString();
                        final String dId = document.get("create_at").toString();
                        final String time = document.get("create_at").toString();
                        final String userId = document.get("user_id").toString();

                        //get the specific user
                        Task<DocumentSnapshot> documentSnapshotTask = db.collection("users").document(document.get("user_id").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                // Log.e(TAG, "onComplete: " + task.getResult().getData());

                                if (!userId.equals(mAuth.getCurrentUser().getUid())) {
                                    Map<String, Object> data = task.getResult().getData();
                                    Log.e(TAG, "onComplete: " + time);
                                    StatusEntity statusEntity = new StatusEntity();
                                    statusEntity.setDescription(description);
                                    statusEntity.setId(Long.valueOf(dId));
                                    statusEntity.setTime(Long.valueOf(time));
                                    statusEntity.setProfileImage(data.get("profile_image").toString());
                                    statusEntity.setUsername(data.get("display_name").toString());

                                    MainActivity.myDatabase.myDao().saveAllStatus(statusEntity);
                                    Log.e(TAG, "onComplete: STATUS SAVED SUCCESSFULL");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: FAILED READ DOCUMENT");
                            }
                        });


                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void mCreateStatus() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_layout);

        final EditText edtPost = dialog.findViewById(R.id.title);
        Button buttonPost = dialog.findViewById(R.id.btn_dialog_ok);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postStatus = edtPost.getText().toString().trim();
                if (!TextUtils.isEmpty(postStatus)) {
                    //start adding data to the database
                    //lets query user who post
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    long time = System.currentTimeMillis();
                    saveDataToDatabase(userId, postStatus, time);


                }
                Toast.makeText(getContext(), "posted ", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void postDataToDatabase(final String userId, final String postStatus, final Long time) {


        final Map<String, String> docData = new HashMap<>();
        docData.put("user_id", userId);
        docData.put("post_content", postStatus);
        docData.put("create_at", String.valueOf(time));

        db.collection("status").add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Ujumbe umetumwa", Toast.LENGTH_SHORT).show();
                    UserStatus userStatus = new UserStatus();
                    userStatus.setId(time);
                    userStatus.setUserId(userId);
                    userStatus.setStatusDiscription(postStatus);
                    userStatus.setStatus(true);
                    MainActivity.myDatabase.myDao().updateStatus(userStatus);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Failed to add status");
                    }
                });
    }

    private void saveDataToDatabase(String userId, String postStatus, Long time) {

        UserStatus userStatus = new UserStatus();
        userStatus.setId(time);
        userStatus.setUserId(userId);
        userStatus.setStatusDiscription(postStatus);
        userStatus.setStatus(false);
        MainActivity.myDatabase.myDao().addStatus(userStatus);

        //startSending to the server
        postDataToDatabase(userId, postStatus, time);

    }
}
