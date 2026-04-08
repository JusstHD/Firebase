package com.example.movieticketapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.R;

import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private List<String> seatList;
    private int selectedPosition = -1;
    private OnSeatClickListener listener;

    public interface OnSeatClickListener {
        void onSeatClick(String seat);
    }

    public SeatAdapter(List<String> seatList, OnSeatClickListener listener) {
        this.seatList = seatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        String seat = seatList.get(position);
        holder.tvSeat.setText(seat);

        if (selectedPosition == position) {
            holder.tvSeat.setBackgroundResource(R.drawable.seat_selected);
            holder.tvSeat.setTextColor(Color.WHITE);
        } else {
            holder.tvSeat.setBackgroundResource(R.drawable.seat_available);
            holder.tvSeat.setTextColor(Color.GRAY);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onSeatClick(seat);
        });
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public String getSelectedSeat() {
        if (selectedPosition != -1) {
            return seatList.get(selectedPosition);
        }
        return null;
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView tvSeat;

        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeat = itemView.findViewById(R.id.tvSeat);
        }
    }
}
