package com.example.blitz;

import static android.app.PendingIntent.getActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.Adapters.ChatAdapter;
import com.example.blitz.Models.Message;
import com.example.blitz.Models.Users;
import com.example.blitz.databinding.ActivityConversationBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.scottyab.aescrypt.AESCrypt;
import com.squareup.picasso.Picasso;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {
        ActivityConversationBinding binding;
        FirebaseDatabase database;
        FirebaseAuth auth;
        FirebaseStorage storage;

        Users user_sender, user_receiver;

        String checker="",myUrl="";

        StorageTask uploadTask;

        Uri fileUri;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityConversationBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            database = FirebaseDatabase.getInstance();
            auth = FirebaseAuth.getInstance();
            storage = FirebaseStorage.getInstance();

            // Progress Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatDetailActivity.this);
            builder.setCancelable(false); // if you want user to wait for some process to finish,
            builder.setView(R.layout.process);
            AlertDialog dialog = builder.create();




            final String senderId = auth.getUid();
            String receiverId = getIntent().getStringExtra("userId");
            String userName = getIntent().getStringExtra("userName");
            String profilePic = getIntent().getStringExtra("profilePicture");

            binding.sendBtn.setEnabled(false);
            ImageViewCompat.setImageTintList(binding.sendBtn, ColorStateList.valueOf(getResources().getColor(R.color.grayBackground)));
            binding.usrname.setText(userName);
            Picasso.get().load(profilePic).placeholder(R.drawable.hacker).into(binding.profileImage);


            //get user info
            database.getReference().child("Users").child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user_receiver = snapshot.getValue(Users.class);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            //get user info
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
                                        //----------
                                        if (message.getType().equals("text")) {
                                            //decrypt message
                                            String messageTxt = message.getMessage();
                                            try {
                                                messageTxt = AESCrypt.decrypt(getString(R.string.key_encrypt), messageTxt);
                                                message.setMessage(messageTxt);
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                            messages.add(message);
                                        }
                                        else if (message.getType().equals("image")) {
                                            messages.add(message);
                                        }
                                        else if (message.getType().equals("pdf")) {
                                            messages.add(message);
                                        }
                                        else if (message.getType().equals("doc")) {
                                            messages.add(message);
                                        }

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
                    //encrypt message
                    try {
                        messageTxt = AESCrypt.encrypt(getString(R.string.key_encrypt),messageTxt);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    final Message message = new Message(senderId, messageTxt);
                    message.setTimestamp(new Date().getTime());
                    message.setType("text");

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
                                                        //make body of notification
                                                        String body;
                                                        try{
                                                            body = AESCrypt.decrypt(getString(R.string.key_encrypt),message.getMessage());
                                                        }
                                                        catch (Exception e)
                                                        {
                                                            throw new RuntimeException(e);
                                                        }
                                                        body = user_sender.getUserName()+": "+body;
                                                        try{
                                                            body = AESCrypt.encrypt(getString(R.string.key_encrypt),body);
                                                        }
                                                        catch (Exception e)
                                                        {
                                                            throw new RuntimeException(e);
                                                        }
                                                        FCMSend.pushNotification(ChatDetailActivity.this,
                                                                user_receiver.getToken(),
                                                                "You have a new message",
                                                                body);


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



            //send file
                    binding.btnSendFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CharSequence options[] = new CharSequence[]
                                    {
                                            "Images",
                                            "PDF Files",
                                            "Files doc"
                                    };
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatDetailActivity.this);
                            builder.setTitle("Select the file");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            checker = "image";

                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            intent.setType("image/*");
                                            startActivityForResult(intent.createChooser(intent, "Select Image"), 438);

                                            break;
                                        case 1:
                                            //send pdf
                                            checker = "pdf";

                                            Intent intent1 = new Intent();
                                            intent1.setAction(Intent.ACTION_GET_CONTENT);
                                            intent1.setType("application/pdf");
                                            startActivityForResult(intent1.createChooser(intent1, "Select PDF File"), 438);


                                            break;
                                        case 2:
                                            //send doc
                                            checker = "doc";

                                            Intent intent2 = new Intent();
                                            intent2.setAction(Intent.ACTION_GET_CONTENT);
                                            intent2.setType("application/msword");
                                            startActivityForResult(intent2.createChooser(intent2, "Select Word File"), 438);


                                            break;
                                    }
                                }
                            });
                            builder.show();
                        }
                    });
        }
        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
            {
                // Progress Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatDetailActivity.this);
                builder.setCancelable(false); // if you want user to wait for some process to finish,
                builder.setTitle("Sending file")
                        .setMessage("Please wait...")
                        .setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();

                fileUri = data.getData();
                if (!checker.equals("image"))
                {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
                    //get uid
                    final String senderId = auth.getUid();
                    String receiverId = getIntent().getStringExtra("userId");

                    //get chat room
                    final String senderRoom = senderId + receiverId;
                    final String receiverRoom = receiverId + senderId;

                    DatabaseReference user_sender_message_key = database.getReference().child("chats").child(senderRoom).push();
                    DatabaseReference user_receiver_message_key = database.getReference().child("chats").child(receiverRoom).push();

                    final String messagePushId_sender = user_sender_message_key.getKey();
                    final String messagePushId_receiver = user_receiver_message_key.getKey();

                    StorageReference filePath = storageReference.child(messagePushId_sender );

                    filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                String messageTxt = messagePushId_sender;
                                //make message
                                final Message message = new Message(senderId, messageTxt);
                                message.setTimestamp(new Date().getTime());
                                message.setType(checker);

                                database.getReference().child("chats").child(senderRoom).push().setValue(message)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if (!senderRoom.equals(receiverRoom)) {
                                                    database.getReference().child("chats").child(receiverRoom).push().setValue(message)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    //make body of notification
                                                                    String body;

                                                                    body = user_sender.getUserName() + ": sent a file";
                                                                    try {
                                                                        body = AESCrypt.encrypt(getString(R.string.key_encrypt), body);
                                                                    } catch (Exception e) {
                                                                        throw new RuntimeException(e);
                                                                    }
                                                                    FCMSend.pushNotification(ChatDetailActivity.this,
                                                                            user_receiver.getToken(),
                                                                            "You have a new message",
                                                                            body);


                                                                }
                                                            });
                                                }
                                            }
                                        });

                                dialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(ChatDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double p = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            builder.setTitle("Sending file")
                                    .setMessage("Please wait..."+(int)p+"%")
                                    .setCancelable(false);
                            dialog.show();

                        }
                    });
                }
                else if (checker.equals("image")) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
                    //get uid
                    final String senderId = auth.getUid();
                    String receiverId = getIntent().getStringExtra("userId");

                    //get chat room
                    final String senderRoom = senderId + receiverId;
                    final String receiverRoom = receiverId + senderId;

                    DatabaseReference user_sender_message_key = database.getReference().child("chats").child(senderRoom).push();
                    DatabaseReference user_receiver_message_key = database.getReference().child("chats").child(receiverRoom).push();

                    final String messagePushId_sender = user_sender_message_key.getKey();
                    final String messagePushId_receiver = user_receiver_message_key.getKey();

                    StorageReference filePath = storageReference.child(messagePushId_sender );

                    uploadTask = filePath.putFile(fileUri);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = (Uri) task.getResult();
                                myUrl = downloadUrl.toString();

                                //make message
                                String messageTxt = messagePushId_sender;
//
                                final Message message = new Message(senderId, messageTxt);
                                message.setTimestamp(new Date().getTime());
                                message.setType("image");

                                database.getReference().child("chats").child(senderRoom).push().setValue(message)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if (!senderRoom.equals(receiverRoom)) {
                                                    database.getReference().child("chats").child(receiverRoom).push().setValue(message)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    //make body of notification
                                                                    String body;

                                                                    body = user_sender.getUserName() + ": sent a photo" ;
                                                                    try {
                                                                        body = AESCrypt.encrypt(getString(R.string.key_encrypt), body);
                                                                    } catch (Exception e) {
                                                                        throw new RuntimeException(e);
                                                                    }
                                                                    FCMSend.pushNotification(ChatDetailActivity.this,
                                                                            user_receiver.getToken(),
                                                                            "You have a new message",
                                                                            body);


                                                                }
                                                            });
                                                }
                                            }
                                        });
                                dialog.dismiss();
                            }


                        }
                    });
                }

            }}
    public interface OnListItemClick {
        void onClick(View view, int position);
    }
}