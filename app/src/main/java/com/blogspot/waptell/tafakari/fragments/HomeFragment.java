package com.blogspot.waptell.tafakari.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.waptell.tafakari.MainActivity;
import com.blogspot.waptell.tafakari.R;
import com.blogspot.waptell.tafakari.adapters.SubjectAdapter;
import com.blogspot.waptell.tafakari.models.Subject;
import com.blogspot.waptell.tafakari.utils.InternetConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    //recycle view
    private RecyclerView mRecyclerView;

    //adapter
    private SubjectAdapter adapter;

    //list
    private List<Subject> subjectList;

    //FirebaseFirestore
    private FirebaseFirestore db;
    private static final String TAG = "HomeFragment";


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //list
        subjectList = new ArrayList<>();

        //recyclerView
        mRecyclerView = view.findViewById(R.id.homeRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //adapter
        adapter = new SubjectAdapter(subjectList, getContext());
        mRecyclerView.setAdapter(adapter);

        //firebasefirestore
        db = FirebaseFirestore.getInstance();
        queryStatusDataFromDatabase();

        if (InternetConnection.checkConnection(getContext())) {
            doInBackground();
        }


        return view;
    }

    private void doInBackground() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.e(TAG, "run: DO IN BACKGROUND");
                downDataFromServer();
            }
        }).start();
    }

    private void queryStatusDataFromDatabase() {

        List<com.blogspot.waptell.tafakari.databases.Subject> subjects = MainActivity.myDatabase.myDao().getAllSubject();
        for (com.blogspot.waptell.tafakari.databases.Subject subject : subjects) {

            Subject subject1 = new Subject();
            subject1.setTitle(subject.getTitle());
            subject1.setEvent(subject.getEvent());
            subject1.setDescription(subject.getDescription());
            subjectList.add(subject1);
            adapter.notifyDataSetChanged();
        }

    }

    private void downDataFromServer() {

        db.collection("subjects").orderBy("create_at", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // Log.e(TAG, document.getId() + " => " + document.get("user_id"));
                        //fake data

                        com.blogspot.waptell.tafakari.databases.Subject subject = new com.blogspot.waptell.tafakari.databases.Subject();
                        subject.setId(Long.valueOf(document.get("create_at").toString()));
                        subject.setTitle(document.get("subject_title").toString());
                        subject.setEvent(document.get("event").toString());
                        subject.setDescription(document.get("subject_description").toString());
                        subject.setTime(Long.valueOf(document.get("create_at").toString()));
                        MainActivity.myDatabase.myDao().addSubject(subject);
                        Log.e(TAG, "onComplete: DATA ADDED SUCCESSFUL");

                        //query data again
                        subjectList.clear();
                        queryStatusDataFromDatabase();

                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

}
