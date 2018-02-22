package com.ji.movieapp.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ji.movieapp.DetailActivity;
import com.ji.movieapp.R;
import com.ji.movieapp.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by jonathanimperato on 19/02/18.
 */

public class FavAdapter extends CursorAdapter {
    /**
     * PROJECT BASED ON UDACITY'S CONTENT PROVIDER PROJECT
     * https://github.com/udacity/android-content-provider
     */
    private static final String LOG_TAG = FavAdapter.class.getSimpleName();
    private static Context mContext;
    private static int sLoaderID;
    static String id, title, desc, image;

    public FavAdapter(Context context, Cursor c, int flags, int loaderID) {
        super(context, c, flags);
        Log.d(LOG_TAG, "FavAdapter");
        mContext = context;
        sLoaderID = loaderID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.movie_item;

        Log.d(LOG_TAG, "In new View");

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        Log.d(LOG_TAG, "In bind View");

        int titleIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
        int idIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
        int imgIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMAGE_URL);
        int desIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_DESCRIPTION);
        title = cursor.getString(titleIndex);
        id = cursor.getString(idIndex);
        desc = cursor.getString(desIndex);
        image = cursor.getString(imgIndex);
        Picasso.with(mContext).load(cursor.getString(imgIndex)).into(viewHolder.img);


        final Movie movie = new Movie(title, image, null, desc, null, id, "0");

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ordinary Intent for launching a new activity
                Intent intent =
                        new Intent(mContext, DetailActivity.class).putExtra("movie_fav", movie);

                // Get the transition name from the string
                String transitionName = mContext.getString(R.string.transition_string);

                // Define the view that the animation will start from
                View viewStart = view.findViewById(R.id.thumbnail);

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, viewStart, transitionName);

                //Start the Intent
                ActivityCompat.startActivity(mContext, intent, options.toBundle());
            }
        });

    }

    public static class ViewHolder {
        ImageView img;

        public ViewHolder(View view) {
            img = view.findViewById(R.id.thumbnail);
        }
    }
}
