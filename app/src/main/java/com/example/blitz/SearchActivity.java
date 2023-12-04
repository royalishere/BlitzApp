package com.example.blitz;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.blitz.Adapters.PersonAdapter;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayList<PersonModel> personList;

    RecyclerView recyclerView;
    PersonAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.recyclerView);

        personList = new ArrayList<>();
        personList.add( new PersonModel( "Dang", "phamdang@gmail.com"));
        personList.add( new PersonModel( "Hau", "phuchau@gmail.com"));
        personList.add( new PersonModel( "Kiet", "chaukiet@gmail.com"));
        personList.add( new PersonModel( "Gia", "hoanggia@gmail.com"));

        adapter = new PersonAdapter(this, personList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
