package com.example.aplicatieandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aplicatieandroid.database.RoomDB;
import com.example.aplicatieandroid.models.Note;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvEmail;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnLogout, btnDeleteProfile;

    private RoomDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvEmail = findViewById(R.id.tvEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteProfile = findViewById(R.id.btnDeleteProfile);

        database = RoomDB.getInstance(this);


        SharedPreferences sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        String currentUserEmail = sharedPref.getString("email", "");


        tvEmail.setText(currentUserEmail);

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_add:
                    Intent intentAdd = new Intent(ProfileActivity.this, NotesTakerActivity.class);
                    startActivityForResult(intentAdd, 101);
                    return true;

                case R.id.action_home:
                    Intent intentProfile = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intentProfile);
                    return true;


                case R.id.action_profile:
                    return true;
            }
            return false;
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.getProviderData().toString().contains("google.com")) {
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Firebase sign out
                            mAuth.signOut();
                            // Save the login state as logged out
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("isLoggedIn", false);
                            editor.putString("email", "");
                            editor.putString("password", "");
                            editor.apply();
                            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {

                    mAuth.signOut();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.putString("email", "");
                    editor.putString("password", "");
                    editor.apply();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        btnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account? This action cannot be undone and all your data will be lost.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User confirmed the deletion
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                if (currentUser != null) {
                                    // Delete the user's account
                                    currentUser.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.remove("email");
                                                        editor.putBoolean("isLogged", false);
                                                        editor.apply();


                                                        List<Note> userNotes =  database.mainDAO().getNotesByUserEmail(currentUserEmail);
                                                        for (Note note : userNotes) {
                                                            database.mainDAO().delete(note);
                                                        }

                                                        // Navigate back to the login page
                                                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {

                                                    }
                                                }
                                            });
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }
}
