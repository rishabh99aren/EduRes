package com.group16.example.edures;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder>{

    private DatabaseReference mDatabase;
    private CommentAdapter nadapter;
    private ArrayList <Comment> comments = new ArrayList<>() ;
    private ArrayList<String> comment_items = new ArrayList<>();

    RecyclerView recyclerView;
    Context context;
    ArrayList<Query> query;
    ArrayList<String> items;

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

            }
        });
        holder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.show_comment_panel);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                mDatabase=database.getReference().child("Query").child(items.get(position)).child("Comments");
                recyclerView  = dialog.findViewById(R.id.querylist);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                nadapter = new CommentAdapter(recyclerView,context,comments, comment_items);
                recyclerView.setAdapter(nadapter);
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
