package com.ji.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ji.movieapp.data.MoviesContract;
import com.ji.movieapp.utils.LoadReviews;
import com.ji.movieapp.utils.LoadSingleMovie;
import com.ji.movieapp.widget.Movie;
import com.ji.movieapp.widget.Review;
import com.ji.movieapp.widget.ReviewAdapter;
import com.ji.movieapp.widget.VideoAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    ImageView toolBarImage, imageView;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView description, rating, date, reviewLabel, trailerLabel, trailerLabelTollbar;
    ImageButton playButton;
    RecyclerView mRecyclerView;
    RecyclerView mRecyclerViewTrailers;
    ReviewAdapter mAdapter;
    VideoAdapter videoAdapter;
    int REVIEW_LOADER_ID = 14;
    int FAV_LOADER_ID = 22;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        toolBarImage = findViewById(R.id.toolbarimage);
        trailerLabel = findViewById(R.id.trailers);
        reviewLabel = findViewById(R.id.reviews);
        imageView = findViewById(R.id.imageView);
        playButton = findViewById(R.id.play);
        description = findViewById(R.id.description);
        rating = findViewById(R.id.rating);
        date = findViewById(R.id.date);
        trailerLabelTollbar = findViewById(R.id.trailerLabel);
        mRecyclerView = findViewById(R.id.rv);
        mRecyclerViewTrailers = findViewById(R.id.videoRV);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        if (getIntent().getExtras().getParcelable("movie") != null) {
            movie = getIntent().getExtras().getParcelable("movie");

            mRecyclerView.setHasFixedSize(true);
            mRecyclerViewTrailers.setHasFixedSize(true);
            ArrayList<String> videos = movie.getVideosUrl();
            videoAdapter = new VideoAdapter(this, videos);
            mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            mRecyclerViewTrailers.setAdapter(videoAdapter);
            toolbar.setTitle(movie.getName());
            collapsingToolbarLayout.setTitle(movie.getName());
            Picasso.with(this).load(movie.getImage()).into(imageView);
            Picasso.with(this).load(movie.getImage()).into(toolBarImage);
            description.setText(movie.getBriefDescription());
            rating.setText(getString(R.string.rating) + movie.getRating());
            date.setText(getString(R.string.date) + movie.getReleaseDate());

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            setUpRecyclerView();

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (movie.getVideosUrl() != null && movie.getVideosUrl().size() > 0) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getVideosUrl().get(0)));
                        startActivity(browserIntent);
                    } else
                        Toast.makeText(DetailActivity.this, getString(R.string.no_trailer), Toast.LENGTH_LONG).show();
                }
            });

        } else if (getIntent().getExtras().getParcelable("movie_fav") != null) {

            Movie myM = getIntent().getExtras().getParcelable("movie_fav");
            movie = myM;
            toolbar.setTitle(myM.getName());
            collapsingToolbarLayout.setTitle(myM.getName());
            Picasso.with(this).load(myM.getImage()).into(imageView);
            Picasso.with(this).load(myM.getImage()).into(toolBarImage);
            description.setText(myM.getBriefDescription());
            if (isInternetAvailable()) updateDataFromOfflineToOnline();
        }

    }

    void updateDataFromOfflineToOnline() {

        if (getSupportLoaderManager().getLoader(FAV_LOADER_ID) == null || !getSupportLoaderManager().getLoader(FAV_LOADER_ID).isStarted())
            getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, new LoaderManager.LoaderCallbacks<Movie>() {
                @Override
                public Loader<Movie> onCreateLoader(int id, Bundle args) {
                    LoadSingleMovie ll = new LoadSingleMovie(getApplicationContext(), movie.getId());
                    ll.forceLoad();
                    return ll;
                }

                @Override
                public void onLoadFinished(Loader<Movie> loader, final Movie data) {
                    if (data == null) {
                        reviewLabel.setVisibility(View.GONE);
                        trailerLabelTollbar.setVisibility(View.GONE);
                        trailerLabel.setVisibility(View.GONE);
                        mRecyclerViewTrailers.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.GONE);
                        playButton.setVisibility(View.GONE);
                    } else {

                        rating.setText(getString(R.string.rating) + data.getRating());
                        date.setText(getString(R.string.date) + data.getReleaseDate());
                        playButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (data.getVideosUrl() != null && data.getVideosUrl().size() > 0) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getVideosUrl().get(0)));
                                    startActivity(browserIntent);
                                } else
                                    Toast.makeText(DetailActivity.this, getString(R.string.no_trailer), Toast.LENGTH_LONG).show();
                            }
                        });
                        setUpRecyclerView();
                        ArrayList<String> videoList = data.getVideosUrl();
                        if (videoList.size() > 0) {
                            mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            VideoAdapter videoAdapter1 = new VideoAdapter(DetailActivity.this, videoList);
                            mRecyclerViewTrailers.setAdapter(videoAdapter1);
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<Movie> loader) {

                }
            });
    }

    boolean isInternetAvailable() {

        /**
         * FOLLOWED THIS LINK:
         * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
         * ACCORDING TO https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true#h.3omxhyonl2o1
         * CODE AUTHOR: STACKOVERFLOW USER GAR
         */

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        movie = null;
    }

    public void insertData() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getName());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getId());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_DESCRIPTION, movie.getBriefDescription());
        movieValues.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_URL, movie.getImage());

        //     Toast.makeText(this, movie.getId(), Toast.LENGTH_SHORT).show();
        // bulkInsert our ContentValues array
        this.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI,
                new ContentValues[]{movieValues});
    }

    void removeData() {
        this.getContentResolver().delete(MoviesContract.MoviesEntry.CONTENT_URI, MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = " + movie.getId(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        if (isFavourite()) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_24dp));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_fav:
                if (isFavourite()) {
                    Toast.makeText(this, R.string.fav_removed, Toast.LENGTH_SHORT).show();
                    removeData();
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_border_24dp));
                } else {
                    Toast.makeText(this, R.string.fav_added, Toast.LENGTH_SHORT).show();
                    insertData();
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_star_24dp));
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    void setUpRecyclerView() {
        mAdapter = null;
        mRecyclerView.setAdapter(mAdapter);
        if (getSupportLoaderManager().getLoader(REVIEW_LOADER_ID) == null || !getSupportLoaderManager().getLoader(REVIEW_LOADER_ID).isStarted())
            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, new LoaderManager.LoaderCallbacks<ArrayList<Review>>() {
                @Override
                public Loader<ArrayList<Review>> onCreateLoader(int id, Bundle args) {
                    Log.wtf("WTF", movie.getId());
                    LoadReviews asyncTaskLoader = new LoadReviews(getApplicationContext(), movie.getId());
                    asyncTaskLoader.forceLoad();
                    return asyncTaskLoader;
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {

                    mRecyclerView.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
                    ReviewAdapter adapter = new ReviewAdapter(data, DetailActivity.this);
                    mAdapter = adapter;
                    mRecyclerView.setAdapter(adapter);
                    //Log.wtf("SIZE", String.valueOf(data.size()));

                }

                @Override
                public void onLoaderReset(Loader<ArrayList<Review>> loader) {

                }
            });

    }

    boolean isFavourite() {
        if (getIntent().getExtras().getParcelable("movie") != null) { //it means it is not a favourite
            Cursor c = this.getContentResolver().query(
                    MoviesContract.MoviesEntry.CONTENT_URI,
                    null,
                    MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = " + movie.getId(),
                    null,
                    null);
            return c.getCount() > 0;
        } else return true;
    }
}
