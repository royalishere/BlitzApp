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

import com.example.blitz.GroupChatDetailActivity;
import com.example.blitz.Models.GroupMessage;
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

public class GroupChatApdapter extends RecyclerView.Adapter {
    ArrayList<GroupMessage> groupMessages;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;
    final String groupID = GroupChatDetailActivity.groupId;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    
    public GroupChatApdapter(ArrayList<GroupMessage> groupMessages, Context context) {
        this.groupMessages = groupMessages;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = View.inflate(context, R.layout.sample_sender, null);
            return new GroupChatApdapter.SenderViewHolder(view);
        } else {
            View view = View.inflate(context, R.layout.sample_receiver, null);
            return new GroupChatApdapter.ReceiverViewHolder(view);
        }
        
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupMessage message = groupMessages.get(position);
        if(holder.getClass() == GroupChatApdapter.SenderViewHolder.class)
        {
            ((SenderViewHolder) holder).sendername.setText(message.getSenderName());
        }
        else {
            ((ReceiverViewHolder) holder).recievername.setText(message.getSenderName());
        }
        if (message.getType().equals("text")) {
            if (holder.getClass() ==GroupChatApdapter.SenderViewHolder.class) {
                ((GroupChatApdapter.SenderViewHolder) holder).senderMsg.setText(message.getMessage());
                String formattedDate = DateFormat.format("dd.MM.yyyy  hh:mm", message.getTimestamp()).toString();
                ((GroupChatApdapter.SenderViewHolder) holder).senderTime.setText(formattedDate);

                //-------------
                //hide sender image
                ((GroupChatApdapter.SenderViewHolder) holder).senderImage.setVisibility(View.GONE);
                ((GroupChatApdapter.SenderViewHolder) holder).senderImage_del.setVisibility(View.GONE);

                //hide sender file
                ((GroupChatApdapter.SenderViewHolder) holder).senderFile.setVisibility(View.GONE);
                ((GroupChatApdapter.SenderViewHolder) holder).senderFile_del.setVisibility(View.GONE);

            } else {
                ((ReceiverViewHolder) holder).recievername.setText(message.getSenderName());
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverMsg.setText(message.getMessage());
                String formattedDate = DateFormat.format("dd.MM.yyyy  hh:mm", message.getTimestamp()).toString();
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverTime.setText(formattedDate);

                //-------------
                //hide receiver image
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverImage.setVisibility(View.GONE);
                //hide receiver file
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverFile.setVisibility(View.GONE);
            }
        }
        else if (message.getType().equals("image")) {
            if (holder.getClass() == GroupChatApdapter.SenderViewHolder.class) {
                //----------
                //hide sender text
                ((GroupChatApdapter.SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((GroupChatApdapter.SenderViewHolder) holder).senderTime.setVisibility(View.GONE);
                //hide sender file
                ((GroupChatApdapter.SenderViewHolder) holder).senderFile.setVisibility(View.GONE);
                ((GroupChatApdapter.SenderViewHolder)holder).senderFile_del.setVisibility(View.GONE);
                //----------
                ((GroupChatApdapter.SenderViewHolder) holder).senderImage.setVisibility(View.VISIBLE);
                StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
                if (message.getMessage().equals("UBiqi2OEkXIx8dLh2/I2HTYc04R42yIUpS/IwUGPwZg=")) //"this image was deleted"
                {
                    Picasso.get().load(R.drawable.image_deleted).into(((GroupChatApdapter.SenderViewHolder) holder).senderImage);
                    ((GroupChatApdapter.SenderViewHolder)holder).senderImage_del.setVisibility(View.GONE);
                    ((GroupChatApdapter.SenderViewHolder) holder).senderImage.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           Toast.makeText(context, "This image was deleted", Toast.LENGTH_SHORT).show();
                       }
                   });
                }

                else {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(((GroupChatApdapter.SenderViewHolder) holder).senderImage);
                        }
                    });


                    ((GroupChatApdapter.SenderViewHolder) holder).senderImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((GroupChatApdapter.SenderViewHolder) holder).senderImage.getContext().startActivity(intent);
                                }
                            });

                        }
                    });
                }


                //resize image : size of image /3
            } else {
                //----------
                //hide receiver text
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverTime.setVisibility(View.GONE);
                //hide receiver file
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverFile.setVisibility(View.GONE);
                //----------
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverImage.setVisibility(View.VISIBLE);
                StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
                if (message.getMessage().equals("UBiqi2OEkXIx8dLh2/I2HTYc04R42yIUpS/IwUGPwZg=")) //"this image was deleted"
                {
                    Picasso.get().load(R.drawable.image_deleted).into(((GroupChatApdapter.ReceiverViewHolder) holder).recieverImage);
                    ((GroupChatApdapter.ReceiverViewHolder) holder).recieverImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "This image was deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(((GroupChatApdapter.ReceiverViewHolder) holder).recieverImage);
                        }
                    });

                    ((GroupChatApdapter.ReceiverViewHolder) holder).recieverImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((GroupChatApdapter.ReceiverViewHolder) holder).recieverImage.getContext().startActivity(intent);
                                }
                            });

                        }
                    });
                }
                //resize image : size of image /3


            }
        }
        else if (message.getType().equals("pdf") || message.getType().equals("docx")) {
            if (holder.getClass() == GroupChatApdapter.SenderViewHolder.class) {
                //----------
                //hide sender text
                ((GroupChatApdapter.SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((GroupChatApdapter.SenderViewHolder) holder).senderTime.setVisibility(View.GONE);
                //----------
                ((GroupChatApdapter.SenderViewHolder) holder).senderImage.setVisibility(View.GONE);
                ((GroupChatApdapter.SenderViewHolder) holder).senderFile.setVisibility(View.VISIBLE);

                if (message.getMessage().equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")) //"this file was deleted"
                {
                    Picasso.get().load(R.drawable.file_deleted).into(((GroupChatApdapter.SenderViewHolder) holder).senderFile);
                    ((GroupChatApdapter.SenderViewHolder)holder).senderFile_del.setVisibility(View.GONE);
                    ((GroupChatApdapter.SenderViewHolder) holder).senderFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "This file was deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {

                    if (message.getType().equals("pdf")) {
                        Picasso.get().load(R.drawable.pdf).into(((GroupChatApdapter.SenderViewHolder) holder).senderFile);
                    }
                    if (message.getType().equals("docx")) {
                        Picasso.get().load(R.drawable.doc).into(((GroupChatApdapter.SenderViewHolder) holder).senderFile);
                    }
                    ((GroupChatApdapter.SenderViewHolder) holder).senderFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Document Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((GroupChatApdapter.SenderViewHolder) holder).senderImage.getContext().startActivity(intent);
                                }
                            });
                        }

                        //save file to local


                    });
                }


            } else {
                //----------
                //hide receiver text
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverTime.setVisibility(View.GONE);
                //----------
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverImage.setVisibility(View.GONE);
                ((GroupChatApdapter.ReceiverViewHolder) holder).recieverFile.setVisibility(View.VISIBLE);

                if (message.getMessage().equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")) //"this file was deleted"
                {
                    Picasso.get().load(R.drawable.file_deleted).into(((GroupChatApdapter.ReceiverViewHolder) holder).recieverFile);
                    ((GroupChatApdapter.ReceiverViewHolder) holder).recieverFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "This file was deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    if (message.getType().equals("pdf")) {
                        Picasso.get().load(R.drawable.pdf).into(((GroupChatApdapter.ReceiverViewHolder) holder).recieverFile);
                    }
                    if (message.getType().equals("docx")) {
                        Picasso.get().load(R.drawable.doc).into(((GroupChatApdapter.ReceiverViewHolder) holder).recieverFile);
                    }

                    ((GroupChatApdapter.ReceiverViewHolder) holder).recieverFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Document Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((GroupChatApdapter.ReceiverViewHolder) holder).recieverImage.getContext().startActivity(intent);
                                }
                            });


                        }
                    });
                }


            }
        }

        //delete message
        String senderId = FirebaseAuth.getInstance().getUid();
        //delete message
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.getClass() == GroupChatApdapter.SenderViewHolder.class) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(((GroupChatApdapter.SenderViewHolder) holder).senderImage.getContext());
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure you want to delete this message? You can't undo this action.");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.getReference().child("Groups").child(groupID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        GroupMessage message_db = ds.getValue(GroupMessage.class);
                                        //decrypt message_db
                                        String message_db_decrypt;
                                        try {
                                            message_db_decrypt = AESCrypt.decrypt("helloworld", message_db.getMessage());
                                            if (message_db_decrypt.equals(message.getMessage())) {

                                                String set_val = "this message was deleted";
                                                //encrypt set_val
                                                try {
                                                    set_val = AESCrypt.encrypt("helloworld", set_val);
                                                    ds.getRef().child("message").setValue(set_val);
                                                } catch (Exception e) {
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                }
                return true;
            }
        });

        if (holder.getClass() == GroupChatApdapter.SenderViewHolder.class) {
            //delete image
            holder.itemView.findViewById(R.id.senderImg_del).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //----------------
                    AlertDialog.Builder builder = new AlertDialog.Builder(((GroupChatApdapter.SenderViewHolder) holder).senderImage.getContext());
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure you want to delete this image? You can't undo this action.");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.getReference().child("chats").child(groupID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        GroupMessage message_db = ds.getValue(GroupMessage.class);
                                        //decrypt message_db
                                        String message_db_decrypt;
                                        if (message_db.getMessage().equals(message.getMessage())) {
                                            String set_val = "this image was deleted";
                                            //encrypt set_val
                                            try {
                                                set_val = AESCrypt.encrypt("helloworld", set_val);
                                                ds.getRef().child("message").setValue(set_val);
                                            } catch (Exception e) {
                                            }
                                        }

                                    }
                                }
                            });
                        }
                    });
                builder.setNegativeButton("Cancel", null);
                builder.show();
                //----------------
                return true;
                }
            });

            //delete file
            holder.itemView.findViewById(R.id.senderFile_del).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //----------------
                    AlertDialog.Builder builder = new AlertDialog.Builder(((GroupChatApdapter.SenderViewHolder) holder).senderImage.getContext());
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure you want to delete this file? You can't undo this action.");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            database.getReference().child("chats").child(groupID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        GroupMessage message_db = ds.getValue(GroupMessage.class);
                                        //decrypt message_db
                                        String message_db_decrypt;
                                        if (message_db.getMessage().equals(message.getMessage())) {
                                            String set_val = "this file was deleted";
                                            //encrypt set_val
                                            try {
                                                set_val = AESCrypt.encrypt("helloworld", set_val);
                                                ds.getRef().child("message").setValue(set_val);
                                            } catch (Exception e) {
                                            }
                                        }

                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.show();
                    //----------------
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return groupMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(groupMessages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMsg, recieverTime, recievername;
        ImageView recieverImage, recieverFile;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMsg = itemView.findViewById(R.id.receiverText);
            recieverTime = itemView.findViewById(R.id.receiverTime);
            recievername = itemView.findViewById(R.id.receivername);
            recieverImage = itemView.findViewById(R.id.receiverImage);
            recieverFile = itemView.findViewById(R.id.receiverFile);
            
            recievername.setVisibility(View.VISIBLE);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg, senderTime, sendername;
        ImageView senderImage, senderFile,senderFile_del,senderImage_del;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            sendername = itemView.findViewById(R.id.sendername);
            senderImage = itemView.findViewById(R.id.senderImage);
            senderFile = itemView.findViewById(R.id.senderFile);
            senderFile_del = itemView.findViewById(R.id.senderFile_del);
            senderImage_del = itemView.findViewById(R.id.senderImg_del);

            sendername.setVisibility(View.VISIBLE);
        }
    }
}
