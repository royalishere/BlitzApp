package com.example.blitz.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{
    ArrayList<String> list;
    Context context;

    public GroupAdapter(Context context, ArrayList<String> list) {
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
        String groupName = list.get(position);
        Picasso.get().load(R.drawable.group_chat).into(holder.groupImage);
        holder.groupName.setText(groupName);
        holder.lastMessage.setVisibility(View.GONE);
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
