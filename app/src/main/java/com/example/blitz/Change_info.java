package com.example.blitz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.blitz.Fragment.ProfileFragment;
import com.example.blitz.Models.Users;
import com.example.blitz.databinding.ActivityChangeInfoBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;

public class Change_info extends AppCompatActivity {

    ActivityChangeInfoBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        //get the profile picture from database
        StorageReference reference = storage.getReference().child("profile_pictures").child(FirebaseAuth.getInstance().getUid());
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(binding.profileAvt);
            }
        });

        // back button
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Change_info.this, ProfileFragment.class);
                startActivity(intent);
            }

        });

        // save button
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the status and username from edit text
                String status = binding.edStatus.getText().toString();
                String username = binding.edUserName.getText().toString();

                //if status and username is empty, then no update
                if (!status.isEmpty() || !username.isEmpty()) {
                    HashMap<String, Object> obj = new HashMap<>();
                    obj.put("userName", username);
                    obj.put("status", status);

                    //update the username and status in database
                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                            .updateChildren(obj);
                    Toast.makeText(Change_info.this, "Profile Updated", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(Change_info.this, "Please enter username and status", Toast.LENGTH_SHORT).show();
                }


            }
        });


        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    //get the username and status from database
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        Picasso.get().load(users.getProfilePic())
                                .placeholder(R.drawable.user_circle_svgrepo_com)
                                .into(binding.profileAvt);

                        binding.edStatus.setText(users.getStatus());
                        binding.edUserName.setText(users.getUserName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // change profile picture button
        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 33);

            }
        });

        // change password button
        binding.btnChangePass.setOnClickListener(new View.OnClickListener() {
            String password ;
            @Override
            public void onClick(View view) {
                // if login with google account, cannot change password
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Change_info.this);
                if (acct != null) {
                    Toast.makeText(Change_info.this,
                            "Account is logged in with a Google account, please change the Google account password", Toast.LENGTH_SHORT).show();
                }
                // if login with email and password
                else
                {
                    if (!check_input()) {
                        return;
                    }
                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                //get the username and status from database
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Users users = snapshot.getValue(Users.class);
                                    password = users.getPassword();
                                    if (password.equals(binding.edCurrentPass.getText().toString())) {
                                        auth.getCurrentUser().updatePassword(binding.edNewPass.getText().toString());
                                        String newPassword = binding.edNewPass.getText().toString();
                                        String confirmPassword = binding.edConfirmPass.getText().toString();


                                        if (newPassword.equals(confirmPassword)) {
                                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("password")
                                                    .setValue(newPassword);
                                            Toast.makeText(Change_info.this, "Password is changed", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Change_info.this, "Password does not match", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data.getData() != null)) {

            Uri sFile = data.getData();

            binding.profileAvt.setImageURI(sFile);

            final StorageReference reference = storage.getReference().child("profile_pictures")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("profilePicture")
                                            .setValue(uri.toString());
                                    //set the profile picture in the profile fragment
                                    Picasso.get().load(uri).into(binding.profileAvt);
                                   Toast.makeText(Change_info.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            });
        }
    }


    // check labels are empty or not
    private boolean check_input() {
        if(binding.edCurrentPass.getText().toString().isEmpty())
        {
            binding.edCurrentPass.setError("Enter Current Password");
            return false;
        }
        if(binding.edNewPass.getText().toString().isEmpty())
        {
            binding.edNewPass.setError("Enter New Password");
            return false;
        }
        if(binding.edConfirmPass.getText().toString().isEmpty())
        {
            binding.edConfirmPass.setError("Enter Confirm Password");
            return false;
        }
        return true;
    }


}



