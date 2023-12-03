package com.example.blitz.Fragment;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blitz.Change_info;
import com.example.blitz.Models.Users;
import com.example.blitz.R;
import com.example.blitz.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseAuth auth;

    FirebaseDatabase database;
    FirebaseStorage storage;

    Button btnLogout, btnChangeProfile;

    ImageView avt;

    TextView tvUsername, tvEmail;

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    TextView change_pass;
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }






       //click logout
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ConstraintLayout layout_profile = (ConstraintLayout) inflater.inflate(R.layout.fragment_profile, null);
        tvUsername = (TextView) layout_profile.findViewById(R.id.tvUserName);
        tvEmail = (TextView) layout_profile.findViewById(R.id.tvEmail);
        avt = (ImageView) layout_profile.findViewById(R.id.avt);
        change_pass = (TextView) layout_profile.findViewById(R.id.btn_changepass);

        //get the profile picture from storage
        StorageReference reference = storage.getReference().child("profile_pictures").child(FirebaseAuth.getInstance().getUid());
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(avt);
            }
        });

        //if user login with google
        //get the username and email from google to display
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            //get the profile picture from google to display
            Uri personPhoto = acct.getPhotoUrl();
            Picasso.get().load(personPhoto).into(avt);
            Toast.makeText(getActivity(), personName + " " + personEmail, Toast.LENGTH_SHORT).show();
            tvUsername.setText(personName);
            tvEmail.setText(personEmail);
            //update avatar to database
            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("profilePicture")
                    .setValue(personPhoto.toString());


        }

        //if user login with email and password
        if(auth.getCurrentUser() != null){
            tvEmail.setText(auth.getCurrentUser().getEmail());
            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        //get the username and status from database
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users users = snapshot.getValue(Users.class);
                            tvUsername.setText(users.getUserName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

        //click logout
        btnLogout = (Button) layout_profile.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if user login with email and password
                if(auth.getCurrentUser() != null){
                    auth.signOut();
                    Toast.makeText(getActivity(), "Logout", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), SignInActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }
               //if user login with google
                if (acct != null) {
                    signOut_google();
                }


            }
        });

        //click change password
//        change_pass.setOnClickListener(new View.OnClickListener() {
//            String password;
//
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_pass, null);
//
//                EditText edCurrentPass = dialogView.findViewById(R.id.edCurrentPass);
//                EditText edNewPass = dialogView.findViewById(R.id.edNewPass);
//                EditText edConfirmPass = dialogView.findViewById(R.id.edConfirmPass);
//
//                builder.setView(dialogView);
//                AlertDialog dialog = builder.create();
//                    // if login with google account, cannot change password
//                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
//                    if (acct != null) {
//                        Toast.makeText(getActivity(),
//                                "Account is logged in with a Google account, please change the Google account password", Toast.LENGTH_SHORT).show();
//                    }
//                    // if login with email and password
//                    else
//                    {
//                        if (check_input()) {
//                            dialog.dismiss();
//                        }
//                        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
//                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    //get the username and status from database
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        Users users = snapshot.getValue(Users.class);
//                                        password = users.getPassword();
//                                        if (password.equals(edCurrentPass.getText().toString())){
//                                            auth.getCurrentUser().updatePassword(edNewPass.getText().toString());
//                                            String newPassword = edNewPass.getText().toString();
//                                            String confirmPassword = edConfirmPass.getText().toString();
//
//
//                                            if (newPassword.equals(confirmPassword)) {
//                                                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("password")
//                                                        .setValue(newPassword);
//                                                Toast.makeText(getActivity(), "Password is changed", Toast.LENGTH_SHORT).show();
//                                            } else {
//                                                Toast.makeText(getActivity(), "Password does not match", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//
//                    }
//
//                }
//            }
//        );



        //click change profile
        btnChangeProfile = (Button) layout_profile.findViewById(R.id.btn_Profile);
        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Change_info.class);
                startActivity(intent);
            }
        });


//
       return layout_profile;
    }

    void signOut_google(){
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                Toast.makeText(getActivity(), "Logout gg acct", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private boolean check_input() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_pass, null);
        EditText edCurrentPass = dialogView.findViewById(R.id.edCurrentPass);
        EditText edNewPass = dialogView.findViewById(R.id.edNewPass);
        EditText edConfirmPass = dialogView.findViewById(R.id.edConfirmPass);

        if (edCurrentPass.getText().toString().isEmpty()) {
            edCurrentPass.setError("Please enter your current password");
            return false;
        }
        if (edNewPass.getText().toString().isEmpty()) {
            edNewPass.setError("Please enter your new password");
            return false;
        }
        if (edConfirmPass.getText().toString().isEmpty()) {
            edConfirmPass.setError("Please confirm your new password");
            return false;
        }





        return true;
    }



}