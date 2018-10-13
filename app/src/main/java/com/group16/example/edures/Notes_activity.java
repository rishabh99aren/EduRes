package com.group16.example.edures;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
                notes.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String branch = postSnapshot.child("Branch").getValue(String.class);
                    String course = postSnapshot.child("Course").getValue(String.class);
                    String url = postSnapshot.child("Download_url").getValue(String.class);
                    int sem = postSnapshot.child("Sem").getValue(Integer.class);
                    int year = postSnapshot.child("Year").getValue(Integer.class);
                    String title = postSnapshot.child("Title").getValue(String.class);
                    //Use the dataType you are using and also use the reference of those childs inside arrays\\

                    // Putting Data into Getter Setter \\
                    notes.add(new Notes(branch,course,url,sem,year,title));
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
