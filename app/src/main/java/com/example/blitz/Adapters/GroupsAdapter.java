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
        holder.lastMessage.setVisibility(View.GONE);

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
