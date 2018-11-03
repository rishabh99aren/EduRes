package com.group16.example.edures;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_password extends AppCompatActivity {
    private EditText email_forget;
    private Button reset_password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        reset_password= (Button) findViewById(R.id.reset_password);
        email_forget= (EditText) findViewById(R.id.email_forget);
        firebaseAuth = FirebaseAuth.getInstance();

        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_forget.getText().toString().trim();
                if(email.equals("")){
                    Toast.makeText(Forget_password.this, "Please Enter Registered Email ID", Toast.LENGTH_SHORT).show();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Forget_password.this, "Reset Password link has been sent", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(Forget_password.this,MainActivity.class));
                            }
                            else {
                                Toast.makeText(Forget_password.this, "Error In sending password reset link", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
