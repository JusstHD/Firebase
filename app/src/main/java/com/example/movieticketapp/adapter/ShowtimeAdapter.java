package com.example.movieticketapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.R;
import com.example.movieticketapp.model.Showtime;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {

    private List<Showtime> showtimeList;
    private int selectedPosition = -1;
    private OnShowtimeClickListener listener;

    public interface OnShowtimeClickListener {
        void onShowtimeClick(Showtime showtime);
    }

    public ShowtimeAdapter(List<Showtime> showtimeList, OnShowtimeClickListener listener) {
        this.showtimeList = showtimeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimeList.get(position);
        holder.tvTime.setText(showtime.getTime());

        if (selectedPosition == position) {
            holder.cardShowtime.setStrokeWidth(4);
            holder.cardShowtime.setCardBackgroundColor(Color.parseColor("#3F0000")); // Darker red
        } else {
            holder.cardShowtime.setStrokeWidth(0);
            holder.cardShowtime.setCardBackgroundColor(Color.parseColor("#2F2F2F")); // Surface color
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onShowtimeClick(showtime);
        });
    }

    @Override
    public int getItemCount() {
        return showtimeList.size();
    }

    public Showtime getSelectedShowtime() {
        if (selectedPosition != -1) {
            return showtimeList.get(selectedPosition);
        }
        return null;
    }

    static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        MaterialCardView cardShowtime;

        public ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            cardShowtime = itemView.findViewById(R.id.cardShowtime);
        }
    }
}
