package com.example.blitz;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.blitz.Adapters.FragmentsAdapter;
import com.example.blitz.Models.Users;
import com.example.blitz.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String NIGHT_MODE_KEY = "nightMode";

    FirebaseDatabase database;

    public static ArrayList<Users> allUsers = new ArrayList<>();
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
        else if (item.getItemId() == R.id.createGroup)
        {
            RequestNewGroup();
        }

        return super.onOptionsItemSelected(item);
    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g. Blitz Devs");
        groupNameField.setTextSize(16);
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();

                if (groupName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter group name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel(); // Close the dialog
            }
        });
        builder.show();
    }

    private void CreateNewGroup(String groupName) {
        database = FirebaseDatabase.getInstance();
        database.getReference().child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, groupName + " group is created successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}