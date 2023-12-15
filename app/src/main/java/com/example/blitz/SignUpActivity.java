package com.example.blitz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;

    boolean isShowPass = false;

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




        // Show/Hide Password
        binding.hideShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowPass)
                {
                    binding.edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPass = false;
                }
                else
                {
                    binding.edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isShowPass = true;
                }
            }
        });
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!check_input())
                {
                    return;
                }
                dialog.show();
                // encrypt password to store in database
                String passwordEncrypted = md5(binding.edPassword.getText().toString());


                // Create user with email and password (password encrypted by firebase )
                auth.createUserWithEmailAndPassword
                                (binding.edEmail.getText().toString(), binding.edPassword.getText().toString()).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    Users user = new Users(binding.edUserName.getText().toString(), binding.edEmail.getText().toString(),
                                            passwordEncrypted);

                                    // Get the current user id
                                    String id = task.getResult().getUser().getUid();
                                    database.getReference().child("Users").child(id).setValue(user);

                                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            String token = task.getResult();
                                            database.getReference().child("Users").child(id).child("token").setValue(token);
                                        }
                                    });

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
    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(String.format("%02X", messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}