package com.example.blitz;

import static android.app.PendingIntent.getActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.blitz.Adapters.ChatAdapter;
import com.example.blitz.Models.Message;
import com.example.blitz.Models.Users;
import com.example.blitz.databinding.ActivityConversationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {
        ActivityConversationBinding binding;
        FirebaseDatabase database;
        FirebaseAuth auth;
        FirebaseStorage storage;

        Users user_sender, user_receiver;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityConversationBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();
            storage = FirebaseStorage.getInstance();




            final String senderId = auth.getUid();
            String receiverId = getIntent().getStringExtra("userId");
            String userName = getIntent().getStringExtra("userName");
            String profilePic = getIntent().getStringExtra("profilePicture");

            binding.sendBtn.setEnabled(false);
            ImageViewCompat.setImageTintList(binding.sendBtn, ColorStateList.valueOf(getResources().getColor(R.color.grayBackground)));
            binding.usrname.setText(userName);
            Picasso.get().load(profilePic).placeholder(R.drawable.hacker).into(binding.profileImage);

            database.getReference().child("Users").child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user_receiver = snapshot.getValue(Users.class);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            database.getReference().child("Users").child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user_sender = snapshot.getValue(Users.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });



            binding.backArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            final ArrayList<Message> messages = new ArrayList<>();
            final ChatAdapter adapter = new ChatAdapter(messages, this);
            binding.chatRecyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.chatRecyclerView.setLayoutManager(layoutManager);

            final String senderRoom = senderId + receiverId;
            final String receiverRoom = receiverId + senderId;

            database.getReference().child("chats").child(senderRoom)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    messages.clear();
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        Message message = snapshot1.getValue(Message.class);
                                        messages.add(message);
                                    }
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

            binding.chatEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String messageTxt = binding.chatEditText.getText().toString();
                    if (messageTxt.isEmpty()) {
                        binding.sendBtn.setEnabled(false);
                        ImageViewCompat.setImageTintList(binding.sendBtn, ColorStateList.valueOf(getResources().getColor(R.color.grayBackground)));
                    } else {
                        binding.sendBtn.setEnabled(true);
                        ImageViewCompat.setImageTintList(binding.sendBtn, ColorStateList.valueOf(getResources().getColor(R.color.indicator)));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            binding.sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String messageTxt = binding.chatEditText.getText().toString();
                    final Message message = new Message(senderId, messageTxt);
                    message.setTimestamp(new Date().getTime());
                    binding.chatEditText.setText("");
                    database.getReference().child("chats").child(senderRoom).push().setValue(message)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    if(!senderRoom.equals(receiverRoom)) {
                                        database.getReference().child("chats").child(receiverRoom).push().setValue(message)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        FCMSend.pushNotification(ChatDetailActivity.this,
                                                                user_receiver.getToken(),
                                                                "You have a new message",
                                                                user_sender.getUserName()+": "+message.getMessage());


                                                    }
                                                });
                                    }
                                }
                            });
                }
            });
            binding.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //show dialog to edit profile
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatDetailActivity.this);
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_profile_otheruser, null);

                    //-------------------
                    //night mode
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                        LinearLayout LL1 = (LinearLayout) dialogView.findViewById(R.id.LL1);
                        LL1.setBackground(getResources().getDrawable(R.drawable.dark_background));

                        ConstraintLayout CL_topbackground = (ConstraintLayout) dialogView.findViewById(R.id.CL_topbackground);
                        CL_topbackground.setBackground(getResources().getDrawable(R.drawable.dark_background));





                    }
                    else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                        LinearLayout LL1 = (LinearLayout) dialogView.findViewById(R.id.LL1);
                        LL1.setBackground(getResources().getDrawable(R.drawable.white_background));

                        ConstraintLayout CL_topbackground = (ConstraintLayout) dialogView.findViewById(R.id.CL_topbackground);
                        CL_topbackground.setBackground(getResources().getDrawable(R.drawable.top_background));


                    }
                    else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    TextView tvUserName = dialogView.findViewById(R.id.tvUserName);
                    TextView tvStatus = dialogView.findViewById(R.id.tvStatus);
                    TextView tvAddress = dialogView.findViewById(R.id.tvAddress);
                    TextView tvMobile = dialogView.findViewById(R.id.tvMobile);
                    TextView tvEmail = dialogView.findViewById(R.id.tvEmail);
                    ImageView avt = dialogView.findViewById(R.id.avt);

                    //get the profile picture from storage
                    StorageReference reference = storage.getReference().child("profile_pictures").child(receiverId);
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(avt);
                        }
                    });

                    database.getReference().child("Users").child(receiverId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                //get the username and status from database
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Users users = snapshot.getValue(Users.class);
                                    tvUserName.setText(users.getUserName());
                                    tvStatus.setText(users.getStatus());
                                    tvAddress.setText(users.getAddress());
                                    tvMobile.setText(users.getMobile());
                                    tvEmail.setText(users.getMail());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });


                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    }


                };
            });
        }
}
