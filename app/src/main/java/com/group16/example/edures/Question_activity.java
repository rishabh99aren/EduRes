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
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.ArrayList;

public class Question_activity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ArrayList <Questions> questions = new ArrayList<>() ;
    private ArrayList<String> items = new ArrayList<>();
    private StorageReference mStorageRef;
    private QuestionAdapter nadapter;
    private Uri uri;
    private String email;
    private String sel_branch;
    private String sel_sem;
    private String sel_course;
    private String sel_type;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase=database.getReference().child("Questions");
        recyclerView  = findViewById(R.id.questionslist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        nadapter = new QuestionAdapter(recyclerView,this,questions, items);
        recyclerView.setAdapter(nadapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questions.clear();
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String branch = postSnapshot.child("Branch").getValue(String.class);
                    String course = postSnapshot.child("Course").getValue(String.class);
                    String email = postSnapshot.child("Email").getValue(String.class);
                    String sem = postSnapshot.child("Sem").getValue(String.class);
                    String type = postSnapshot.child("Type").getValue(String.class);
                    questions.add(new Questions(branch,course,email,sem,type));
                    items.add(postSnapshot.getKey());
                    nadapter.notifyDataSetChanged();
                }
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
        final Dialog dialog = new Dialog(Question_activity.this);
        dialog.setContentView(R.layout.option_panel_questions);
        Spinner branch = dialog.findViewById(R.id.branch_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter1);
        branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_branch = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner sem = dialog.findViewById(R.id.sem_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sem_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sem.setAdapter(adapter2);
        sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_sem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner course = dialog.findViewById(R.id.course_spinner);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.cse_course_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course.setAdapter(adapter3);
        course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_course = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner type = dialog.findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter4);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button b = dialog.findViewById(R.id.dialogButtonOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((sel_branch.equals("Null")) || (sel_sem.equals("Null")) || (sel_course.equals("Null"))) {
                    Toast.makeText(Question_activity.this, "First select values for all fields", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(Question_activity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
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
                    final StorageReference riversRef = mStorageRef.child("Questions").child(fileName+".pdf");

                    riversRef.putFile(uri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.e("Tuts+", "uri: " + uri.toString());
                                            progressDialog.cancel();
                                            Toast.makeText(Question_activity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    mDatabase=database.getReference().child("Questions").child(fileName);
                                    mDatabase.child("Branch").setValue(sel_branch);
                                    mDatabase.child("Sem").setValue(sel_sem);
                                    mDatabase.child("Course").setValue(sel_course);
                                    mDatabase.child("Email").setValue(email);
                                    mDatabase.child("Type").setValue(sel_type);
                                    items.add(fileName);
                                    nadapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Question_activity.this,"Upload Failed", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                    progressDialog.setProgress(currentProgress);
                                }

                            });
                }
                break;
        }
    }

    public void filter(View v){
        final Dialog dialog = new Dialog(Question_activity.this);
        dialog.setContentView(R.layout.option_panel_questions);
        Spinner branch = dialog.findViewById(R.id.branch_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter1);
        branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_branch = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner sem = dialog.findViewById(R.id.sem_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.sem_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sem.setAdapter(adapter2);
        sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_sem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner course = dialog.findViewById(R.id.course_spinner);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.cse_course_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course.setAdapter(adapter3);
        course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_course = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner type = dialog.findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter4);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_type = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button b = dialog.findViewById(R.id.dialogButtonOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                mDatabase=database.getReference().child("Questions");
                final RecyclerView recyclerview=findViewById(R.id.questionslist);
                recyclerview.setAdapter(nadapter);

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        items.clear();
                        questions.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String branch = postSnapshot.child("Branch").getValue(String.class);
                            String course = postSnapshot.child("Course").getValue(String.class);
                            String email = postSnapshot.child("Email").getValue(String.class);
                            String sem = postSnapshot.child("Sem").getValue(String.class);
                            String type = postSnapshot.child("Type").getValue(String.class);
                            if((sel_branch.equals("Null") || branch.equals(sel_branch)) &&
                                    (sel_sem.equals("Null") || sem.equals(sel_sem)) &&
                                    (sel_course.equals("Null") ||course.equals(sel_course)) &&
                                    (sel_type.equals("Null") || type.equals(sel_type))) {
                                items.add(postSnapshot.getKey());
                                questions.add(new Questions(branch, course, email, sem, type));
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
