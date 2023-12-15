package com.example.blitz.Fragment;

import static com.google.android.material.color.utilities.MaterialDynamicColors.error;




import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

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

    Button btnLogout, edit_profile,btn_NightMode;

    ImageView avt, plus;

    TextView tvUsername, tvEmail,tvMobile, tvAddress, tvStatus;

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    TextView change_pass;
    boolean isNightMode;
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
        tvMobile = (TextView) layout_profile.findViewById(R.id.tvMobile);
        tvAddress = (TextView) layout_profile.findViewById(R.id.tvAddress);
        tvStatus = (TextView) layout_profile.findViewById(R.id.tvStatus);
        avt = (ImageView) layout_profile.findViewById(R.id.avt);
        plus = (ImageView) layout_profile.findViewById(R.id.plus);
        change_pass = (TextView) layout_profile.findViewById(R.id.btn_changepass);
        edit_profile = (Button) layout_profile.findViewById(R.id.btn_EditProfile);



        //-------------------
        //night mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            LinearLayout LL1 = (LinearLayout) layout_profile.findViewById(R.id.LL1);
            LL1.setBackground(getResources().getDrawable(R.drawable.dark_background));

            ConstraintLayout CL_topbackground = (ConstraintLayout) layout_profile.findViewById(R.id.CL_topbackground);
            CL_topbackground.setBackground(getResources().getDrawable(R.drawable.dark_background));

        }
        else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            LinearLayout LL1 = (LinearLayout) layout_profile.findViewById(R.id.LL1);
            LL1.setBackground(getResources().getDrawable(R.drawable.white_background));

            ConstraintLayout CL_topbackground = (ConstraintLayout) layout_profile.findViewById(R.id.CL_topbackground);
            CL_topbackground.setBackground(getResources().getDrawable(R.drawable.top_background));
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }




        //-------------------


        //get the profile picture from storage
        StorageReference reference = storage.getReference().child("profile_pictures").child(FirebaseAuth.getInstance().getUid());
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(avt);
            }
        });

        //choose the profile picture from storage
        avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT); //ACTION_GET_CONTENT
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT); //ACTION_GET_CONTENT
                intent.setType("image/*");
                startActivityForResult(intent, 33);
            }
        });



        //get address and mobile from database
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    //get the username and status from database
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        tvAddress.setText(users.getAddress());
                        tvMobile.setText(users.getMobile());
                        tvStatus.setText(users.getStatus());
                        if (tvAddress.getText().toString().isEmpty()) {
                            tvAddress.setText("Empty");
                        }
                        if (tvMobile.getText().toString().isEmpty()) {
                            tvMobile.setText("Empty");
                        }
                        if (tvStatus.getText().toString().isEmpty()) {
                            tvStatus.setText("Empty");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
            tvUsername.setText(personName);
            tvEmail.setText(personEmail);
            //update avatar to database
            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("profilePicture")
                    .setValue(personPhoto.toString());
            //update address and mobile to database


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

                //if user login with google
                if (acct != null) {
                    mGoogleSignInClient.signOut();
                    //delete token
                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("token")
                            .setValue("");
                    Toast.makeText(getActivity(), "Logout gg acct", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }


                //if user login with email and password
                if(auth.getCurrentUser() != null){
                    //delete token
                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("token")
                            .setValue("");


                    auth.signOut();
                    Toast.makeText(getActivity(), "Logout", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), SignInActivity.class);



                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                }



            }
        });

        //click change password
        change_pass.setOnClickListener(new View.OnClickListener() {
            String password;

            @Override
            public void onClick(View v) {
                //if user login with google account
                //cannot change password
                //show notification to user
                if (acct != null) {
                    change_pass.setText("Cannot change password");
                    Toast.makeText(getActivity(), "Account is logged in with a Google account, please change the Google account password", Toast.LENGTH_SHORT).show();
                }
                else {
                    //if user login with email and password
                    //show dialog to change password

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_pass, null);

                    EditText edCurrentPass = dialogView.findViewById(R.id.edCurrentPass);
                    EditText edNewPass = dialogView.findViewById(R.id.edNewPass);
                    EditText edConfirmPass = dialogView.findViewById(R.id.edConfirmPass);

                    //-----------------------
                    //night mode
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                        ConstraintLayout CL_dialog = (ConstraintLayout) dialogView.findViewById(R.id.dialogBox);
                        CL_dialog.setBackground(getResources().getDrawable(R.drawable.dark_box));
                    }
                    else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                        ConstraintLayout CL_dialog = (ConstraintLayout) dialogView.findViewById(R.id.dialogBox);
                        CL_dialog.setBackground(getResources().getDrawable(R.drawable.white_box));
                    }
                    else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    //-----------------------




                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    dialogView.findViewById(R.id.btnChange_CP_dialog).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (edCurrentPass.getText().toString().isEmpty() || edNewPass.getText().toString().isEmpty() || edConfirmPass.getText().toString().isEmpty()) {
                                Toast.makeText(getActivity(), "Please enter all information", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (!edNewPass.getText().toString().equals(edConfirmPass.getText().toString())) {
                                Toast.makeText(getActivity(), "Confirm password does not match", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (edNewPass.getText().toString().length() < 6) {
                                Toast.makeText(getActivity(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        //get the username and status from database
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Users users = snapshot.getValue(Users.class);
                                            password = users.getPassword();
                                            String currentPass_encrypted = md5(edCurrentPass.getText().toString());
                                            String newPass_encrypted = md5(edNewPass.getText().toString());
                                            if (password.equals(currentPass_encrypted) && !password.equals(newPass_encrypted)){


                                                auth.getCurrentUser().updatePassword(edNewPass.getText().toString());





                                                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("password")
                                                            .setValue(newPass_encrypted);
                                                    Toast.makeText(getActivity(), "Password is changed", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                            } else {
                                                    Toast.makeText(getActivity(), "Password does not match", Toast.LENGTH_SHORT).show();
                                                }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                        }


                    });
                    dialogView.findViewById(R.id.btnCancel_CP_dialog).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    }

                }



            }
            //if have changed data, reload

        }
        );

        //click edit profile
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                //show dialog to edit profile
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);

                EditText edUserName = dialogView.findViewById(R.id.edUserName);
                EditText edStatus = dialogView.findViewById(R.id.edStatus);
                EditText edAddress = dialogView.findViewById(R.id.edAddress);
                EditText edMobile = dialogView.findViewById(R.id.edMobile);

                //-----------------------
                //night mode
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    ConstraintLayout CL_dialog = (ConstraintLayout) dialogView.findViewById(R.id.dialogBox);
                    CL_dialog.setBackground(getResources().getDrawable(R.drawable.dark_box));

                }
                else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                    ConstraintLayout CL_dialog = (ConstraintLayout) dialogView.findViewById(R.id.dialogBox);
                    CL_dialog.setBackground(getResources().getDrawable(R.drawable.white_box));
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                //-----------------------

                //get the username, status, address, mobile from database
                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            //get the username and status from database
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Users users = snapshot.getValue(Users.class);

                                edStatus.setText(users.getStatus());
                                edUserName.setText(users.getUserName());
                                edAddress.setText(users.getAddress());
                                edMobile.setText(users.getMobile());
                                if (edAddress.getText().toString().isEmpty()) {
                                    edAddress.setText("Empty");
                                }
                                if (edMobile.getText().toString().isEmpty()) {
                                    edMobile.setText("Empty");
                                }
                                if (edStatus.getText().toString().isEmpty()) {
                                    edStatus.setText("Empty");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();



                dialogView.findViewById(R.id.btn_Save_EP_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get the status and username from edit text
                        String status = edStatus.getText().toString();
                        String username = edUserName.getText().toString();
                        String address = edAddress.getText().toString();
                        String mobile = edMobile.getText().toString();

                        //if user does not enter username
                        if (username.isEmpty()) {
                            edUserName.setError("Please enter your username");
                            return;
                        } else {
                            HashMap<String, Object> obj = new HashMap<>();
                            obj.put("userName", username);
                            obj.put("about", status);
                            obj.put("address", address);
                            obj.put("mobile", mobile);
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                    .updateChildren(obj);
                            Toast.makeText(getActivity(), "Profile is updated", Toast.LENGTH_SHORT).show();

                        }
                        //set text in profile fragment
                        if (status.isEmpty()) {
                            tvStatus.setText("Empty");
                        } else {
                            tvStatus.setText(status);
                        }
                        if (address.isEmpty()) {
                            tvAddress.setText("Empty");
                        } else {
                            tvAddress.setText(address);
                        }
                        if (mobile.isEmpty()) {
                            tvMobile.setText("Empty");
                        } else {
                            tvMobile.setText(mobile);
                        }
                        tvUsername.setText(username);




                        dialog.dismiss();

                    }
                });
                dialogView.findViewById(R.id.btnCancel_EP_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }











            }
            public void reload() {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(ProfileFragment.this).attach(ProfileFragment.this).commit();
            }
        });





        //click change profile
//        btnChangeProfile = (Button) layout_profile.findViewById(R.id.btn_Profile);
//        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), Change_info.class);
//                startActivity(intent);
//            }
//        });


//
       return layout_profile;
    }



    void signOut_google(){
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                //delete token
                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("token")
                        .setValue("");
                Toast.makeText(getActivity(), "Logout gg acct", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data.getData() != null)) {

            Uri sFile = data.getData();

            avt.setImageURI(sFile);

            final StorageReference reference = storage.getReference().child("profile_pictures")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("profilePicture")
                                            .setValue(uri.toString());
                                    //set the profile picture in the profile fragment
                                    Picasso.get().load(uri).into(avt);
                                    Toast.makeText(getActivity(), "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
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

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(String.format("%02X", messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }



}