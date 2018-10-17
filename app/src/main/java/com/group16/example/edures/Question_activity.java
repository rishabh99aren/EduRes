package com.group16.example.edures;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Question_activity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    ArrayList<Notes> notes = new ArrayList<Notes>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_activity);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase=database.getReference("/Questions");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notes.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String branch = postSnapshot.child("Branch").getValue(String.class);
                    String course = postSnapshot.child("Course").getValue(String.class);
                    String url = postSnapshot.child("Download_url").getValue(String.class);
                    String email = postSnapshot.child("Email").getValue(String.class);
                    String sem = postSnapshot.child("Sem").getValue(String.class);
                    notes.add(new Notes(branch,course,url,email,sem));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Missing", "Failed to read value.", databaseError.toException());
            }
        });
        Notesadapter nadapter = new Notesadapter(this,notes);
        ListView listview=( ListView) findViewById(R.id.noteslist);
        listview.setAdapter((ListAdapter) nadapter);
    }
}
