package com.example.blitz.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blitz.Fragment.ContactsFragment;
import com.example.blitz.Models.Users;
import com.example.blitz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        Users user = contactList.get(position);
        Picasso.get().load(user.getProfilePicture()).placeholder(R.drawable.hacker).into(holder.contactImage);
        holder.contactName.setText(user.getUserName());
        holder.contactStatus.setText(user.getStatus());
        holder.contactMobile.setText(user.getMobile());

        if (ContactsFragment.friendList.contains(user.getUserId())) {
            holder.itemView.findViewById(R.id.addfr_btn).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.unfr_btn).setVisibility(View.VISIBLE);
        }

        holder.addFriend.setOnClickListener(v -> {
            holder.handleAddFriend(user.getUserId());
            holder.itemView.findViewById(R.id.addfr_btn).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.unfr_btn).setVisibility(View.VISIBLE);
        });

        holder.unFriend.setOnClickListener(v -> {
            holder.handleUnFriend(user.getUserId());
            holder.itemView.findViewById(R.id.addfr_btn).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.unfr_btn).setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImage, addFriend, unFriend, onlineIcon;
        TextView contactName, contactStatus, contactMobile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImage = itemView.findViewById(R.id.contact_image);
            contactName = itemView.findViewById(R.id.contact_username);
            contactStatus = itemView.findViewById(R.id.contact_status);
            contactMobile = itemView.findViewById(R.id.contact_mobile);
            addFriend = itemView.findViewById(R.id.addfr_btn);
            unFriend = itemView.findViewById(R.id.unfr_btn);
            onlineIcon = itemView.findViewById(R.id.user_online_status);
        }

        public void handleAddFriend(String uid) {
            String cur_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("Users").child(cur_uid).child("friendList").child(uid).setValue(true);
            ref.child("Users").child(uid).child("friendList").child(cur_uid).setValue(true);
        }

        public void handleUnFriend(String uid) {
            String cur_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("Users").child(cur_uid).child("friendList").child(uid).removeValue();
            ref.child("Users").child(uid).child("friendList").child(cur_uid).removeValue();
        }
    }
}
