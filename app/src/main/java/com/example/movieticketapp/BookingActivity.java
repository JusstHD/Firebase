package com.example.movieticketapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.adapter.SeatAdapter;
import com.example.movieticketapp.adapter.ShowtimeAdapter;
import com.example.movieticketapp.model.Movie;
import com.example.movieticketapp.model.Showtime;
import com.example.movieticketapp.model.Theater;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.example.movieticketapp.model.Ticket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingActivity extends AppCompatActivity {

    private Movie movie;
    private Spinner spinnerTheater;
    private RecyclerView rvShowtimes, rvSeats;
    private ShowtimeAdapter showtimeAdapter;
    private SeatAdapter seatAdapter;
    private List<Showtime> showtimeList;
    private List<String> seatList;
    private List<Theater> theaterList;
    private List<String> theaterNames;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        movie = (Movie) getIntent().getSerializableExtra("movie");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        createNotificationChannel();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        TextView tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvMovieTitle.setText(movie != null ? movie.getTitle() : "Movie Title");

        spinnerTheater = findViewById(R.id.spinnerTheater);
        rvShowtimes = findViewById(R.id.rvShowtimes);
        rvSeats = findViewById(R.id.rvSeats);
        progressBar = findViewById(R.id.progressBar);
        Button btnConfirm = findViewById(R.id.btnConfirm);

        showtimeList = new ArrayList<>();
        showtimeAdapter = new ShowtimeAdapter(showtimeList, showtime -> {
            // Showtime selected
        });
        rvShowtimes.setLayoutManager(new GridLayoutManager(this, 3));
        rvShowtimes.setAdapter(showtimeAdapter);

        seatList = new ArrayList<>();
        for (int i = 1; i <= 24; i++) {
            seatList.add("A" + i);
        }
        seatAdapter = new SeatAdapter(seatList, seat -> {
            // Seat selected
        });
        rvSeats.setLayoutManager(new GridLayoutManager(this, 6));
        rvSeats.setAdapter(seatAdapter);

        fetchTheaters();

        btnConfirm.setOnClickListener(v -> bookTicket());
    }

    private void fetchTheaters() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("theaters").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                theaterList = new ArrayList<>();
                theaterNames = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Theater t = doc.toObject(Theater.class);
                    t.setId(doc.getId());
                    theaterList.add(t);
                    theaterNames.add(t.getName());
                }
                
                if (theaterList.isEmpty()) {
                    seedTheaters();
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, theaterNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerTheater.setAdapter(adapter);
                    fetchShowtimes();
                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void seedTheaters() {
        Theater t1 = new Theater("", "CGV Vincom", "District 1");
        Theater t2 = new Theater("", "Lotte Cinema", "District 7");
        db.collection("theaters").add(t1);
        db.collection("theaters").add(t2).addOnCompleteListener(task -> fetchTheaters());
    }

    private void fetchShowtimes() {
        // In a real app, filter by movie and theater. For demo, just show some.
        showtimeList.clear();
        showtimeList.add(new Showtime("1", movie.getId(), "t1", "10:00 AM", 120000));
        showtimeList.add(new Showtime("2", movie.getId(), "t1", "01:30 PM", 120000));
        showtimeList.add(new Showtime("3", movie.getId(), "t1", "04:45 PM", 150000));
        showtimeList.add(new Showtime("4", movie.getId(), "t1", "08:00 PM", 150000));
        showtimeAdapter.notifyDataSetChanged();
    }

    private void bookTicket() {
        Showtime selectedShowtime = showtimeAdapter.getSelectedShowtime();
        String selectedSeat = seatAdapter.getSelectedSeat();

        if (selectedShowtime == null) {
            Toast.makeText(this, "Please select a showtime", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedSeat == null) {
            Toast.makeText(this, "Please select a seat", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String userId = mAuth.getCurrentUser().getUid();
        String ticketId = UUID.randomUUID().toString();
        
        Ticket ticket = new Ticket(
                ticketId,
                userId,
                selectedShowtime.getId(),
                movie.getTitle(),
                spinnerTheater.getSelectedItem().toString(),
                selectedShowtime.getTime(),
                selectedSeat,
                selectedShowtime.getPrice()
        );

        db.collection("tickets").document(ticketId).set(ticket)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        showNotification(ticket);
                        Toast.makeText(BookingActivity.this, "Ticket Booked!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BookingActivity.this, TicketActivity.class);
                        intent.putExtra("ticketId", ticketId);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(BookingActivity.this, "Booking failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showNotification(Ticket ticket) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                // Even if not granted now, the booking is saved. The user will see it in My Tickets.
                return;
            }
        }

        String channelId = "booking_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Booking Confirmed!")
                .setContentText("You booked: " + ticket.getMovieTitle() + " at " + ticket.getTime())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "booking_channel";
            CharSequence name = "Booking Notifications";
            String description = "Notifications for ticket bookings";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
