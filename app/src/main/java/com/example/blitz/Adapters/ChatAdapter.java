package com.example.blitz.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.ChatDetailActivity;
import com.example.blitz.Models.GroupMessage;
import com.example.blitz.Models.Message;
import com.example.blitz.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.scottyab.aescrypt.AESCrypt;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<Message> messages;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    String senderRoom, receiverRoom;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public ChatAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = View.inflate(context, R.layout.sample_sender, null);
            return new SenderViewHolder(view);
        } else {
            View view = View.inflate(context, R.layout.sample_receiver, null);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).senderMsg.setVisibility(View.VISIBLE);
            ((SenderViewHolder) holder).senderTime.setVisibility(View.VISIBLE);
            ((SenderViewHolder) holder).senderImage.setVisibility(View.GONE);
            ((SenderViewHolder) holder).senderFile.setVisibility(View.GONE);
            ((SenderViewHolder) holder).senderFile_del.setVisibility(View.VISIBLE);
            ((SenderViewHolder) holder).senderImage_del.setVisibility(View.VISIBLE);
        } else {
            ((ReceiverViewHolder) holder).recieverMsg.setVisibility(View.VISIBLE);
            ((ReceiverViewHolder) holder).recieverTime.setVisibility(View.VISIBLE);
            ((ReceiverViewHolder) holder).recieverImage.setVisibility(View.GONE);
            ((ReceiverViewHolder) holder).recieverFile.setVisibility(View.GONE);
        }
        super.onViewRecycled(holder);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        // retreive roomID
        String senderId = FirebaseAuth.getInstance().getUid();
        String receiverId = message.getuId()== "myself" ? senderId:message.getuId();
        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

        switch (message.getType()) {
            case "text":
                String formattedDate = DateFormat.format("dd.MM.yyyy  hh:mm", message.getTimestamp()).toString();
                if (holder.getClass() == SenderViewHolder.class) {
                    ((SenderViewHolder) holder).senderMsg.setText(message.getMessage());
                    ((SenderViewHolder) holder).senderTime.setText(formattedDate);
                    //delete message
                    ((SenderViewHolder) holder).senderMsg.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ((SenderViewHolder) holder).handleDeleteText(message.getMessage());
                            return true;
                        }
                    });
                } else {
                    ((ReceiverViewHolder) holder).recieverMsg.setText(message.getMessage());
                    ((ReceiverViewHolder) holder).recieverTime.setText(formattedDate);
                }
                break;
            case "image":
                if (holder.getClass() == SenderViewHolder.class) {
                    ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                    ((SenderViewHolder) holder).senderTime.setVisibility(View.GONE);
                    ((SenderViewHolder) holder).senderImage.setVisibility(View.VISIBLE);
                    ((SenderViewHolder) holder).senderImage_del.setVisibility(View.VISIBLE);
                    ((SenderViewHolder) holder).handleImageClick(message.getMessage());
                    //delete image
                    ((SenderViewHolder) holder).senderImage_del.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ((SenderViewHolder) holder).handleDeleteImage(message.getMessage());
                            return true;
                        }
                    });
                }
                else {
                    ((ReceiverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                    ((ReceiverViewHolder) holder).recieverTime.setVisibility(View.GONE);
                    ((ReceiverViewHolder) holder).recieverImage.setVisibility(View.VISIBLE);
                    ((ReceiverViewHolder) holder).handleImageClick(message.getMessage());
                }
                break;
            case "pdf": case "docx":
                if (holder.getClass() == SenderViewHolder.class) {
                    ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                    ((SenderViewHolder) holder).senderTime.setVisibility(View.GONE);
                    ((SenderViewHolder) holder).senderFile.setVisibility(View.VISIBLE);
                    ((SenderViewHolder) holder).senderFile_del.setVisibility(View.VISIBLE);
                    ((SenderViewHolder) holder).handleFileClick(message.getMessage(), message.getType());
                    //delete file
                    ((SenderViewHolder) holder).senderFile_del.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ((SenderViewHolder) holder).handleDeleteFile(message.getMessage());
                            return true;
                        }
                    });
                }
                else {
                    ((ReceiverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                    ((ReceiverViewHolder) holder).recieverTime.setVisibility(View.GONE);
                    ((ReceiverViewHolder) holder).recieverFile.setVisibility(View.VISIBLE);
                    ((ReceiverViewHolder) holder).handleFileClick(message.getMessage(), message.getType());
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(!messages.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMsg, recieverTime;
        ImageView recieverImage, recieverFile;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMsg = itemView.findViewById(R.id.receiverText);
            recieverTime = itemView.findViewById(R.id.receiverTime);
            recieverImage = itemView.findViewById(R.id.receiverImage);
            recieverFile = itemView.findViewById(R.id.receiverFile);

            recieverImage.setVisibility(View.GONE);
            recieverFile.setVisibility(View.GONE);
        }

        public void handleImageClick(String message) {
            if (message.equals("UBiqi2OEkXIx8dLh2/I2HTYc04R42yIUpS/IwUGPwZg=")) //"this image was deleted"
            {
                Picasso.get().load(R.drawable.image_deleted).into(recieverImage);
                recieverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "This image was deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                StorageReference reference = storage.getReference().child("Image Files").child(message);
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(recieverImage);
                    }
                });
                recieverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StorageReference reference = storage.getReference().child("Image Files").child(message);
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                recieverImage.getContext().startActivity(intent);
                            }});
                    }});
            }
        }

        public void handleFileClick(String message, String type) {
            if (message.equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")) //"this file was deleted"
            {
                Picasso.get().load(R.drawable.file_deleted).into(recieverFile);
                recieverFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "This file was deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                StorageReference reference = storage.getReference().child("Document Files").child(message);
                if (type.equals("pdf")) {
                    Picasso.get().load(R.drawable.pdf).into(recieverFile);
                }
                else { //docx
                    Picasso.get().load(R.drawable.doc).into(recieverFile);
                }
                recieverFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                recieverFile.getContext().startActivity(intent);
                            }});
                    }});
            }
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime;
        ImageView senderImage, senderFile,senderFile_del,senderImage_del;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            senderImage = itemView.findViewById(R.id.senderImage);
            senderFile = itemView.findViewById(R.id.senderFile);
            senderFile_del = itemView.findViewById(R.id.senderFile_del);
            senderImage_del = itemView.findViewById(R.id.senderImg_del);

            senderImage.setVisibility(View.GONE);
            senderFile.setVisibility(View.GONE);
            senderImage_del.setVisibility(View.GONE);
            senderFile_del.setVisibility(View.GONE);
        }

        public void handleImageClick(String message) {
            if (message.equals("UBiqi2OEkXIx8dLh2/I2HTYc04R42yIUpS/IwUGPwZg=")) //"this image was deleted"
            {
                Picasso.get().load(R.drawable.image_deleted).into(senderImage);
                senderImage_del.setVisibility(View.GONE);
                senderImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "This image was deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                StorageReference reference = storage.getReference().child("Image Files").child(message);
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(senderImage);
                    }
                });
                senderImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                senderImage.getContext().startActivity(intent);
                            }
                        });
                    }
                });
            }
        }

        public void handleFileClick(String message, String type) {
            if (message.equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")) //"this file was deleted"
            {
                Picasso.get().load(R.drawable.file_deleted).into(senderFile);
                senderFile_del.setVisibility(View.GONE);
                senderFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "This file was deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else {
                StorageReference reference = storage.getReference().child("Document Files").child(message);
                if (type.equals("pdf")) {
                    Picasso.get().load(R.drawable.pdf).into(senderFile);
                }
                else { //docx
                    Picasso.get().load(R.drawable.doc).into(senderFile);
                }
                senderFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                senderFile.getContext().startActivity(intent);
                            }
                        });
                    }
                });
            }
        }

        public void handleDeleteText(String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(senderImage.getContext());
            builder.setTitle("Delete");
            builder.setMessage("Are you sure you want to delete this message? You can't undo this action.");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    database.getReference().child("chats").child(senderRoom).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message_db = ds.getValue(Message.class);
                                //decrypt message_db
                                String message_db_decrypt;
                                try {
                                    message_db_decrypt = AESCrypt.decrypt("helloworld", message_db.getMessage());
                                    if (message_db_decrypt.equals(message)) {

                                        String set_val = "this message was deleted";
                                        //encrypt set_val
                                        try {
                                            set_val = AESCrypt.encrypt("helloworld", set_val);
                                            ds.getRef().child("message").setValue(set_val);
                                        } catch (Exception e) {}
                                    }
                                } catch (Exception e) {}
                            }
                        }
                    });
                    database.getReference().child("chats").child(receiverRoom).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message_db = ds.getValue(Message.class);
                                //decrypt message_db
                                String message_db_decrypt;
                                try {
                                    message_db_decrypt = AESCrypt.decrypt("helloworld", message_db.getMessage());
                                    if (message_db_decrypt.equals(message)) {
                                        String set_val = "this message was deleted";
                                        //encrypt set_val
                                        try {
                                            set_val = AESCrypt.encrypt("helloworld", set_val);
                                            ds.getRef().child("message").setValue(set_val);
                                        } catch (Exception e) {}
                                    }
                                } catch (Exception e) {}
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

        public void handleDeleteImage(String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(senderImage.getContext());
            builder.setTitle("Delete");
            builder.setMessage("Are you sure you want to delete this image? You can't undo this action.");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    database.getReference().child("chats").child(senderRoom).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message_db = ds.getValue(Message.class);
                                if (message_db.getMessage().equals(message)) {
                                    String set_val = "this image was deleted";
                                    //encrypt set_val
                                    try {
                                        set_val = AESCrypt.encrypt("helloworld", set_val);
                                        ds.getRef().child("message").setValue(set_val);
                                    } catch (Exception e) {}
                                }
                            }
                        }
                    });
                    database.getReference().child("chats").child(receiverRoom).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message_db = ds.getValue(Message.class);
                                if (message_db.getMessage().equals(message)) {
                                    String set_val = "this image was deleted";
                                    try {
                                        set_val = AESCrypt.encrypt("helloworld", set_val);
                                        ds.getRef().child("message").setValue(set_val);
                                    } catch (Exception e) {}
                                }
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }

        public void handleDeleteFile(String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(senderImage.getContext());
            builder.setTitle("Delete");
            builder.setMessage("Are you sure you want to delete this file? You can't undo this action.");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    database.getReference().child("chats").child(senderRoom).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message_db = ds.getValue(Message.class);
                                //decrypt message_db
                                String message_db_decrypt;
                                if (message_db.getMessage().equals(message)) {
                                    String set_val = "this file was deleted";
                                    try {
                                        set_val = AESCrypt.encrypt("helloworld", set_val);
                                        ds.getRef().child("message").setValue(set_val);
                                    } catch (Exception e) {}
                                }
                            }
                        }
                    });
                    database.getReference().child("chats").child(receiverRoom).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Message message_db = ds.getValue(Message.class);
                                if (message_db.getMessage().equals(message)) {
                                    String set_val = "this file was deleted";
                                    try {
                                        set_val = AESCrypt.encrypt("helloworld", set_val);
                                        ds.getRef().child("message").setValue(set_val);
                                    } catch (Exception e) {}
                                }
                            }
                        }
                    });
                }
            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }
}
