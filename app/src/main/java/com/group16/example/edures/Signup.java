package com.group16.example.edures;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
////import com.facebook.FacebookSdk;
//import com.facebook.FacebookSdk;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import static com.facebook.FacebookSdk.*;


public class Signup extends AppCompatActivity {

    private EditText email,password,confirmpassword;
    private Button email_sign_up_button;
    private FirebaseAuth firebaseAuth;
    private TextView login;
//    private LoginButton loginButton;
//    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setApplicationId("278246966157809");
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_signup);
//        initializeControls();
//        LoginFb();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmpassword = (EditText) findViewById(R.id.confirmpassword);
        email_sign_up_button = (Button) findViewById(R.id.email_sign_up_button);
        firebaseAuth = FirebaseAuth.getInstance();
        login = (TextView) findViewById(R.id.login_into_main);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(Signup.this, MainActivity.class));
            }
        });

        email_sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation shake = AnimationUtils.loadAnimation(Signup.this, R.anim.shake);
                v.startAnimation(shake);
                if (validate()) {
                    String user_email = email.getText().toString().trim();
                    String user_password = confirmpassword.getText().toString().trim();

                    if (user_email.indexOf("@lnmiit.ac.in") > 0) {

                        firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    sendEmailVerifiation();
                                } else {
                                    Toast.makeText(Signup.this, "Registration Failed", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    } else {
                        Toast.makeText(Signup.this, "This Email-ID is not in LNMIIT domain...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
//        private void initializeControls(){
//            loginButton = (LoginButton) findViewById(R.id.fb_login_btn);
//            callbackManager = CallbackManager.Factory.create();
//            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//                @Override
//                public void onSuccess(LoginResult loginResult) {
//                    Toast.makeText(Signup.this, "Login Successful\n" +
//                            loginResult.getAccessToken().getUserId() +
//                            "\n" + loginResult.getAccessToken().getToken(), Toast.LENGTH_SHORT).show();
//
//                }
//
//                @Override
//                public void onCancel() {
//                    Toast.makeText(Signup.this, "Login Canceled", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onError(FacebookException error) {
//
//                }
//            });
//
//        }
//        private void LoginFb(){
//
//        }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode,data);
//    }

    private Boolean validate(){
        Boolean result= false;

        String emailId= email.getText().toString();
        String pass= password.getText().toString();
        String confirmpass= confirmpassword.getText().toString();

        if(emailId.isEmpty() || pass.isEmpty() || confirmpass.isEmpty()){
            Toast.makeText(this, "Please Enter All Details", Toast.LENGTH_SHORT).show();
        }
        else if(!pass.equals(confirmpass)){
            Toast.makeText(this, "Oopss...Password didn't match", Toast.LENGTH_SHORT).show();
        }
        else
            return true;

        return result;
    }

    private void sendEmailVerifiation(){
        final FirebaseUser firebaseUser= firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Signup.this, "Successfully Registered, Verification link has been sent!! ", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(Signup.this,MainActivity.class));
                    }
                    else {
                        Toast.makeText(Signup.this, "Verification link hasn't been sent!!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder= new AlertDialog.Builder(Signup.this);
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
                System.exit(0);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}