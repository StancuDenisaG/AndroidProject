package com.example.aplicatieandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicatieandroid.models.Note;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesTakerActivity extends AppCompatActivity {

    EditText etTitle, etText;
    ImageView ivSave, ivCancel;
    Note note;

    boolean editOldNote = false;

    String currentUserEmail;


    @Override
    protected void onCreate(Bundle savedInstance) {


        super.onCreate(savedInstance);
        setContentView(R.layout.activity_notes_taker);

        ivSave = findViewById(R.id.ivSave);
        ivCancel=findViewById(R.id.ivCancel);
        etText = findViewById(R.id.etText);
        etTitle = findViewById(R.id.etTitle);
        SharedPreferences sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        currentUserEmail = sharedPref.getString("email", "");

        Note new_note ;

        try {
            note = (Note) getIntent().getSerializableExtra("old_note");
            etTitle.setText(note.getTitle());
            etText.setText(note.getText());
            editOldNote = true;
        } catch (Exception e){
            e.printStackTrace();
        }


        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString();
                String text = etText.getText().toString();

                if(text.isEmpty()){
                    Toast.makeText(NotesTakerActivity.this, "Please enter text", Toast.LENGTH_LONG);
                    return;
                }
                SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                if(!editOldNote){
                    note = new Note();
                }

                note.setTitle(title);
                note.setText(text);
                note.setDate(format.format(date));
                note.setUserEmail(currentUserEmail);

                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

    }

}
