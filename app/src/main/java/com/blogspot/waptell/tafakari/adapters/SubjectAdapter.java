package com.blogspot.waptell.tafakari.adapters;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.waptell.tafakari.R;
import com.blogspot.waptell.tafakari.SubjectInfoActivity;
import com.blogspot.waptell.tafakari.models.Subject;

import java.util.List;


public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private List<Subject> subjectList;

    public SubjectAdapter(List<Subject> subjectList, Context context) {
        this.subjectList = subjectList;
        this.context = context;
    }

    private Context context;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Subject subject = subjectList.get(position);

        holder.mSubTitle.setText(subject.getTitle());
        holder.mSubEvent.setText(subject.getEvent());
        holder.mLetter.setText(subject.getEvent().substring(0,1));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SubjectInfoActivity.class);
                intent.putExtra("description",subject.getDescription());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mSubTitle, mLetter;
        private TextView mSubEvent;
        private LinearLayout parentLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            mSubTitle = itemView.findViewById(R.id.subject_title);
            mSubEvent = itemView.findViewById(R.id.subject_event);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            mLetter = itemView.findViewById(R.id.letter);
        }
    }
}
