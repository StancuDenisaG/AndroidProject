package com.example.aplicatieandroid.models;

import androidx.cardview.widget.CardView;

public interface NotesClickListener {
    void onClick(Note notes);
    void onLongClick(Note notes, CardView cardView);
}
