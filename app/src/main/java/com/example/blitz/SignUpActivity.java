package com.example.blitz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blitz.Models.Users;
import com.example.blitz.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //getSupportActionBar().hide();
        // Initialize Firebase Auth, Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Progress Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.process);

        AlertDialog dialog = builder.create();


        // Sign Up Button

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!check_input())
                {
                    return;
                }
                dialog.show();
                auth.createUserWithEmailAndPassword
                                (binding.edEmail.getText().toString(), binding.edPassword.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    Users user = new Users(binding.edUserName.getText().toString(), binding.edEmail.getText().toString(),
                                            binding.edPassword.getText().toString());

                                    // Get the current user id
                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);
                                    // sign in with the new user and go to main activity
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);



                                    Toast.makeText(SignUpActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        binding.tvAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
    // check input have no empty
    public boolean check_input()
    {
        if(binding.edUserName.getText().toString().isEmpty())
        {
            binding.edUserName.setError("Enter your name");
            return false;
        }
        if(binding.edEmail.getText().toString().isEmpty())
        {
            binding.edEmail.setError("Enter your email");
            return false;
        }
        if(binding.edPassword.getText().toString().isEmpty())
        {
            binding.edPassword.setError("Enter your password");
            return false;
        }
        return true;
    }

}