package com.example.saibahmed.firebase;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {

    SignInButton button2;

    //Reference to firebase
    private FirebaseAuth myAuth;

    //Ui References
    private EditText myEmail;
    private EditText myPassword;
    private final static int RC_SIGN_IN=2;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Grab Data
        myEmail=(EditText)findViewById(R.id.Login_email);
        myPassword=(EditText)findViewById(R.id.Login_password);

        //Get firebase instance
        myAuth=FirebaseAuth.getInstance();

        //Grap Google button
        button2=(SignInButton)findViewById(R.id.googleSignButton);
        button2.setOnClickListener(new View.OnClickListener() {

           //Google Button is tapped
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();



    }

    //Google sign in mehthod
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
           GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account=result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
            else
            {
                Toast.makeText(MainActivity.this,"Auth went wrong",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        myAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = myAuth.getCurrentUser();
                            Intent intent=new Intent(MainActivity.this,SecondOne.class);
                            finish();
                            startActivity(intent);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

    //Login button was Tapped
    public void  Login(View v){
        loginUserWithFirebase();
    }

    //Login user with firebase
    private void loginUserWithFirebase(){
        String email= myEmail.getText().toString();
        String password= myPassword.getText().toString();

        final boolean[] cancel = {false};
        final View[] focusView = {null};

        myEmail.setError(null);
        myPassword.setError(null);

        //implement a check like in SignUp Activity

        //Email validation
        if(TextUtils.isEmpty(email)&&!checkEmail(email)){
            myEmail.setError(getString((R.string.Invalid_Email)));
            focusView[0] =myEmail;
            cancel[0] =true;
        }

        if(cancel[0]){
            focusView[0].requestFocus();

        }
        //TODO;password validation

        if(!TextUtils.isEmpty(password)&&!checkPassword(password)){
            myPassword.setError(getString(R.string.Invalid_Password));
            focusView[0]=myPassword;
            cancel[0]=true;
        }

        if(email.equals("") || password.equals("")){
          new AlertDialog.Builder(this)
                  .setTitle("Please fill the Info")
                  .setPositiveButton(android.R.string.ok,null)
                  .setIcon(android.R.drawable.ic_dialog_alert)
                  .show();
            return;
        }
        Toast.makeText(this,"Logging you in...",Toast.LENGTH_SHORT).show();
        myAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i("FINDCODE_L","Was user Logged in"+task.isSuccessful());

                if (!task.isSuccessful()){

                    Toast.makeText(MainActivity.this,"Invalid email",Toast.LENGTH_SHORT).show();
                    Log.i("FINDCODE","MESSAGE"+task.getException());
                }
                else
                {
                    Intent intent=new Intent(MainActivity.this,SecondOne.class );
                    finish();
                    startActivity(intent);
                }
            }
        });

    }
    //Validation for Email
    private boolean checkEmail(String email){
        return email.contains("@");
    }

    //Validation for Password
    private boolean checkPassword(String password){
        String confirmPassword=myPassword.getText().toString();
        return confirmPassword.equals(password) && password.length()>4;
    }


    //Move user to register activity
    public void ragisterNewUser(View v){
        Intent intent=new Intent(this,SignUp.class);
        finish();
        startActivity(intent);
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
