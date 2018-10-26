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
import android.widget.EditText;
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

public class QueryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private DatabaseReference mDatabase;
    private ArrayList <Query> query = new ArrayList<>() ;
    private ArrayList<String> items = new ArrayList<>();
    private QueryAdapter nadapter;
    private String email;
    private String sel_receiver;
    private String sel_type;
    private String fileId;
    private String details;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase=database.getReference().child("Query");
        recyclerView  = findViewById(R.id.querylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(QueryActivity.this));
        nadapter = new QueryAdapter(recyclerView,QueryActivity.this,query, items);
        recyclerView.setAdapter(nadapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                query.clear();
                items.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String receiver = postSnapshot.child("Receiver").getValue(String.class);
                    String sender = postSnapshot.child("Sender").getValue(String.class);
                    String file = postSnapshot.child("ID").getValue(String.class);
                    String type = postSnapshot.child("Type").getValue(String.class);
                    String detail = postSnapshot.child("Details").getValue(String.class);
                    try {
                        if ((receiver.equals("All")) || (receiver.equals("Batch-Mates") && (email.contains(sender.substring(0, 2))))
                                || (receiver.equals("Admin") && (email.equals("adedures@gmail.com"))) || (sender.equals(email))) {
                            query.add(new Query(receiver, sender, file, type, detail));
                            items.add(postSnapshot.getKey());
                        }
                        nadapter.notifyDataSetChanged();
                    }catch (NullPointerException e){
                        Intent i = new Intent(QueryActivity.this,QueryActivity.class);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Missing", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void upload(View v) {
        final Dialog dialog = new Dialog(QueryActivity.this);
        dialog.setContentView(R.layout.option_panel_query);

        Spinner receiver = dialog.findViewById(R.id.receiver_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.receiver_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiver.setAdapter(adapter1);
        receiver.setOnItemSelectedListener(this);

        Spinner type = dialog.findViewById(R.id.query_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.query_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter2);
        type.setOnItemSelectedListener(this);



        Button b = dialog.findViewById(R.id.dialogButtonOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText id = dialog.findViewById(R.id.fileId);
                fileId = id.getText().toString();

                EditText e = dialog.findViewById(R.id.details);
                details = e.getText().toString();

                if((sel_receiver.equals("Null")) || (sel_type.equals("Null")) || (fileId.equals("")) || (details.equals(""))) {
                    Toast.makeText(QueryActivity.this, "First fill values for all fields", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.dismiss();
                    String fileName = System.currentTimeMillis()+"";
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    mDatabase=database.getReference().child("Query").child(fileName);
                    mDatabase.child("Sender").setValue(email);
                    mDatabase.child("Receiver").setValue(sel_receiver);
                    mDatabase.child("ID").setValue(fileId);
                    mDatabase.child("Type").setValue(sel_type);
                    mDatabase.child("Details").setValue(details);
                    items.add(fileName);

                    nadapter.notifyDataSetChanged();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.receiver_spinner)
        {
            sel_receiver = parent.getSelectedItem().toString();
        }
        else if(spinner.getId() == R.id.query_spinner)
        {
            sel_type = parent.getSelectedItem().toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void filter(View v){
        final Dialog dialog = new Dialog(QueryActivity.this);
        dialog.setContentView(R.layout.option_panel_query_filter);

        Spinner receiver = dialog.findViewById(R.id.receiver_spinner);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.receiver_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        receiver.setAdapter(adapter1);
        receiver.setOnItemSelectedListener(this);

        Spinner type = dialog.findViewById(R.id.query_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.query_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter2);
        type.setOnItemSelectedListener(this);

        Button b = dialog.findViewById(R.id.dialogButtonOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                mDatabase=database.getReference().child("Query");
                recyclerView  = findViewById(R.id.querylist);
                recyclerView.setLayoutManager(new LinearLayoutManager(QueryActivity.this));
                nadapter = new QueryAdapter(recyclerView,QueryActivity.this,query, items);
                recyclerView.setAdapter(nadapter);

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        query.clear();
                        items.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            String receiver = postSnapshot.child("Receiver").getValue(String.class);
                            String sender = postSnapshot.child("Sender").getValue(String.class);
                            String file = postSnapshot.child("ID").getValue(String.class);
                            String type = postSnapshot.child("Type").getValue(String.class);
                            String detail = postSnapshot.child("Details").getValue(String.class);
                            if((sel_type.equals("Null") || type.equals(sel_type)) &&
                                    (sel_receiver.equals("Null") || receiver.equals(sel_receiver))) {
                                try {
                                    if ((receiver.equals("All")) || (receiver.equals("Batch-Mates") && (email.contains(sender.substring(0, 2))))
                                            || (receiver.equals("Admin") && (email.equals("adedures@gmail.com"))) || (sender.equals(email))) {
                                        query.add(new Query(receiver, sender, file, type, detail));
                                        items.add(postSnapshot.getKey());
                                    }
                                    nadapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    Intent i = new Intent(QueryActivity.this, QueryActivity.class);
                                    startActivity(i);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        dialog.show();
    }
}
