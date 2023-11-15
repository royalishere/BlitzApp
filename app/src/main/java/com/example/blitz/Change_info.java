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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Change_info extends AppCompatActivity {

    ActivityChangeInfoBinding binding;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



        StorageReference reference = storage.getReference().child("profile_pictures").child(FirebaseAuth.getInstance().getUid());
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(binding.profileAvt);
            }
        });

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Change_info.this, ProfileFragment.class);
                startActivity(intent);
            }

        });




        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = binding.edStatus.getText().toString();
                String username = binding.edUserName.getText().toString();

                //if status and username is empty, then no update
                if(!status.isEmpty() || !username.isEmpty()){
                    HashMap<String, Object> obj = new HashMap<>();
                    obj.put("userName", username);
                    obj.put("status", status);

                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                            .updateChildren(obj);
                    Toast.makeText(Change_info.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                }
                else{
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
        binding.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,33);





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
}