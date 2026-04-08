package com.example.movieticketapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.movieticketapp.model.Ticket;
import com.google.firebase.firestore.FirebaseFirestore;

public class TicketActivity extends AppCompatActivity {

    private TextView tvMovieTitle, tvTheater, tvTime, tvSeat, tvPrice;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvTheater = findViewById(R.id.tvTheater);
        tvTime = findViewById(R.id.tvTime);
        tvSeat = findViewById(R.id.tvSeat);
        tvPrice = findViewById(R.id.tvPrice);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        String ticketId = getIntent().getStringExtra("ticketId");
        if (ticketId != null) {
            fetchTicketDetails(ticketId);
        }

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(TicketActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void fetchTicketDetails(String ticketId) {
        db.collection("tickets").document(ticketId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Ticket ticket = documentSnapshot.toObject(Ticket.class);
                    if (ticket != null) {
                        tvMovieTitle.setText(ticket.getMovieTitle());
                        tvTheater.setText(ticket.getTheaterName());
                        tvTime.setText("Time: " + ticket.getTime());
                        tvSeat.setText("Seat: " + ticket.getSeatNumber());
                        tvPrice.setText("Total: " + String.format("%,.0f", ticket.getTotalPrice()) + " VND");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching ticket", Toast.LENGTH_SHORT).show());
    }
}
