package com.example.rgain.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    TextInputLayout textInputLayoutEmail, textInputLayoutPass, textInputLayoutConfirmPassword, textInputLayoutPhone;
    MaterialButton btnSignin, btnSignup;
    View contextView;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.setTitle("Signup");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        textInputLayoutEmail = findViewById(R.id.textUsernameSignup);
        textInputLayoutPass = findViewById(R.id.textPasswordSignup);
        textInputLayoutConfirmPassword = findViewById(R.id.textConfirmPasswordSignup);
        textInputLayoutPhone = findViewById(R.id.textPhoneSignup);
        btnSignin = findViewById(R.id.btnSigninSignup);
        btnSignup = findViewById(R.id.btnSignupSignup);
        contextView = findViewById(R.id.linearVIewSIgnup);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        btnSignin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSigninSignup:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.btnSignupSignup:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        final String email = textInputLayoutEmail.getEditText().getText().toString().trim();
        final String password = textInputLayoutPass.getEditText().getText().toString().trim();
        String repeatPassword = textInputLayoutConfirmPassword.getEditText().getText().toString().trim();
        final String phone = textInputLayoutPhone.getEditText().getText().toString().trim();

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

        if (!Patterns.PHONE.matcher(phone).matches()) {
            textInputLayoutPhone.setError("Invalid Phone Number");
            textInputLayoutPhone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            textInputLayoutPass.setError("Password can't be empty");
            textInputLayoutPass.requestFocus();
            return;
        }

        if (repeatPassword.isEmpty()) {
            textInputLayoutConfirmPassword.setError("Password can't be empty");
            textInputLayoutConfirmPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            textInputLayoutPass.setError("Too short, minimum length 6");
            textInputLayoutPass.requestFocus();
            return;
        }

        if (!password.equals(repeatPassword)) {
            textInputLayoutConfirmPassword.setError("Password do not match");
            textInputLayoutConfirmPassword.requestFocus();
            return;
        }
        checkIfPhoneExists(email, password, phone);


        textInputLayoutPhone.setErrorEnabled(false);
        textInputLayoutConfirmPassword.setErrorEnabled(false);
        textInputLayoutPass.setErrorEnabled(false);
        textInputLayoutEmail.setErrorEnabled(false);
        progressBar.setVisibility(View.VISIBLE);



    }

    private void startRegistration(final String email, final String password, final String phone) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                progressBar.setVisibility(View.INVISIBLE);
                if (task.isSuccessful()) {
                    Snackbar.make(contextView, R.string.success, Snackbar.LENGTH_SHORT)
                            .show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {

                            user = mAuth.getCurrentUser().getUid();
                            databaseReference.child(phone).setValue(phone);
                            Intent intent = new Intent(SignupActivity.this, Welcome.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });

                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Snackbar.make(contextView, R.string.already, Snackbar.LENGTH_SHORT)
                                .show();
                    } else {
                        Snackbar.make(contextView, R.string.failed, Snackbar.LENGTH_SHORT)
                                .show();
                        Snackbar.make(contextView, R.string.failed, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }

            }
        });
    }

    private void checkIfPhoneExists(final String email, final String password, final String phone) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Snackbar.make(contextView, R.string.exists, Snackbar.LENGTH_SHORT)
                            .show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else{
                    startRegistration(email, password,phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
