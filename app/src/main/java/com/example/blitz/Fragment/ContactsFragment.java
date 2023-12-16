package com.example.blitz.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.SearchView;

import com.example.blitz.Adapters.UsersAdapter;
import com.example.blitz.Models.Users;
import com.example.blitz.R;
import com.example.blitz.databinding.FragmentChatsBinding;
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
    ArrayList<Users> friendslist = new ArrayList<>();
    ArrayList<Users> allUsers = new ArrayList<>();
    FirebaseDatabase database;

    FirebaseAuth auth;
    UsersAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContactsBinding.inflate(inflater, container, false);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        adapter = new UsersAdapter(getContext(), friendslist);
        binding.friendsRecyclerView.setAdapter(adapter);

        // fetch contacts from firebase
        new FetchContacts().execute();
        return binding.getRoot();
    }

    private class FetchContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // get current user id
            String uid = auth.getCurrentUser().getUid();
            Bundle bundle = getArguments();
            if(bundle != null)
                allUsers = (ArrayList<Users>) bundle.getSerializable("allUsers");

            return null;
        }
    }
}