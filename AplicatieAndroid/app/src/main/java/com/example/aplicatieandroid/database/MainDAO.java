package com.example.aplicatieandroid.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.aplicatieandroid.models.Note;

import java.util.List;

@Dao
public interface MainDAO {

    @Insert(onConflict = REPLACE)
    void insert(Note note);

    @Query("SELECT * FROM notes ORDER BY note_ID DESC")
    List<Note> getALL();

    @Query("UPDATE notes SET title = :title, text = :text WHERE note_ID = :id")
    void update(int id, String title, String text);

    @Delete
    void delete(Note note);

    @Query("UPDATE notes SET important = :pin WHERE note_ID = :id")
    void pin(int id, boolean pin);

    @Query("SELECT * FROM notes WHERE user_email = :userEmail")
    List<Note> getNotesByUserEmail(String userEmail);

}
