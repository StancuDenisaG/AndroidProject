package com.example.aplicatieandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.aplicatieandroid.adapters.NotesListAdapter;
import com.example.aplicatieandroid.database.RoomDB;
import com.example.aplicatieandroid.models.Note;
import com.example.aplicatieandroid.models.NotesClickListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private Button btnLogOut;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private RecyclerView recyclerView;
    private NotesListAdapter notesListAdapter;
    private List<Note> notes = new ArrayList<>();
    private RoomDB database;
    private FloatingActionButton btnAdd;
    private SearchView svHome;
    private Note selectedNote;
    private String currentUserEmail;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        SharedPreferences sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        boolean isLogged = sharedPref.getBoolean("isLoggedIn", false);


        if (!isLogged) {
            // User is not logged in, navigate to the login page
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recycler_main);
        svHome = findViewById(R.id.svHome);



        currentUserEmail = sharedPref.getString("email", "");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_add:
                    Intent intentAdd = new Intent(MainActivity.this, NotesTakerActivity.class);
                    startActivityForResult(intentAdd, 101);
                    return true;

                case R.id.action_profile:
                    Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intentProfile);
                    return true;


                case R.id.action_home:
                    return true;
            }
            return false;
        });


        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getNotesByUserEmail(currentUserEmail);

        updateRecycler(notes);


        mAuth = FirebaseAuth.getInstance();

        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());


        svHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Check if the user is logged in
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                }
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    private void filter(String newText) {
        List<Note> filteredList = new ArrayList<>();
        for (Note singleNote : notes) {
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || singleNote.getText().toLowerCase().contains(newText.toLowerCase())) {
                if (singleNote.getUserEmail().equals(currentUserEmail)) {
                    filteredList.add(singleNote);
                }
            }
        }
        notesListAdapter.filterList(filteredList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Note newNote = (Note) data.getSerializableExtra("note");
                database.mainDAO().insert(newNote);
                notes.clear();
                notes.addAll(database.mainDAO().getNotesByUserEmail(currentUserEmail));
                notesListAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                Note updatedNote = (Note) data.getSerializableExtra("note");
                database.mainDAO().update(updatedNote.getNote_ID(), updatedNote.getTitle(), updatedNote.getText());
                notes.clear();
                notes.addAll(database.mainDAO().getNotesByUserEmail(currentUserEmail));
                notesListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateRecycler(List<Note> notes) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this, notes, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);
    }


    private NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Note note) {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("old_note", note);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Note note, CardView cardView) {
            selectedNote = note;
            showPopup(cardView);
        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.pin:
                if (selectedNote.isImportant()) {
                    database.mainDAO().pin(selectedNote.getNote_ID(), false);
                    Toast.makeText(MainActivity.this, "Unpinned!", Toast.LENGTH_LONG).show();
                } else {
                    database.mainDAO().pin(selectedNote.getNote_ID(), true);
                    Toast.makeText(MainActivity.this, "Pinned!", Toast.LENGTH_LONG).show();
                }
                notes.clear();
                notes.addAll(database.mainDAO().getNotesByUserEmail(currentUserEmail));
                notesListAdapter.notifyDataSetChanged();
                return true;

            case R.id.delete:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                notesListAdapter.notifyDataSetChanged();
                updateRecycler(notes);
                Toast.makeText(MainActivity.this, "Note deleted!", Toast.LENGTH_LONG).show();
                return true;

            default:
                return false;
        }
    }




}
