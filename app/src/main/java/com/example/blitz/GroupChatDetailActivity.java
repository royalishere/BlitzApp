package com.example.blitz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.blitz.Adapters.GroupChatApdapter;
import com.example.blitz.Models.GroupMessage;
import com.example.blitz.Models.Users;
import com.example.blitz.databinding.ActivityGroupConversationBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.scottyab.aescrypt.AESCrypt;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatDetailActivity extends AppCompatActivity {
    ActivityGroupConversationBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseStorage storage;
    String checker = "", myUrl = "";
    StorageTask uploadTask;
    Uri fileUri;
    Users sender;
    public static String groupId = "";
    String groupName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Progress Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatDetailActivity.this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.process);
        AlertDialog dialog = builder.create();

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");

        binding.sendBtn.setEnabled(false);
        ImageViewCompat.setImageTintList(binding.sendBtn, ColorStateList.valueOf(getResources().getColor(R.color.grayBackground)));
        binding.groupname.setText(groupName);

        // get user info
        database.getReference().child("Users").child(auth.getUid()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                sender = task.getResult().getValue(Users.class);
                sender.setUserId(auth.getUid());
            }
        });

        // back button
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // get messages
        final ArrayList<GroupMessage> messages = new ArrayList<>();
        final GroupChatApdapter adapter = new GroupChatApdapter(messages, this);
        binding.chatRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        database.getReference().child("Groups").child(groupId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    GroupMessage message = snapshot1.getValue(GroupMessage.class);
                    //----------
                    if (message.getType().equals("text")) {//decrypt message
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
                    else if (message.getType().equals("docx")) {
                        messages.add(message);
                    }

                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        dialog.dismiss();

        // text changed when typing handler
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

        // send message button handler
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

                final GroupMessage message = new GroupMessage(sender.getUserId(), sender.getUserName(), messageTxt);
                message.setTimestamp(new Date().getTime());
                message.setType("text");

                binding.chatEditText.setText("");
                database.getReference().child("Groups").child(groupId).child("Messages").push().setValue(message);
            }
        });
        //send file button handler
        binding.btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[] = new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "Files doc"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatDetailActivity.this);
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
                                //send docx
                                checker = "docx";

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
            AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatDetailActivity.this);
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
                DatabaseReference user_sender_message_key = database.getReference().child("Groups").child(groupId).child("Messages").push();
                final String messagePushId_sender = user_sender_message_key.getKey();

                StorageReference filePath = storageReference.child(messagePushId_sender);

                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String messageTxt = messagePushId_sender;
                            //make message
                            final GroupMessage message = new GroupMessage(sender.getUserId(),sender.getUserName(), messageTxt);
                            message.setTimestamp(new Date().getTime());
                            message.setType(checker);

                            database.getReference().child("Groups").child(groupId).child("Messages").push().setValue(message);
                            dialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(GroupChatDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                DatabaseReference user_sender_message_key = database.getReference().child("Groups").child(groupId).child("Messages").push();

                final String messagePushId_sender = user_sender_message_key.getKey();
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
                            final GroupMessage message = new GroupMessage(sender.getUserId(), sender.getUserName(), messageTxt);
                            message.setTimestamp(new Date().getTime());
                            message.setType("image");

                            database.getReference().child("Groups").child(groupId).child("Messages").push().setValue(message);
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