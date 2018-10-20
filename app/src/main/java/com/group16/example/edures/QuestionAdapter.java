package com.group16.example.edures;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>{

    RecyclerView recyclerView;
    Context context;
    ArrayList<Questions> questions;
    ArrayList<String> items;

    public QuestionAdapter(RecyclerView recyclerView, Context context, ArrayList<Questions> questions, ArrayList<String> items){
        this.recyclerView = recyclerView;
        this.context = context;
        this.questions = questions;
        this.items = items;
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_view_questions, parent, false);
        return new QuestionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QuestionAdapter.ViewHolder holder, int position) {
        holder.course.setText(questions.get(position).getCourse());
        holder.uploader.setText(questions.get(position).getEmail());
        holder.type.setText(questions.get(position).getType());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView course;
        TextView uploader;
        TextView type;
        public ViewHolder(View itemView) {
            super(itemView);
            course = itemView.findViewById(R.id.title);
            uploader = itemView.findViewById(R.id.uploader);
            type = itemView.findViewById(R.id.type);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildLayoutPosition(v);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Questions").child(items.get(position)+".pdf");
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("Tuts+", "uri: " + uri.toString());
                            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                            context.startActivity(intent);
                        }
                    });
                }
            });
        }
    }
}
