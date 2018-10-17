package com.group16.example.edures;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Notes_activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private DatabaseReference mDatabase;
    private int count=1;
    private int temp;
    private ArrayList <Notes> notes = new ArrayList<Notes>() ;
    private StorageReference mStorageRef;
    private Notesadapter nadapter;
    private String path;
    private String email;
    private String sel_branch;
    private String sel_sem;
    private String sel_course;
    private ProgressBar pb;
    private Task<Uri> downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        pb = findViewById(R.id.pbUploadProgress);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase=database.getReference("/Notes");
        nadapter = new Notesadapter(this,notes);
        final ListView listview=( ListView) findViewById(R.id.noteslist);
        listview.setAdapter((ListAdapter) nadapter);

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
                    count++;
                    nadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Missing", "Failed to read value.", databaseError.toException());
            }
        });
        Log.w("Value","Count="+count);
    }

    private static final int FILE_SELECT_CODE = 0;

    public void upload(View v) {
        mStorageRef = FirebaseStorage.getInstance().getReference("/Notes" + count);
        temp = count;
        final Dialog dialog = new Dialog(Notes_activity.this);
        dialog.setContentView(R.layout.option_panel);
        dialog.setTitle("Option Panel");
        Spinner branch = dialog.findViewById(R.id.branch_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter1);
        branch.setOnItemSelectedListener(this);

        Spinner sem = dialog.findViewById(R.id.sem_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sem_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sem.setAdapter(adapter2);
        sem.setOnItemSelectedListener(this);

        Spinner course = dialog.findViewById(R.id.course_spinner);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.cse_course_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course.setAdapter(adapter3);
        course.setOnItemSelectedListener(this);

        Button b = dialog.findViewById(R.id.dialogButtonOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(Notes_activity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.branch_spinner)
        {
            sel_branch = parent.getSelectedItem().toString();
        }
        else if(spinner.getId() == R.id.sem_spinner)
        {
            sel_sem = parent.getItemAtPosition(pos).toString();
        }
        else if(spinner.getId() == R.id.course_spinner)
        {
            sel_course = parent.getItemAtPosition(pos).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        pb.setVisibility(View.VISIBLE);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    path = data.getData().getPath();
                    Uri file = Uri.fromFile(new File(path));
                    StorageReference riversRef = mStorageRef;

                    riversRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    pb.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Notes_activity.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    mDatabase=database.getReference("/Notes");
                                    mDatabase.child("/"+temp+"/Branch").setValue(sel_branch);
                                    mDatabase.child("/"+temp+"/Sem").setValue(sel_sem);
                                    mDatabase.child("/"+temp+"/Course").setValue(sel_course);
                                    mDatabase.child("/"+temp+"/Email").setValue(email);
                                    mDatabase.child("/"+temp+"/Download_url").setValue(downloadUrl.toString());
                                    nadapter.notifyDataSetChanged();
                                    count++;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pb.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Notes_activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                break;
        }
    }

    public void filter(View v){
        final Dialog dialog = new Dialog(Notes_activity.this);
        dialog.setContentView(R.layout.option_panel);
        dialog.setTitle("Option Panel");
        Spinner branch = dialog.findViewById(R.id.branch_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter1);
        branch.setOnItemSelectedListener(this);

        Spinner sem = dialog.findViewById(R.id.sem_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sem_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sem.setAdapter(adapter2);
        sem.setOnItemSelectedListener(this);

        Spinner course = dialog.findViewById(R.id.course_spinner);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.cse_course_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course.setAdapter(adapter3);
        course.setOnItemSelectedListener(this);

        Button b = dialog.findViewById(R.id.dialogButtonOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                mDatabase=database.getReference("/Notes");
                final ListView listview=( ListView) findViewById(R.id.noteslist);
                listview.setAdapter((ListAdapter) nadapter);

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
                            if((sel_branch.equals("Null") || branch.equals(sel_branch)) &&
                                    (sel_sem.equals("Null") || sem.equals(sel_sem)) &&
                                    (sel_course.equals("Null") ||course.equals(sel_course)))
                            notes.add(new Notes(branch,course,url,email,sem));
                            nadapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("Missing", "Failed to read value.", databaseError.toException());
                    }
                });
            }
        });
        dialog.show();
    }

}
