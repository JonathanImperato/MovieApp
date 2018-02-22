package com.ji.movieapp.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ji.movieapp.DetailActivity;
import com.ji.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jonathanimperato on 15/02/18.
 */

public class MoviesAdapter extends RecyclerView.Adapter {
    private final Context mContext;
    private final ArrayList<Movie> mMovies;

    public MoviesAdapter(Context mContext, ArrayList<Movie> mMovies) {
        this.mContext = mContext;
        this.mMovies = mMovies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.movie_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);
        view.setClickable(true);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        Picasso.with(mContext).load(mMovies.get(position).getImage()).into(((MovieAdapterViewHolder) holder).thumbnail);

    }

    @Override
    public int getItemCount() {
        return (mMovies == null || mMovies.size() == 0) ? 0 : mMovies.size();
    }


    private class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView thumbnail;

        public MovieAdapterViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovies.get(adapterPosition);
            if (view.getId() == R.id.thumbnail) {
                animateIntent(view, movie);
            }
        }


        public void animateIntent(View view, Movie movie) {

            // Ordinary Intent for launching a new activity
            Intent intent =
                    new Intent(mContext, DetailActivity.class).putExtra("movie", movie);

            // Get the transition name from the string
            String transitionName = mContext.getString(R.string.transition_string);

            // Define the view that the animation will start from
            View viewStart = view.findViewById(R.id.thumbnail);

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, viewStart, transitionName);

            //Start the Intent
            ActivityCompat.startActivity(mContext, intent, options.toBundle());
        }
    }
}
