package com.example.rgain.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.signin.SignIn;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class Welcome extends AppCompatActivity {


    LinearLayout contextView ;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    MaterialButton btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();
        contextView = findViewById(R.id.linearLayoutWelcome);
        btnLogout = findViewById(R.id.btnLogout);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.welcome);
        toolbar.setVisibility(View.VISIBLE);

        Snackbar.make(contextView, R.string.welcome, Snackbar.LENGTH_SHORT)
                .show();


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Welcome.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
