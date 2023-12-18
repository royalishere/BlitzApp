package com.example.blitz.Adapters;

import static android.content.Intent.getIntent;
import static android.provider.Settings.System.getString;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.ChatDetailActivity;
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
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<Message> messages;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ChatDetailActivity.OnListItemClick onListItemClick;

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.getType().equals("text")) {
            if (holder.getClass() == SenderViewHolder.class) {
                ((SenderViewHolder) holder).senderMsg.setText(message.getMessage());
                String formattedDate = DateFormat.format("dd.MM.yyyy  hh:mm", message.getTimestamp()).toString();
                ((SenderViewHolder) holder).senderTime.setText(formattedDate);

                //-------------
                //hide sender image
                ((SenderViewHolder) holder).senderImage.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderImage_del.setVisibility(View.GONE);

                //hide sender file
                ((SenderViewHolder) holder).senderFile.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderFile_del.setVisibility(View.GONE);

            } else {
                ((ReceiverViewHolder) holder).recieverMsg.setText(message.getMessage());
                String formattedDate = DateFormat.format("dd.MM.yyyy  hh:mm", message.getTimestamp()).toString();
                ((ReceiverViewHolder) holder).recieverTime.setText(formattedDate);

                //-------------
                //hide receiver image
                ((ReceiverViewHolder) holder).recieverImage.setVisibility(View.GONE);
                //hide receiver file
                ((ReceiverViewHolder) holder).recieverFile.setVisibility(View.GONE);
            }
        }
        if (message.getType().equals("image")) {
            if (holder.getClass() == SenderViewHolder.class) {
                //----------
                //hide sender text
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderTime.setVisibility(View.GONE);
                //hide sender file
                ((SenderViewHolder) holder).senderFile.setVisibility(View.GONE);
                ((SenderViewHolder)holder).senderFile_del.setVisibility(View.GONE);
                //----------
                ((SenderViewHolder) holder).senderImage.setVisibility(View.VISIBLE);
                StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
                if (message.getMessage().equals("UBiqi2OEkXIx8dLh2/I2HTYc04R42yIUpS/IwUGPwZg=")) //"this image was deleted"
                {
                    Picasso.get().load(R.drawable.image_deleted).into(((SenderViewHolder) holder).senderImage);
                    ((SenderViewHolder)holder).senderImage_del.setVisibility(View.GONE);
                    ((SenderViewHolder) holder).senderImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "This image was deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                    );
                }

                else {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(((SenderViewHolder) holder).senderImage);
                        }
                    });


                    ((SenderViewHolder) holder).senderImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((SenderViewHolder) holder).senderImage.getContext().startActivity(intent);
                                }
                            });

                        }
                    });
                }


                //resize image : size of image /3


            } else {
                //----------
                //hide receiver text
                ((ReceiverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).recieverTime.setVisibility(View.GONE);
                //hide receiver file
                ((ReceiverViewHolder) holder).recieverFile.setVisibility(View.GONE);
                //----------
                ((ReceiverViewHolder) holder).recieverImage.setVisibility(View.VISIBLE);
                StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
                if (message.getMessage().equals("UBiqi2OEkXIx8dLh2/I2HTYc04R42yIUpS/IwUGPwZg=")) //"this image was deleted"
                {
                    Picasso.get().load(R.drawable.image_deleted).into(((ReceiverViewHolder) holder).recieverImage);
                    ((ReceiverViewHolder) holder).recieverImage.setOnClickListener(new View.OnClickListener() {
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
                            Picasso.get().load(uri).into(((ReceiverViewHolder) holder).recieverImage);
                        }
                    });

                    ((ReceiverViewHolder) holder).recieverImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((ReceiverViewHolder) holder).recieverImage.getContext().startActivity(intent);
                                }
                            });

                        }
                    });
                }
                //resize image : size of image /3


            }
        } else if (message.getType().equals("pdf") || message.getType().equals("docx")) {
            if (holder.getClass() == SenderViewHolder.class) {
                //----------
                //hide sender text
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderTime.setVisibility(View.GONE);
                //----------
                ((SenderViewHolder) holder).senderImage.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderFile.setVisibility(View.VISIBLE);

                if (message.getMessage().equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")) //"this file was deleted"
                {
                    Picasso.get().load(R.drawable.file_deleted).into(((SenderViewHolder) holder).senderFile);
                    ((SenderViewHolder)holder).senderFile_del.setVisibility(View.GONE);
                    ((SenderViewHolder) holder).senderFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "This file was deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {

                    if (message.getType().equals("pdf")) {
                        Picasso.get().load(R.drawable.pdf).into(((SenderViewHolder) holder).senderFile);
                    }
                    if (message.getType().equals("docx")) {
                        Picasso.get().load(R.drawable.doc).into(((SenderViewHolder) holder).senderFile);
                    }
                    ((SenderViewHolder) holder).senderFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Document Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((SenderViewHolder) holder).senderImage.getContext().startActivity(intent);
                                }
                            });
                        }

                        //save file to local


                    });
                }


            } else {
                //----------
                //hide receiver text
                ((ReceiverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).recieverTime.setVisibility(View.GONE);
                //----------
                ((ReceiverViewHolder) holder).recieverImage.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).recieverFile.setVisibility(View.VISIBLE);

                if (message.getMessage().equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")) //"this file was deleted"
                {
                    Picasso.get().load(R.drawable.file_deleted).into(((ReceiverViewHolder) holder).recieverFile);
                    ((ReceiverViewHolder) holder).recieverFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "This file was deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    if (message.getType().equals("pdf")) {
                        Picasso.get().load(R.drawable.pdf).into(((ReceiverViewHolder) holder).recieverFile);
                    }
                    if (message.getType().equals("docx")) {
                        Picasso.get().load(R.drawable.doc).into(((ReceiverViewHolder) holder).recieverFile);
                    }

                    ((ReceiverViewHolder) holder).recieverFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Document Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((ReceiverViewHolder) holder).recieverImage.getContext().startActivity(intent);
                                }
                            });


                        }
                    });
                }


            }
        }

        //delete message
        String senderId = FirebaseAuth.getInstance().getUid();
        String receiverId = message.getuId();

        String senderRoom = senderId + receiverId;
        String receiverRoom = receiverId + senderId;
        //delete message
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (holder.getClass() == SenderViewHolder.class) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(((SenderViewHolder) holder).senderImage.getContext());
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
                            database.getReference().child("chats").child(receiverRoom).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        Message message_db = ds.getValue(Message.class);
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

        if (holder.getClass() == SenderViewHolder.class) {
            //delete image
            holder.itemView.findViewById(R.id.senderImg_del).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //----------------
                    if (holder.getClass() == SenderViewHolder.class) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(((SenderViewHolder) holder).senderImage.getContext());
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
                                database.getReference().child("chats").child(receiverRoom).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            Message message_db = ds.getValue(Message.class);
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
                    }
                    //----------------
                    return true;
                }
            });

            //delete file
            holder.itemView.findViewById(R.id.senderFile_del).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //----------------
                    if (holder.getClass() == SenderViewHolder.class) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(((SenderViewHolder) holder).senderImage.getContext());
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
                                database.getReference().child("chats").child(receiverRoom).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            Message message_db = ds.getValue(Message.class);
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
                    }
                    //----------------
                    return true;
                }
            });
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
        }
    }
}
