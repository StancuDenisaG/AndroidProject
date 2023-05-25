package com.example.aplicatieandroid.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    int note_ID = 0;

    @ColumnInfo(name = "user_email")
    String userEmail = "";

    @ColumnInfo(name = "title")
    String title = "";

    @ColumnInfo(name = "text")
    String text = "";

    @ColumnInfo(name = "date")
    String date = "";

    @ColumnInfo(name = "important")
    boolean important = false;

    public int getNote_ID() {
        return note_ID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    public void setNote_ID(int note_ID) {
        this.note_ID = note_ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }
}
