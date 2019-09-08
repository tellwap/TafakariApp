package com.blogspot.waptell.tafakari.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.waptell.tafakari.MainActivity;
import com.blogspot.waptell.tafakari.MyStatusActivity;
import com.blogspot.waptell.tafakari.R;
import com.blogspot.waptell.tafakari.databases.UserStatus;
import com.blogspot.waptell.tafakari.models.MyStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyStatusAdapter extends RecyclerView.Adapter<MyStatusAdapter.ViewHolder> {

    private List<MyStatus> statusList;
    private Context context;
    private static final String TAG = "StatusAdapter";
    private FirebaseFirestore db;

    public MyStatusAdapter(List<MyStatus> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_status_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final MyStatus status = statusList.get(position);


        holder.mDescription.setText(status.getDescription());
        holder.mTime.setText(status.getTime());

        holder.mUsername.setText(status.getUsername());

        Glide.with(context).load(status.getProfile()).apply(new RequestOptions().placeholder(R.mipmap.profile_image_default)).into(holder.statusProfileImage);
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Futa Ujumbe");
                builder.setPositiveButton("Futa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setId(status.getId());
                        MainActivity.myDatabase.myDao().deleteStatus(userStatus);
                        MyStatusActivity.statusList.clear();
                        MyStatusActivity.queryStatusDataFromDatabase(context,MyStatusActivity.displayName,MyStatusActivity.imageProfile);
                        deleteStatusFromServer(status.getId());
                        Toast.makeText(context, "Ujumbe umefutwa", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Acha", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    private void deleteStatusFromServer(final Long id) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                db.collection("status").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (DocumentSnapshot documentSnapshot: task.getResult()){

                            if (documentSnapshot.getString("create_at").equals(String.valueOf(id))){
                                String statusId = documentSnapshot.getId();
                                db.collection("status").document(statusId).delete();
                                Log.e(TAG, "onComplete: ***DELETED" );
                            }

                        }
                    }
                });
            }
        }).start();
    }


    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mUsername;
        private TextView mDescription;
        private TextView mTime;
        private CircleImageView statusProfileImage;
        private LinearLayout parentLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.status_username);
            mDescription = itemView.findViewById(R.id.status_description);
            mTime = itemView.findViewById(R.id.status_time);
            statusProfileImage = itemView.findViewById(R.id.my_status_profile_image);
            parentLayout =  itemView.findViewById(R.id.parent_layout_status);
        }
    }
}
