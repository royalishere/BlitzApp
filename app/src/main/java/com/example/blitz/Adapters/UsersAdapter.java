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
import com.scottyab.aescrypt.AESCrypt;
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
        reference.child("Messages").orderByChild("timestamp").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("type").getValue().toString().equals("text")) {
                            String last_msg = dataSnapshot.child("message").getValue().toString();

                            //decrypt last message
                            try {
                                last_msg = AESCrypt.decrypt(context.getString(R.string.key_encrypt), last_msg);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            if (last_msg.length() > 20) {
                                last_msg = last_msg.substring(0, 20) + "...";
                            }

                            holder.lastMessage.setText(last_msg );
                        }
                        else if(dataSnapshot.child("type").getValue().toString().equals("image")&& !dataSnapshot.child("message").getValue().toString().equals("UBiqi2OEkXIx8dLh2/I2HTYc04R42yIUpS/IwUGPwZg=")){

                            holder.lastMessage.setText("Photo ");
                        }
                        else if(dataSnapshot.child("type").getValue().toString().equals("pdf")&& !dataSnapshot.child("message").getValue().toString().equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")){

                            holder.lastMessage.setText("PDF File ");
                        }
                        else if(dataSnapshot.child("type").getValue().toString().equals("docx")&& !dataSnapshot.child("message").getValue().toString().equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")){

                            holder.lastMessage.setText("DOC file" );
                        }
                        else{
                            holder.lastMessage.setText("This message was deleted");
                        }
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

        // check if user is online or offline
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(users.getUserId());
        userRef.child("userState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String state = snapshot.child("state").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();

                    if(state.equals("online")) {
                        holder.status.setText("Online");
                        holder.status.setTextColor(context.getResources().getColor(R.color.status_background));
                    }
                    else if(state.equals("offline")) {
                        holder.status.setText("Last visited: " + date + " " + time);
                        holder.status.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    holder.status.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView userName, lastMessage, status;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.profile_image);
                userName = itemView.findViewById(R.id.username);
                lastMessage = itemView.findViewById(R.id.last_msg);
                status = itemView.findViewById(R.id.status);
            }
    }
}
