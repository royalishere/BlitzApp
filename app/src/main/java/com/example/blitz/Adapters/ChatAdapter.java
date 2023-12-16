package com.example.blitz.Adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.ChatDetailActivity;
import com.example.blitz.Models.Message;
import com.example.blitz.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<Message> messages;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    FirebaseStorage storage = FirebaseStorage.getInstance();
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
        if (message.getType().equals("text"))
        {
            if (holder.getClass() == SenderViewHolder.class) {
                ((SenderViewHolder) holder).senderMsg.setText(message.getMessage());
                String formattedDate = DateFormat.format("dd.MM.yyyy  hh:mm", message.getTimestamp()).toString();
                ((SenderViewHolder) holder).senderTime.setText(formattedDate);

                //-------------
                //hide sender image
                ((SenderViewHolder) holder).senderImage.setVisibility(View.GONE);

            } else {
                ((ReceiverViewHolder) holder).recieverMsg.setText(message.getMessage());
                String formattedDate = DateFormat.format("dd.MM.yyyy  hh:mm", message.getTimestamp()).toString();
                ((ReceiverViewHolder) holder).recieverTime.setText(formattedDate);

                //-------------
                //hide receiver image
                ((ReceiverViewHolder) holder).recieverImage.setVisibility(View.GONE);
            }
        }
        if (message.getType().equals("image"))
        {
            if (holder.getClass() == SenderViewHolder.class) {
               //----------
                //hide sender text
                ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                ((SenderViewHolder) holder).senderTime.setVisibility(View.GONE);
                //----------
                ((SenderViewHolder) holder).senderImage.setVisibility(View.VISIBLE);
                StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
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
                                ((SenderViewHolder)holder).senderImage.getContext().startActivity(intent);
                            }
                        });

                    }
                });


                //resize image : size of image /3






            } else {
                //----------
                //hide receiver text
                ((ReceiverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).recieverTime.setVisibility(View.GONE);
                //----------
                ((ReceiverViewHolder) holder).recieverImage.setVisibility(View.VISIBLE);
                StorageReference reference = storage.getReference().child("Image Files").child(message.getMessage());
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
                                ((ReceiverViewHolder)holder).recieverImage.getContext().startActivity(intent);
                            }
                        });

                    }
                });
                //resize image : size of image /3



            }
        }
        else
        {
            {
                if (holder.getClass() == SenderViewHolder.class) {
                    //----------
                    //hide sender text
                    ((SenderViewHolder) holder).senderMsg.setVisibility(View.GONE);
                    ((SenderViewHolder) holder).senderTime.setVisibility(View.GONE);
                    //----------
                    ((SenderViewHolder) holder).senderImage.setVisibility(View.VISIBLE);
                    if (message.getType().equals("pdf")) {
                        Picasso.get().load(R.drawable.pdf).into(((SenderViewHolder) holder).senderImage);
                    }
                    if (message.getType().equals("docx")) {
                        Picasso.get().load(R.drawable.doc).into(((SenderViewHolder) holder).senderImage);
                    }
                    ((SenderViewHolder) holder).senderImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Document Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((SenderViewHolder)holder).senderImage.getContext().startActivity(intent);
                                }
                            });
                        }

                            //save file to local


                        });








                } else {
                    //----------
                    //hide receiver text
                    ((ReceiverViewHolder) holder).recieverMsg.setVisibility(View.GONE);
                    ((ReceiverViewHolder) holder).recieverTime.setVisibility(View.GONE);
                    //----------
                    ((ReceiverViewHolder) holder).recieverImage.setVisibility(View.VISIBLE);
                    if (message.getType().equals("pdf")) {
                        Picasso.get().load(R.drawable.pdf).into(((ReceiverViewHolder) holder).recieverImage);
                    }
                    if (message.getType().equals("docx")) {
                        Picasso.get().load(R.drawable.doc).into(((ReceiverViewHolder) holder).recieverImage);
                    }

                    ((ReceiverViewHolder) holder).recieverImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StorageReference reference = storage.getReference().child("Document Files").child(message.getMessage());
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                                    ((ReceiverViewHolder)holder).recieverImage.getContext().startActivity(intent);
                                }
                            });



                        }
                    });



                }
            }



        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView recieverMsg, recieverTime;
        ImageView recieverImage;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMsg = itemView.findViewById(R.id.receiverText);
            recieverTime = itemView.findViewById(R.id.receiverTime);
            recieverImage = itemView.findViewById(R.id.receiverImage);
        }
    }

    public class SenderViewHolder extends ReceiverViewHolder {
        TextView senderMsg, senderTime;
        ImageView senderImage;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
            senderImage = itemView.findViewById(R.id.senderImage);
        }
    }

    public void scaleImage(ImageView imageView) {
        // Lấy chiều rộng và chiều cao của tấm ảnh
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Tính toán hệ số thu nhỏ
        float scale = Math.max(250.0f / width, 250.0f / height);

        // Tạo một matrix để áp dụng hệ số thu nhỏ
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Tạo một bitmap mới với kích thước đã được thu nhỏ
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        // Đặt bitmap mới vào `ImageView`
        imageView.setImageBitmap(scaledBitmap);
    }




}
