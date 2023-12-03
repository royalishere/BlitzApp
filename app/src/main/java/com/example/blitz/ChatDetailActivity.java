package com.example.blitz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.blitz.Adapters.ChatAdapter;
import com.example.blitz.Models.Message;
import com.example.blitz.databinding.ActivityConversationBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {
        ActivityConversationBinding binding;
        FirebaseDatabase database;
        FirebaseAuth auth;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityConversationBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();

            final String senderId = auth.getUid();
            String receiverId = getIntent().getStringExtra("userId");
            String userName = getIntent().getStringExtra("userName");
            String profilePic = getIntent().getStringExtra("profilePic");

            binding.usrname.setText(userName);
            Picasso.get().load(profilePic).placeholder(R.drawable.hacker).into(binding.profileImage);

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

                                                    }
                                                });
                                    }
                                }
                            });
                }
            });
        }
}
