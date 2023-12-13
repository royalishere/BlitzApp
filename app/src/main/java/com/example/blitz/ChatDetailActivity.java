package com.example.blitz;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {
        ActivityConversationBinding binding;
        FirebaseDatabase database;
        FirebaseAuth auth;

        Users user_sender, user_receiver;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityConversationBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();

            //-----------------------------------------------------
            //Device id
//            FirebaseMessaging.getInstance().getToken()
//                    .addOnCompleteListener(new OnCompleteListener<String>() {
//                        @Override
//                        public void onComplete(@NonNull Task<String> task) {
//                            if (!task.isSuccessful()) {
//
//                                return;
//                            }
//
//                            String token = task.getResult();
//                            System.out.println("Token: " + token);
//
//                        }
//                    });
            //Create notification service
            //-----------------------------------------------------

            final String senderId = auth.getUid();
            String receiverId = getIntent().getStringExtra("userId");
            String userName = getIntent().getStringExtra("userName");
            String profilePic = getIntent().getStringExtra("profilePic");

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
        }
//    public  void makeNotification(){
//        String channelId = "CHANNEL_ID_NOTIFICATION";
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),channelId);
//        builder.setSmallIcon(R.drawable.notifications_24)
//                .setContentTitle("Notification Title")
//                .setContentText("Notification Text")
//                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        Intent intent = new Intent(getApplicationContext(),getApplicationContext().getClass());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("data","Some value to be pased here");
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_MUTABLE);
//        builder.setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
//            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
//            if(notificationChannel == null){
//                int importance = NotificationManager.IMPORTANCE_DEFAULT;
//                notificationChannel = new NotificationChannel(channelId,"Some description",importance);
//                notificationChannel.setLightColor(Color.GREEN);
//                notificationChannel.enableVibration(true);
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
//        notificationManager.notify(0,builder.build());
//    }
}
