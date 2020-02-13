package com.blogspot.waptell.tafakari.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blogspot.waptell.tafakari.R;
import com.blogspot.waptell.tafakari.models.Status;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {

    private List<Status> statusList;
    private Context context;
    private static final String TAG = "StatusAdapter";

    public StatusAdapter(List<Status> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Status status = statusList.get(position);

        holder.mDescription.setText(status.getDescription());
        holder.mTime.setText(status.getTime());

        String username = status.getUsername();
        String profile_image = status.getImage();
        holder.mUsername.setText(username);
        if (profile_image.equals("default")) {
            Glide.with(context).load(R.mipmap.profile_image_default).apply(new RequestOptions().placeholder(R.mipmap.profile_image_default)).into(holder.statusProfileImage);
        } else {
            Glide.with(context).load(profile_image).apply(new RequestOptions().placeholder(R.mipmap.profile_image_default)).into(holder.statusProfileImage);
        }
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

        private ViewHolder(View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.status_username);
            mDescription = itemView.findViewById(R.id.status_description);
            mTime = itemView.findViewById(R.id.status_time);
            statusProfileImage = itemView.findViewById(R.id.status_profile_image);
        }
    }
}
