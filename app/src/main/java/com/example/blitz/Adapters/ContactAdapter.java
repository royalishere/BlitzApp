package com.example.blitz.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.Models.Users;
import com.example.blitz.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    ArrayList<Users> contactList;
    Context context;

    public ContactAdapter(ArrayList<Users> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((context)).inflate(R.layout.show_contacts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = contactList.get(position);
        Picasso.get().load(users.getProfilePicture()).placeholder(R.drawable.hacker).into(holder.contactImage);
        holder.contactName.setText(users.getUserName());
        holder.contactStatus.setText(users.getStatus());
        holder.contactMobile.setText(users.getMobile());

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImage;
        TextView contactName, contactStatus, contactMobile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_username);
            contactStatus = itemView.findViewById(R.id.contact_status);
            contactMobile = itemView.findViewById(R.id.contact_mobile);
        }
    }
}
