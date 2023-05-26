package com.example.aplicatieandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicatieandroid.R;
import com.example.aplicatieandroid.models.Note;
import com.example.aplicatieandroid.models.NotesClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapter extends RecyclerView.Adapter{
    Context context;
    List<Note> list;
    NotesClickListener listener;

    public NotesListAdapter(Context context, List<Note> list, NotesClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotesViewHolder notesViewHolder = (NotesViewHolder) holder;
        notesViewHolder.tvTitle.setText(list.get(position).getTitle());
        notesViewHolder.tvTitle.setSelected(true);

        notesViewHolder.tvText.setText(list.get(position).getText());

        notesViewHolder.tvDate.setText(list.get(position).getDate());
        notesViewHolder.tvDate.setSelected(true);

        if(list.get(position).isImportant()){
            notesViewHolder.ivPinned.setImageResource(R.drawable.pin);

        }

        else {
            notesViewHolder.ivPinned.setImageResource(0);
        }

        int color_code =getRandomColor();

        notesViewHolder.notes_container.setCardBackgroundColor(notesViewHolder.itemView.getResources().getColor(color_code, null) );

        notesViewHolder.notes_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(notesViewHolder.getAdapterPosition()));
            }
        });

        notesViewHolder.notes_container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(list.get(notesViewHolder.getAdapterPosition()), notesViewHolder.notes_container );
                return true;
            }
        });


    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();


        colorCode.add(R.color.yellow);
        colorCode.add(R.color.orange);
        colorCode.add(R.color.pink);


        Random random = new Random();
        int randomIndex = random.nextInt(colorCode.size());
        return colorCode.get(randomIndex);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Note> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }
}
class NotesViewHolder extends RecyclerView.ViewHolder{

    CardView notes_container;
    TextView tvTitle, tvText, tvDate;
    ImageView ivPinned;
    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notes_container = itemView.findViewById(R.id.notes_container);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvText = itemView.findViewById(R.id.tvText);
        tvTitle = itemView.findViewById(R.id.tvTitle);
        ivPinned = itemView.findViewById(R.id.ivPinned);

    }
}