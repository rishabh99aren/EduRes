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

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    RecyclerView recyclerView;
    Context context;
    ArrayList<Notes> notes;
    ArrayList<String> items;

    public NotesAdapter(RecyclerView recyclerView, Context context, ArrayList<Notes> notes, ArrayList<String> items){
        this.recyclerView = recyclerView;
        this.context = context;
        this.notes = notes;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_view_notes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.course.setText(notes.get(position).getCourse());
        holder.uploader.setText(notes.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView course;
        TextView uploader;
        public ViewHolder(View itemView) {
            super(itemView);
            course = itemView.findViewById(R.id.title);
            uploader = itemView.findViewById(R.id.uploader);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildLayoutPosition(v);
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Notes").child(items.get(position)+".pdf");
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
