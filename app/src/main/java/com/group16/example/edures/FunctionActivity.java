package com.group16.example.edures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }

    private void note(View v){
        
    }

    private void question(View v){

    }

    private void solution(View v){

    }

    private void forum(View v){

    }

    public void signout(View v){
        mAuth.signOut();
        Intent i = new Intent(FunctionActivity.this,MainActivity.class);
        startActivity(i);
        this.finish();
    }
}
