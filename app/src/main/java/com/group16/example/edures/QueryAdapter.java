package com.group16.example.edures;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder>{

    private DatabaseReference mDatabase;
    private CommentAdapter nadapter;
    private ArrayList <Comment> comments = new ArrayList<>() ;
    private ArrayList<String> comment_items = new ArrayList<>();

    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<Query> query;
    private ArrayList<String> items;

    public QueryAdapter(RecyclerView recyclerView, Context context, ArrayList<Query> query, ArrayList<String> items){
        this.recyclerView = recyclerView;
        this.context = context;
        this.query = query;
        this.items = items;
    }

    @Override
    public QueryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_view_query, parent, false);
        return new QueryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QueryAdapter.ViewHolder holder, final int position) {
        holder.sender.setText(query.get(position).getSender());
        holder.type.setText(query.get(position).getType());
        holder.file.setText(query.get(position).getID());
        holder.details.setText(query.get(position).getDetail());
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.add_comment_panel);

                Button b = dialog.findViewById(R.id.dialogboxclose);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText com = dialog.findViewById(R.id.add_comment);
                        if (com.getText().toString().equals("")) {
                            Toast.makeText(context, "Empty comment can't be added", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            String fileName = System.currentTimeMillis() + "";
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            mDatabase = database.getReference().child("Query").child(items.get(position)).child("Comments").child(fileName);
                            mDatabase.child("Sender").setValue(query.get(position).getSender());
                            mDatabase.child("Text").setValue(com.getText().toString());
                            comment_items.add(fileName);
                            try {
                                nadapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Intent i = new Intent(context, QueryActivity.class);
                                context.startActivity(i);
                            }
                        }
                    }
                });
                dialog.show();
            }
        });

        holder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.show_comment_panel);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                mDatabase = database.getReference().child("Query").child(items.get(position)).child("Comments");
                recyclerView = dialog.findViewById(R.id.commentlist);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        comments.clear();
                        comment_items.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String sender = postSnapshot.child("Sender").getValue(String.class);
                            String text = postSnapshot.child("Text").getValue(String.class);
                            comments.add(new Comment(sender, text));
                            comment_items.add(postSnapshot.getKey());
                            try {
                                nadapter = new CommentAdapter(recyclerView, context, comments, comment_items);
                                recyclerView.setAdapter(nadapter);
                                nadapter.notifyDataSetChanged();
                            }catch (Exception e){
                                Intent i = new Intent(context,context.getClass());
                                context.startActivity(i);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Button b = dialog.findViewById(R.id.dialogboxOk);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if(comment_items.size() > 0) {
                    dialog.show();
                }else{
                    Toast.makeText(context,"No comment added yet",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return query.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView sender;
        TextView type;
        TextView file;
        TextView details;
        Button add;
        Button show;
        public ViewHolder(View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            type = itemView.findViewById(R.id.reason);
            file = itemView.findViewById(R.id.fileId);
            details = itemView.findViewById(R.id.query);
            add = itemView.findViewById(R.id.add);
            show = itemView.findViewById(R.id.show);
        }
    }
}
