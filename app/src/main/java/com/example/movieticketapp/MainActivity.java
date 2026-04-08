package com.example.movieticketapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticketapp.adapter.MovieAdapter;
import com.example.movieticketapp.model.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {

    private RecyclerView rvMovies;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        toolbar.setNavigationOnClickListener(null);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.action_my_tickets) {
                startActivity(new Intent(MainActivity.this, MyTicketsActivity.class));
                return true;
            }
            return false;
        });

        rvMovies = findViewById(R.id.rvMovies);
        progressBar = findViewById(R.id.progressBar);

        movieList = new ArrayList<>();
        adapter = new MovieAdapter(movieList, this);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        fetchMovies();
    }

    private void fetchMovies() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("movies")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        movieList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Movie movie = document.toObject(Movie.class);
                            movie.setId(document.getId());
                            // Force update poster URL if it matches old small resolution (optional, to ensure images load)
                            if (movie.getPosterUrl() != null && movie.getPosterUrl().contains("w500")) {
                                movie.setPosterUrl(movie.getPosterUrl().replace("w500", "w780"));
                            }
                            movieList.add(movie);
                        }
                        adapter.notifyDataSetChanged();
                        
                        if (movieList.isEmpty()) {
                            seedData();
                        } else if (movieList.get(0).getPosterUrl().contains("w500")) {
                            deleteOldAndSeed();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void seedData() {
        Movie m1 = new Movie("", "Avengers: Endgame", "After the devastating events of Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more in order to restore balance to the universe.", "https://image.tmdb.org/t/p/w780/or06vSqzWkaG0Cc7p0q97G3qytB.jpg", 4.8, "Action, Sci-Fi");
        Movie m2 = new Movie("", "Inception", "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.", "https://image.tmdb.org/t/p/w780/9gk7Fn9sVAsS969O9oqysOE3HBo.jpg", 4.7, "Sci-Fi, Thriller");
        Movie m3 = new Movie("", "Interstellar", "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.", "https://image.tmdb.org/t/p/w780/gEU2QniE6EwfVnzC9vNI6YETrM8.jpg", 4.6, "Adventure, Drama, Sci-Fi");
        Movie m4 = new Movie("", "The Dark Knight", "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.", "https://image.tmdb.org/t/p/w780/qJ2tW6WMUDp9s1vmsTu4X3qTPqy.jpg", 4.9, "Action, Crime, Drama");
        
        db.collection("movies").add(m1);
        db.collection("movies").add(m2);
        db.collection("movies").add(m3);
        db.collection("movies").add(m4).addOnCompleteListener(task -> fetchMovies());
    }

    private void deleteOldAndSeed() {
        db.collection("movies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    db.collection("movies").document(document.getId()).delete();
                }
                seedData();
            }
        });
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout || item.getItemId() == android.R.id.home) {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_my_tickets) {
            startActivity(new Intent(MainActivity.this, MyTicketsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
