package com.example.blitz.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.blitz.Adapters.UsersAdapter;
import com.example.blitz.MainActivity;
import com.example.blitz.Models.Users;
import com.example.blitz.R;
import com.example.blitz.databinding.FragmentChatsBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }
    FragmentChatsBinding binding;
    ArrayList<Users> chatlist = new ArrayList<>();
    ArrayList<Users> grouplist = new ArrayList<>();
    FirebaseDatabase database;
    Button chat_btn, group_btn;
    UsersAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        new FetchUsersFromFirebase().execute();

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
                adapter = new UsersAdapter(getContext(), grouplist);
                binding.chatRecyclerView.setAdapter(adapter);

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
            database.getReference().child("Users").child(uid).child("friendList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatlist.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        // for each key in friendList, get the user info
                        database.getReference().child("Users").child((String) dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users user = snapshot.getValue(Users.class);
                                chatlist.add(user);
                                adapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
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