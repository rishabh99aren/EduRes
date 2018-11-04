package com.group16.example.edures;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FunctionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        mAuth = FirebaseAuth.getInstance();
    }

    public void note(View v){
        if(isNetworkAvailable()) {
            Intent i = new Intent(FunctionActivity.this, Notes_activity.class);
            startActivity(i);
        }else{
            Toast.makeText(this,"Network Unavailable!",Toast.LENGTH_SHORT).show();
        }
    }

    public void question(View v){
        if(isNetworkAvailable()) {
            Intent i = new Intent(FunctionActivity.this,Question_activity.class);
            startActivity(i);
        }else{
            Toast.makeText(this,"Network Unavailable!",Toast.LENGTH_SHORT).show();
        }
    }

    public void forum(View v){
        if(isNetworkAvailable()) {
            Intent i = new Intent(FunctionActivity.this,QueryActivity.class);
            startActivity(i);
        }else{
            Toast.makeText(this,"Network Unavailable!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                mAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(FunctionActivity.this,MainActivity.class));
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        MenuItem menuItem = menu.findItem(R.id.user);
        menuItem.setTitle(user.getEmail().substring(0,8));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(FunctionActivity.this);
        dialog.setContentView(R.layout.exit_dialog);
        TextView positive = dialog.findViewById(R.id.yes);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView negative = dialog.findViewById(R.id.no);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}