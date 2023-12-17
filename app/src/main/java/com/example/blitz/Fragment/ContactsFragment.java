package com.example.blitz.Fragment;

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

public class ContactsFragment extends Fragment {
    public ContactsFragment() {
    }

    FragmentContactsBinding binding;
    FirebaseDatabase database;
    ContactAdapter adapter;
    ArrayList<Users> friendList = new ArrayList<>();
    ArrayList<Users> allUsers = new ArrayList<>();
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.searchText.setOnEditorActionListener((v, actionId, event) -> {
            String searchText = binding.searchText.getText().toString();
            if (actionId == 6)
            {
                binding.searchText.clearFocus();
                if (searchText.isEmpty())
                {
                    friendList.clear();
                    adapter = new ContactAdapter(friendList, getContext());
                    binding.contactRecyclerView.setAdapter(adapter);
                }
                else if(searchText.equals("@all"))
                {
                    database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            allUsers.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                Users user = dataSnapshot.getValue(Users.class);
                                user.setUserId(dataSnapshot.getKey());
                                allUsers.add(user);
                            }
                            adapter = new ContactAdapter(allUsers, getContext());
                            binding.contactRecyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            return false;
        });
        adapter = new ContactAdapter(allUsers, getContext());
        binding.contactRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.contactRecyclerView.setLayoutManager(layoutManager);
        return binding.getRoot();
    }

}