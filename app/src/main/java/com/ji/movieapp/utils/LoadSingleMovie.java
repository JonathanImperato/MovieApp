package com.ji.movieapp.utils;

import android.content.Context;
import android.net.Uri;

import com.ji.movieapp.R;
import com.ji.movieapp.widget.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jonathanimperato on 21/02/18.
 */

public class LoadSingleMovie extends android.support.v4.content.AsyncTaskLoader<Movie> {

    String id;

    String KEY;

    public LoadSingleMovie(Context context, String movieId) {
        super(context);
        id = movieId;
        KEY = context.getString(R.string.key);
    }

    @Override
    public Movie loadInBackground() {
        return getMovieData(id);
    }

    Movie getMovieData(String movieId) {
        URL movieUrl = buildMovieUrl(movieId);
        String jsonString = getResponseFromHttpUrl(movieUrl);
        Movie movie = getMovie(jsonString, movieId);
        return movie;
    }

    Movie getMovie(String json, String id) {
        Movie movie = new Movie();
        JSONObject root = null;
        try {
            root = new JSONObject(json);
            if (root.has("release_date")) {
                movie.setReleaseDate(root.getString("release_date"));
            }
            if (root.has("vote_average")) {
                movie.setRating(root.getInt("vote_average"));
            }
            movie.setVideosUrl(videoUrl(id));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movie;
    }

    private java.net.URL buildMovieUrl(String id) {
        String BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "?api_key=";

        Uri moviesUri = Uri.parse(BASE_URL + KEY).buildUpon()
                //  .appendQueryParameter("app_key", KEY)
                .build();

        try {
            URL movieUrl = new URL(moviesUri.toString());
            return movieUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getResponseFromHttpUrl(URL url) {
        HttpsURLConnection con = null;
        try {
            URL u = url;
            con = (HttpsURLConnection) u.openConnection();
            con.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }

    ArrayList<String> videoUrl(String id) {
        URL videoUrl = buildVideoUrl(id);
        String jsonString = getResponseFromHttpUrl(videoUrl);
        ArrayList<String> url = getMovieVideoUrl(jsonString);
        return url.size() > 0 ? url : null;
    }

    private java.net.URL buildVideoUrl(String id) {
        String BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "/videos" + "?api_key=";

        Uri moviesUri = Uri.parse(BASE_URL + KEY).buildUpon()
                //  .appendQueryParameter("app_key", KEY)
                .build();

        try {
            URL movieUrl = new URL(moviesUri.toString());
            return movieUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<String> getMovieVideoUrl(String jsonString) {
        String url = "";
        JSONObject root = null;
        ArrayList<String> videoList = new ArrayList<>();
        if (jsonString != null && jsonString.length() > 0)
            try {
                root = new JSONObject(jsonString);
                if (root.has("results")) {
                    JSONArray result = root.getJSONArray("results");
                    if (result.length() > 0)
                        for (int i = 0; i < result.length(); i++) {
                            JSONObject item = result.getJSONObject(i);
                            if (item.has("key")) {
                                String key = item.optString("key");
                                url = ("https://www.youtube.com/watch?v=" + key);
                                videoList.add(url);
                            }
                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return videoList;
    }


}
