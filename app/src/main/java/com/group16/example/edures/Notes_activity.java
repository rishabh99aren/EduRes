package com.group16.example.edures;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Notes_activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private DatabaseReference mDatabase;
    private ArrayList <Notes> notes = new ArrayList<Notes>() ;
    private ArrayList<String> items = new ArrayList<>();
    private StorageReference mStorageRef;
    private NotesAdapter nadapter;
    private Uri uri;
    private String email;
    private String sel_branch;
    private String sel_sem;
    private String sel_course;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private Spinner course;
    private ArrayAdapter<CharSequence> adapter3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog = new ProgressDialog(Notes_activity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("It will take few seconds!!");
        progressDialog.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase=database.getReference().child("Notes");
        recyclerView  = findViewById(R.id.noteslist);
        recyclerView.setLayoutManager(new LinearLayoutManager(Notes_activity.this));
        nadapter = new NotesAdapter(recyclerView,Notes_activity.this,notes, items);
        recyclerView.setAdapter(nadapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notes.clear();
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String branch = postSnapshot.child("Branch").getValue(String.class);
                    String course = postSnapshot.child("Course").getValue(String.class);
                    String email = postSnapshot.child("Email").getValue(String.class);
                    String sem = postSnapshot.child("Sem").getValue(String.class);
                    notes.add(new Notes(branch,course,email,sem));
                    items.add(postSnapshot.getKey());
                    nadapter.notifyDataSetChanged();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Missing", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private static final int FILE_SELECT_CODE = 0;

    public void upload(View v) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final Dialog dialog = new Dialog(Notes_activity.this);
        dialog.setContentView(R.layout.option_panel_notes);
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

        course = dialog.findViewById(R.id.course_spinner);
        course.setOnItemSelectedListener(this);

        Button b = dialog.findViewById(R.id.dialogButtonOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((sel_branch.equals("Null")) || (sel_sem.equals("Null")) || (sel_course.equals("Null"))) {
                    Toast.makeText(Notes_activity.this, "First select values for all fields", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(Notes_activity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                    }
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
            if(sel_branch.equals("Null")){
                sel_course = "Null";
            }
            else if(sel_branch.equals("CSE"))
            {
                switch (sel_sem) {
                    case "1st":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.course_array_1stsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;


                    case "2nd":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cse_course_array_2ndsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "3rd":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cse_course_array_3rdsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "4th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cse_course_array_4thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "5th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cse_course_array_5thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "6th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cse_course_array_6thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "7th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cse_course_array_7thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "8th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cse_course_array_8thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;
                }
            }
            else if(sel_branch.equals("CCE"))
            {
                switch (sel_sem) {
                    case "1st":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.course_array_1stsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;


                    case "2nd":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cce_course_array_2ndsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "3rd":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cce_course_array_3rdsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "4th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cce_course_array_4thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "5th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cce_course_array_5thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "6th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cce_course_array_6thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "7th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cce_course_array_7thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "8th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.cce_course_array_8thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                }
            }

            else if(sel_branch.equals("ECE"))
            {
                switch (sel_sem) {
                    case "1st":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.course_array_1stsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;


                    case "2nd":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.ece_course_array_2ndsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "3rd":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.ece_course_array_3rdsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "4th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.ece_course_array_4thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "5th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.ece_course_array_5thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "6th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.ece_course_array_6thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "7th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.ece_course_array_7thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "8th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.ece_course_array_8thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                }
            }

            else if(sel_branch.equals("ME"))
            {
                switch (sel_sem) {
                    case "1st":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.course_array_1stsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;


                    case "2nd":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.me_course_array_2ndsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "3rd":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.me_course_array_3rdsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "4th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.me_course_array_4thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "5th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.me_course_array_5thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "6th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.me_course_array_6thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "7th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.me_course_array_7thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                    case "8th":
                        adapter3 = ArrayAdapter.createFromResource(this,
                                R.array.me_course_array_8thsem, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        course.setAdapter(adapter3);
                        course.setOnItemSelectedListener(this);
                        break;

                }
            }
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setProgress(0);
        progressDialog.show();

        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK && data!=null) {
                    final String fileName = System.currentTimeMillis()+"";
                    uri = data.getData();
                    Toast.makeText(this,"A file is selected : "+ uri.getLastPathSegment(),Toast.LENGTH_SHORT).show();
                    final StorageReference riversRef = mStorageRef.child("Notes").child(fileName+".pdf");

                    riversRef.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.e("Tuts+", "uri: " + uri.toString());
                                            progressDialog.cancel();
                                            Toast.makeText(Notes_activity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    mDatabase=database.getReference().child("Notes").child(fileName);
                                    mDatabase.child("Branch").setValue(sel_branch);
                                    mDatabase.child("Sem").setValue(sel_sem);
                                    mDatabase.child("Course").setValue(sel_course);
                                    mDatabase.child("Email").setValue(email);
                                    items.add(fileName);
                                    nadapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.cancel();
                                    Toast.makeText(Notes_activity.this,"Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                    progressDialog.setProgress(currentProgress);
                                }

                            });
                }else if(data==null){
                    progressDialog.dismiss();
                    Toast.makeText(Notes_activity.this,"Please select file to upload", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void filter(View v){
        final Dialog dialog = new Dialog(Notes_activity.this);
        dialog.setContentView(R.layout.option_panel_notes);
        dialog.setTitle("Option Panel");
        Spinner branch = dialog.findViewById(R.id.branch_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter1);
        branch.setOnItemSelectedListener(this);

        course = dialog.findViewById(R.id.course_spinner);

        Spinner sem = dialog.findViewById(R.id.sem_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sem_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sem.setAdapter(adapter2);
        sem.setOnItemSelectedListener(this);

        Button b = dialog.findViewById(R.id.dialogButtonOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                mDatabase=database.getReference("/Notes");
                final RecyclerView recyclerview=findViewById(R.id.noteslist);
                recyclerview.setAdapter(nadapter);

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        items.clear();
                        notes.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String branch = postSnapshot.child("Branch").getValue(String.class);
                            String course = postSnapshot.child("Course").getValue(String.class);
                            String email = postSnapshot.child("Email").getValue(String.class);
                            String sem = postSnapshot.child("Sem").getValue(String.class);

                            if((sel_branch.equals("Null") || branch.equals(sel_branch)) &&
                                    (sel_sem.equals("Null") || sem.equals(sel_sem)) &&
                                    (sel_course.equals("Null") ||course.equals(sel_course))) {
                                items.add(postSnapshot.getKey());
                                notes.add(new Notes(branch, course, email, sem));
                            }
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