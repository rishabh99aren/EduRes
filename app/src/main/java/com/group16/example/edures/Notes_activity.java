package com.group16.example.edures;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Notes_activity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    ArrayList <Notes> notes = new ArrayList<Notes>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase=database.getReference("/Notes");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notes notes1 = dataSnapshot.getValue(Notes.class);
                notes.add(notes1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Notesadapter nadapter = new Notesadapter(this,notes);
        ListView listview=( ListView) findViewById(R.id.noteslist);
        listview.setAdapter((ListAdapter) nadapter);
        }
}
