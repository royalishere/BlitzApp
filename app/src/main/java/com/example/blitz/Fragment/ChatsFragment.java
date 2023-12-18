package com.example.blitz.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.blitz.Adapters.GroupsAdapter;
import com.example.blitz.Adapters.UsersAdapter;
import com.example.blitz.Models.Groups;
import com.example.blitz.Models.Users;
import com.example.blitz.R;
import com.example.blitz.databinding.FragmentChatsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }
    FragmentChatsBinding binding;
    public static ArrayList<Users> chatlist = new ArrayList<>();
    public ArrayList<Groups> grouplist = new ArrayList<>();

    FirebaseDatabase database;
    Button chat_btn, group_btn;
    UsersAdapter adapter;
    GroupsAdapter groupAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        new FetchUsersFromFirebase().execute();
        new FetchGroupFromFirebase().execute();

        chat_btn = binding.chatBtn;
        group_btn = binding.groupchatBtn;

        // main chat sections handling
        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set tint color for chat_btn
                chat_btn.setBackgroundTintList(getResources().getColorStateList(R.color.grayBubble));
                group_btn.setBackgroundTintList(getResources().getColorStateList(R.color.grayBackground));
                chat_btn.setTextColor(getResources().getColor(R.color.white));
                group_btn.setTextColor(getResources().getColor(R.color.black));
                adapter = new UsersAdapter(getContext(), chatlist);
                binding.chatRecyclerView.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                binding.chatRecyclerView.setLayoutManager(layoutManager);
            }
        });

        // group chat sections handling
        group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat_btn.setBackgroundTintList(getResources().getColorStateList(R.color.grayBackground));
                group_btn.setBackgroundTintList(getResources().getColorStateList(R.color.grayBubble));
                chat_btn.setTextColor(getResources().getColor(R.color.black));
                group_btn.setTextColor(getResources().getColor(R.color.white));
                groupAdapter = new GroupsAdapter(getContext(), grouplist);
                binding.chatRecyclerView.setAdapter(groupAdapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                binding.chatRecyclerView.setLayoutManager(layoutManager);
            }
        });

        chat_btn.performClick();
        return binding.getRoot();
    }

    // do in background
    private class FetchUsersFromFirebase extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // get current user id
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Set<String> friendList = new HashSet<>();
            database.getReference().child("Users").child(uid).child("friendList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    friendList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        friendList.add(dataSnapshot.getKey());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatlist.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Users users = dataSnapshot.getValue(Users.class);
                        users.setUserId(dataSnapshot.getKey());
                        if (friendList.contains(users.getUserId())) {
                            chatlist.add(users);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            return null;
        }
    }

    // do in background
    private class FetchGroupFromFirebase extends AsyncTask<Void, Void ,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.getReference().child("Groups").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    grouplist.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Groups group = dataSnapshot.getValue(Groups.class);
                        group.setGroupId(dataSnapshot.getKey());
                        grouplist.add(group);
                        // if the current adapter is group adapter, notify the adapter
                        if (binding.chatRecyclerView.getAdapter() instanceof GroupsAdapter) {
                            groupAdapter.notifyDataSetChanged();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            return null;
        }
    }
}