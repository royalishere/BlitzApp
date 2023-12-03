package com.example.blitz.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.Models.Message;
import com.example.blitz.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<Message> messages;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

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
        if(holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder)holder).senderMsg.setText(message.getMessage());
            String formattedDate = DateFormat.format("dd/MM/yy hh:mm", message.getTimestamp()).toString();
            ((SenderViewHolder)holder).senderTime.setText(formattedDate);
        } else {
            ((ReceiverViewHolder)holder).recieverMsg.setText(message.getMessage());
            String formattedDate = DateFormat.format("dd/MM/yy hh:mm", message.getTimestamp()).toString();
            ((ReceiverViewHolder)holder).recieverTime.setText(formattedDate);
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
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            recieverMsg = itemView.findViewById(R.id.receiverText);
            recieverTime = itemView.findViewById(R.id.receiverTime);
        }
    }

    public class SenderViewHolder extends ReceiverViewHolder {
        TextView senderMsg, senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }
}
