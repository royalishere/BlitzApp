package com.example.blitz.Adapters;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.blitz.Fragment.ChatsFragment;
import com.example.blitz.Fragment.ContactsFragment;
import com.example.blitz.Fragment.ProfileFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fragment) {
        super(fragment, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new ChatsFragment();
            case 1: return new ContactsFragment();
            case 2: return new ProfileFragment();
            default: return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Chats";
            case 1: return "Contacts";
            case 2: return "Profile";
            default: return null;
        }
    }

    @Override
    public int getCount() { return 3  ; }
}
