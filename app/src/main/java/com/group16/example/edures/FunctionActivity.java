package com.group16.example.edures;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class FunctionActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        mAuth = FirebaseAuth.getInstance();

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

    }


    public void note(View v){
        Intent i = new Intent(FunctionActivity.this,Notes_activity.class);
        startActivity(i);
    }

    public void question(View v){
        Intent i = new Intent(FunctionActivity.this,Question_activity.class);
        startActivity(i);
    }

    public void solution(View v){

    }

    public void forum(View v){

    }

    public void signout(View v){
        mAuth.signOut();
        Intent i = new Intent(FunctionActivity.this,MainActivity.class);
        startActivity(i);
        this.finish();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()){
//            case R.id.menuLogout:
//                mAuth.getInstance().signOut();
//                finish();
//                startActivity(new Intent(FunctionActivity.this,MainActivity.class));
//                break;
//        }
//        return true;
//    }


    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(FunctionActivity.this);
        builder.setMessage("You are quitting the app\nAre you sure?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}