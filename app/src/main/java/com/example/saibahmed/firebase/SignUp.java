package com.example.saibahmed.firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    //Ref to fields
    private AutoCompleteTextView MyUserNameView;
    private EditText myEmail;
    private EditText myPassword;
    private EditText myPasswordConfirm;
    private final static String DISPLAY_NAME ="UserName";
    private final static String SIGNUP_PREF ="SignUp";

    //firebase reference
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //get values on create
        myEmail=(EditText)findViewById(R.id.RagisterEmail);
        MyUserNameView=(AutoCompleteTextView)findViewById(R.id.YourUsername);
        myPassword=(EditText)findViewById(R.id.RagisterPassword);
        myPasswordConfirm=(EditText)findViewById(R.id.ConfirmPassword);

        //Get a hold of firebase instance
        myAuth=FirebaseAuth.getInstance();
    }

    //signUp method call by tapping
    public void signUp(View v){
        ragisterUser();
    }

    //Actual ragistration happens here
     private void ragisterUser(){
        myEmail.setError(null);
        myPassword.setError(null);
        myPasswordConfirm.setError(null);

        //Grab Values
         String email= myEmail.getText().toString();
         String password=myPassword.getText().toString();

         boolean cancel=false;
         View  focusView=null;

         //Password validation
         if(!TextUtils.isEmpty(password)&&!checkPassword(password)){
             myPassword.setError(getString(R.string.Invalid_Password));
             focusView=myPassword;
             cancel=true;
         }

         //Email validation
         if(TextUtils.isEmpty(email)&&!checkEmail(email)){
             myEmail.setError(getString((R.string.Invalid_Email)));
             focusView=myEmail;
             cancel=true;
         }

         if(cancel){
             focusView.requestFocus();

         }
         else
         {
             createUser();
         }

     }

    //Validation for Email
    private boolean checkEmail(String email){
        return email.contains("@");
    }

    //Validation for Password
    private boolean checkPassword(String password){
        String confirmPassword=myPasswordConfirm.getText().toString();
        return confirmPassword.equals(password) && password.length()>4;
    }

    //SignUp a user at firebase
    private void createUser(){
        String email=myEmail.getText().toString();
        String password=myPassword.getText().toString();

        //call method from firebase
        myAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Delete before production
                Log.i("FINDCODE","User creation was"+task.isSuccessful());

                if(!task.isSuccessful())
                {
                    showErrorBox("Oops Registration failed");
                }
                else
                {
                    saveUserName();
                    Toast.makeText(SignUp.this,"Success",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(SignUp.this,MainActivity.class);
                    finish();
                    startActivity(intent);


                }
            }

        });
    }


    //use shared prefs for usernames
    private void saveUserName(){
        String userName=MyUserNameView.getText().toString();
        SharedPreferences pref=getSharedPreferences(SIGNUP_PREF,0);
        pref.edit().putString(DISPLAY_NAME,userName).apply();
    }

    //Create error box for errors
    private void showErrorBox(String message){
        new AlertDialog.Builder(this)
                .setTitle("Heyyyy")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
