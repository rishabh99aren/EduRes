package com.group16.example.edures;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    RecyclerView recyclerView;
    Context context;
    ArrayList<Comment> comment;
    ArrayList<String> items;

    public CommentAdapter(RecyclerView recyclerView, Context context, ArrayList<Comment> comment, ArrayList<String> items) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.comment = comment;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_view_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.sender.setText(comment.get(position).getSender());
        holder.text.setText(comment.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sender;
        TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            text = itemView.findViewById(R.id.comment);
        }
    }
}
