package com.example.saibahmed.firebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SecondOne extends AppCompatActivity {
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_one);
        myAuth=FirebaseAuth.getInstance();
    }


    public void Logout(View view) {
        myAuth.signOut();
        Toast.makeText(SecondOne.this,"Logging you out..",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this,MainActivity.class);
        finish();
        startActivity(intent);
    }
}
