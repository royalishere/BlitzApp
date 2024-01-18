package com.example.blitz.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.ChatDetailActivity;
import com.example.blitz.GroupChatDetailActivity;
import com.example.blitz.Models.Groups;
import com.example.blitz.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scottyab.aescrypt.AESCrypt;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder>{
    ArrayList<Groups> list;
    Context context;

    public GroupsAdapter(Context context, ArrayList<Groups> list) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_users,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Groups group = list.get(position);
        Picasso.get().load(R.drawable.group_chat).into(holder.groupImage);
        holder.groupName.setText(group.getGroupName());

        // get last message
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Groups").child(group.getGroupId()).child("Messages");
        reference.orderByChild("timestamp").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String senderName = dataSnapshot.child("senderName").getValue().toString();
                        if (dataSnapshot.child("type").getValue().toString().equals("text")) {
                            String last_msg = dataSnapshot.child("message").getValue().toString();
                            //decrypt last message
                            try {
                                last_msg = AESCrypt.decrypt(context.getString(R.string.key_encrypt), last_msg);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            last_msg = last_msg.replaceAll("\\r|\\n", " ");
                            if (last_msg.length() > 10) {
                                last_msg = last_msg.substring(0, 10) + "...";
                            }

                            holder.lastMessage.setText(senderName + ": " + last_msg);
                        }
                        else if(dataSnapshot.child("type").getValue().toString().equals("image")&& !dataSnapshot.child("message").getValue().toString().equals("UBiqi2OEkXIx8dLh2/I2HTYc04R42yIUpS/IwUGPwZg=")){

                            holder.lastMessage.setText(senderName+ ": Photo ");
                        }
                        else if(dataSnapshot.child("type").getValue().toString().equals("pdf")&& !dataSnapshot.child("message").getValue().toString().equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")){

                            holder.lastMessage.setText(senderName+ ": PDF File ");
                        }
                        else if(dataSnapshot.child("type").getValue().toString().equals("docx")&& !dataSnapshot.child("message").getValue().toString().equals("X3BahcMIGeJCX/ZJe03P745iTtxVRgThnYKO37QSFLs=")){

                            holder.lastMessage.setText(senderName+ ": DOC file" );
                        }
                        else{
                            holder.lastMessage.setText("This message was deleted");
                        }
                    }
                }
                else {
                    holder.lastMessage.setText(group.getGroupName() + " has been created");
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
                Intent intent = new Intent(context, GroupChatDetailActivity.class);
                intent.putExtra("groupId", group.getGroupId());
                intent.putExtra("groupName", group.getGroupName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // reuse the user viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView groupImage;
        TextView groupName, lastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.profile_image);
            groupName = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.last_msg);
        }
    }

}
