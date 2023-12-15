package com.example.blitz.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.ChatDetailActivity;
import com.example.blitz.Models.Users;
import com.example.blitz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{
    @androidx.annotation.NonNull
    ArrayList<Users> list;
    Context context;

    public UsersAdapter(@NonNull Context context, ArrayList<Users> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_users,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
        Users users = list.get(position);
        Picasso.get().load(users.getProfilePicture()).placeholder(R.drawable.hacker).into(holder.image);
        holder.userName.setText(users.getUserName());

        // get last message
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(FirebaseAuth.getInstance().getUid() + users.getUserId());
        reference.orderByChild("timestamp").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String last_msg = dataSnapshot.child("message").getValue().toString();
                        if(last_msg.length() > 20)
                        {
                            last_msg = last_msg.substring(0,20) + "...";
                        }
                        holder.lastMessage.setText(dataSnapshot.child("message").getValue().toString());
                    }
                }
                else {
                    holder.lastMessage.setText("You are now connected. Say Hi!");
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send data to ChatDetailActivity
                Intent intent = new Intent(context, ChatDetailActivity.class);
                intent.putExtra("userId", users.getUserId());
                intent.putExtra("profilePicture", users.getProfilePicture());
                intent.putExtra("userName", users.getUserName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView userName, lastMessage;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.profile_image);
                userName = itemView.findViewById(R.id.username);
                lastMessage = itemView.findViewById(R.id.last_msg);
            }
    }
}
