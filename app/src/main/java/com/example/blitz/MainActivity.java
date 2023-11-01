package com.example.blitz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.blitz.Adapters.FragmentsAdapter;
import com.example.blitz.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabNavigation.setupWithViewPager(binding.viewPager);
    }
}