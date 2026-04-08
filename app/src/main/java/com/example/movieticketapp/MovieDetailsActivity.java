package com.example.movieticketapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.movieticketapp.model.Movie;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class MovieDetailsActivity extends AppCompatActivity {

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movie = (Movie) getIntent().getSerializableExtra("movie");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        ImageView ivMoviePoster = findViewById(R.id.ivMoviePoster);
        TextView tvMovieTitle = findViewById(R.id.tvMovieTitle);
        TextView tvMovieGenre = findViewById(R.id.tvMovieGenre);
        TextView tvMovieRating = findViewById(R.id.tvMovieRating);
        TextView tvMovieDescription = findViewById(R.id.tvMovieDescription);
        Button btnBookNow = findViewById(R.id.btnBookNow);

        if (movie != null) {
            collapsingToolbar.setTitle(movie.getTitle());
            tvMovieTitle.setText(movie.getTitle());
            tvMovieGenre.setText(movie.getGenre());
            tvMovieRating.setText(String.valueOf(movie.getRating()));
            tvMovieDescription.setText(movie.getDescription());

            Glide.with(this)
                    .load(movie.getPosterUrl())
                    .into(ivMoviePoster);
        }

        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailsActivity.this, BookingActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
        });
    }
}
