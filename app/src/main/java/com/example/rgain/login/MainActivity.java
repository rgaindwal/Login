package com.example.rgain.login;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    TextInputLayout textInputLayoutEmail, textInputLayoutPass;
    MaterialButton btnSignin, btnSignup;
    ProgressBar progressBar;
    LinearLayout contextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBarsignin);
        progressBar.setVisibility(View.GONE);

        mAuth =  FirebaseAuth.getInstance();

        textInputLayoutEmail = findViewById(R.id.textUsernameSignin);
        textInputLayoutPass = findViewById(R.id.textPasswordSignin);

        btnSignin = findViewById(R.id.btnSigninSignin);
        btnSignup = findViewById(R.id.btnSignupSignin);


        contextView = findViewById(R.id.linearViewSignin);
        btnSignin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSigninSignin :
                userSignin();
                break;

            case R.id.btnSignupSignin :
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }

    }

    private void userSignin() {
        String email = textInputLayoutEmail.getEditText().getText().toString().trim();
        String pass = textInputLayoutPass.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            textInputLayoutEmail.setError("Email can't be empty");
            textInputLayoutEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputLayoutEmail.setError("Invalid Email");
            textInputLayoutEmail.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            textInputLayoutPass.setError("Password can't be empty");
            textInputLayoutPass.requestFocus();
            return;
        }

        if (pass.length() < 6) {
            textInputLayoutPass.setError("Too short, minimum length 6");
            textInputLayoutPass.requestFocus();
            return;
        }



        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, Welcome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else{
                    Snackbar.make(contextView, R.string.failedsignin, Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(MainActivity.this, Welcome.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
