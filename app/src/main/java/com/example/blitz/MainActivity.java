package com.example.blitz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.blitz.Adapters.FragmentsAdapter;
import com.example.blitz.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String NIGHT_MODE_KEY = "nightMode";

    ActivityMainBinding binding;
    FirebaseAuth auth;
    boolean isNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabNavigation.setupWithViewPager(binding.viewPager);

        Toolbar toolbar = binding.myToolbar;
        setSupportActionBar(toolbar);

        // Load night mode preference
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        isNightMode = settings.getBoolean(NIGHT_MODE_KEY, false);

        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem nightModeItem = menu.findItem(R.id.nightMode);
        nightModeItem.setChecked(isNightMode);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.nightMode) {
            isNightMode = !isNightMode;
            item.setChecked(isNightMode);

            // Save night mode preference
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(NIGHT_MODE_KEY, isNightMode);
            editor.apply();

            // Apply night mode
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            recreate(); // Recreate the activity to apply the night mode immediately
        }

        return super.onOptionsItemSelected(item);
    }
}