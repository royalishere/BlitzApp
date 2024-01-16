package com.example.blitz.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blitz.Adapters.ContactAdapter;
import com.example.blitz.Models.Users;
import com.example.blitz.databinding.FragmentContactsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactsFragment extends Fragment {
    public ContactsFragment() {}

    public static FragmentContactsBinding binding;
    FirebaseDatabase database;
    ContactAdapter adapter;
    public static Set<String> friendList = new HashSet<>();
    ArrayList<Users> usersList = new ArrayList<>();
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        new fetchFriendList().execute();

        binding.searchText.setOnEditorActionListener((v, actionId, event) -> {
            String searchText = binding.searchText.getText().toString();
            if (actionId == 6) // "Done" button
            {
                binding.searchText.clearFocus();
                if (searchText.isEmpty())
                {
                    adapter = new ContactAdapter(new ArrayList<>(), getContext());
                    binding.contactRecyclerView.setAdapter(adapter);
                }
                else if(searchText.equals("@all"))
                {
                    database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usersList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                if (dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())) continue;
                                Users user = dataSnapshot.getValue(Users.class);
                                user.setUserId(dataSnapshot.getKey());
                                usersList.add(user);
                            }
                            adapter = new ContactAdapter(usersList, getContext());
                            binding.contactRecyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                else if(searchText.startsWith("@")) {
                    // search by email
                    searchText = searchText.substring(1);
                    database.getReference().child("Users").orderByChild("mail").startAt(searchText).endAt(searchText+"\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usersList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                if (dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())) continue;
                                Users user = dataSnapshot.getValue(Users.class);
                                user.setUserId(dataSnapshot.getKey());
                                usersList.add(user);
                            }
                            adapter = new ContactAdapter(usersList, getContext());
                            binding.contactRecyclerView.setAdapter(adapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                else
                {
                    // search by username
                    database.getReference().child("Users").orderByChild("userName").startAt(searchText).endAt(searchText+"\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usersList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                if (dataSnapshot.getKey().equals(auth.getCurrentUser().getUid())) continue;
                                Users user = dataSnapshot.getValue(Users.class);
                                user.setUserId(dataSnapshot.getKey());
                                usersList.add(user);
                            }
                            adapter = new ContactAdapter(usersList, getContext());
                            binding.contactRecyclerView.setAdapter(adapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            return false;
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.contactRecyclerView.setLayoutManager(layoutManager);
        return binding.getRoot();
    }

    private class fetchFriendList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String uid = auth.getCurrentUser().getUid();
            database.getReference().child("Users").child(uid).child("friendList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    friendList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        friendList.add(dataSnapshot.getKey());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
            return null;
        }
    }
}