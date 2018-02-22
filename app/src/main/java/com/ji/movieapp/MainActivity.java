package com.ji.movieapp;

import android.content.Context;
import android.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ji.movieapp.data.MoviesContract;
import com.ji.movieapp.utils.LoadMovies;
import com.ji.movieapp.widget.FavAdapter;
import com.ji.movieapp.widget.Movie;
import com.ji.movieapp.widget.MoviesAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {
    RecyclerView mRecyclerView;
    MoviesAdapter mAdapter;
    FavAdapter mAdapterGrid;
    ProgressBar pBar;
    int TASK_ID = 1;
    int TASK_ID_UPDATE = 2;
    Bundle TASK_BUNDLE = null;
    int numberOfColumns = 2;
    GridView mGridView;
    int CURSOR_LOADER_ID = 3;
    Parcelable RVstate, GVstate;
    GridLayoutManager RVLayoutManager;
    int GridViewLatestPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pBar = findViewById(R.id.pBar);
        RVLayoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerView = findViewById(R.id.listFilms);
        mRecyclerView.setLayoutManager(RVLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mGridView = findViewById(R.id.gridView);
        mGridView.setHorizontalSpacing(0);
        mGridView.setVerticalSpacing(0);

        if (savedInstanceState != null) {
            if (!getSelectedSortingMethod().equals("favourite")) {
                mRecyclerView.scrollTo(savedInstanceState.getInt("ScrollStateX"), savedInstanceState.getInt("ScrollStateY"));
                setUpRecyclerView(TASK_ID);
            } else {
                //mGridView.scrollTo(savedInstanceState.getInt("GScrollStateX"), savedInstanceState.getInt("GScrollStateY"));
                mGridView.setSelection(savedInstanceState.getInt("G"));
                if (GridViewLatestPosition > 0)
                    mGridView.setSelection(GridViewLatestPosition);
            }
        } else {
            if (isInternetAvailable()) {
                setUpRecyclerView(TASK_ID);
            } else {
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                setUpGridView();
                getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sorting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle args) {//i = CURSOR LOADER ID
        return new CursorLoader(this,
                MoviesContract.MoviesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mAdapterGrid.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mAdapterGrid.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_by_rating:
                if (isInternetAvailable()) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                    setSelectedSortingMethod("top_rated");
                    setUpRecyclerView(TASK_ID_UPDATE);
                    mRecyclerView.scrollToPosition(0); //scroll to top
                }
                break;
            case R.id.sort_by_popularity:
                if (isInternetAvailable()) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mGridView.setVisibility(View.GONE);
                    setSelectedSortingMethod("popular");
                    setUpRecyclerView(TASK_ID_UPDATE);
                    mRecyclerView.scrollToPosition(0); //scroll to top
                }
                break;
            case R.id.fav:
                setUpGridView();
                setSelectedSortingMethod("favourite");
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this); //this let the user update his fav list
                break;
        }
        return super.onOptionsItemSelected(item);
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

    void setUpGridView() {
        pBar.setVisibility(View.VISIBLE);
        mAdapterGrid = new FavAdapter(this, null, 0, CURSOR_LOADER_ID);
        mRecyclerView.setVisibility(View.GONE);
        mGridView.setVisibility(View.VISIBLE);
        mGridView.setAdapter(mAdapterGrid);
        pBar.setVisibility(View.GONE);

        // if (getLoaderManager().getLoader(CURSOR_LOADER_ID) == null) //THIS LET ME UPDATE THE LIST AFTER ADDED SOME NEW FAVS
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        //  else
        //     getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    void setUpRecyclerView(int id) {
        mAdapter = null;
        mRecyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(id, TASK_BUNDLE, new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {
            @Override
            public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
                pBar.setVisibility(View.VISIBLE);
                LoadMovies asyncTaskLoader = new LoadMovies(getApplicationContext());
                asyncTaskLoader.forceLoad();
                return asyncTaskLoader;
            }

            @Override
            public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
                pBar.setVisibility(View.GONE);
                mAdapter = new MoviesAdapter(MainActivity.this, data);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
                pBar.setVisibility(View.GONE);
            }
        });
    }

    void setSelectedSortingMethod(String sortingMethod) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sorting", sortingMethod);
        editor.apply();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecyclerView.getVisibility() == View.VISIBLE) { //IT MEANS I AM NOT USING GRIDVIEW
            outState.putInt("ScrollStateX", mRecyclerView.getScrollX());
            outState.putInt("ScrollStateY", mRecyclerView.getScrollY());
            if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
                RVstate = RVLayoutManager.onSaveInstanceState();
                outState.putParcelable("RVstate", RVstate);
                Log.d("TEST", "saving state");
            }
        } else {
            outState.putInt("GScrollStateX", mGridView.getScrollX());
            outState.putInt("GScrollStateY", mGridView.getScrollY());
            outState.putInt("G", mGridView.getFirstVisiblePosition());
            GridViewLatestPosition = mGridView.getFirstVisiblePosition();
            if (mGridView != null) {
                GVstate = mGridView.onSaveInstanceState();
                outState.putParcelable("GVstate", GVstate);
            }

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            RVstate = savedInstanceState.getParcelable("RVstate");
            GVstate = savedInstanceState.getParcelable("GVstate");

            Log.d("TEST", "restoring state");
            RVLayoutManager.onRestoreInstanceState(RVstate);
        }
        if (mRecyclerView.getVisibility() == View.VISIBLE) { //IT MEANS I AM NOT USING GRIDVIEW
            mRecyclerView.scrollTo(savedInstanceState.getInt("ScrollStateX"), savedInstanceState.getInt("ScrollStateY"));
        } else {
            //mGridView.scrollTo(savedInstanceState.getInt("GScrollStateX"), savedInstanceState.getInt("GScrollStateY"));
            mGridView.setSelection(savedInstanceState.getInt("G"));
        }
    }

    String getSelectedSortingMethod() {
        String sorting = PreferenceManager.getDefaultSharedPreferences(this).getString("sorting", "");
        if (sorting.length() < 1) return "popular"; //default value
        return sorting;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (RVstate != null && mRecyclerView != null) {
            RVLayoutManager.onRestoreInstanceState(RVstate);
        }
        if (GVstate != null && mGridView != null) {
            mGridView.onRestoreInstanceState(GVstate);
        }

        if (getSelectedSortingMethod().equals("favourite") || mGridView.getVisibility() == View.VISIBLE) { //IF TRUE IT MEANS WE ARE IN FAVOURITE LIST
            setUpGridView(); //this will update the data even if i remove a new favourite from the detail activity
            if (GridViewLatestPosition > 0)
                mGridView.setSelection(GridViewLatestPosition);
        }
    }
}