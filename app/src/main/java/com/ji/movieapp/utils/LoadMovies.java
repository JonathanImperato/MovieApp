package com.ji.movieapp.utils;

import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

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
 * Created by jonathanimperato on 15/02/18.
 */

public class LoadMovies extends android.support.v4.content.AsyncTaskLoader<ArrayList<Movie>> {

    static Context mContext;

    String KEY = "YOUR KEY HERE";
    public LoadMovies(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public ArrayList<Movie> loadInBackground() {
        return getMovies();
    }

    @Override
    public void deliverResult(ArrayList<Movie> data) {
        super.deliverResult(data);
    }


    String getSelectedSortingMethod() {
        Context context = getContext();
        String sorting = PreferenceManager.getDefaultSharedPreferences(context).getString("sorting", "");
        if (sorting.length() < 1) return "popular"; //default value
        return sorting;
    }


    private java.net.URL buildUrl() {
        String sorting = getSelectedSortingMethod();
        Log.w("Sorting", sorting);
        String BASE_URL = "https://api.themoviedb.org/3/movie/" + sorting + "?api_key=";

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

    public ArrayList<Movie> getMovies() {
        try {
            URL RequestURL = buildUrl();
            String jsonString = getResponseFromHttpUrl(RequestURL);
            ArrayList<Movie> movieL = getMoviesList(jsonString);
            return movieL;

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
            return null;
        }
    }


    public ArrayList<Movie> getMoviesList(String jsonString) {
        ArrayList<Movie> moviesList = new ArrayList<>();
        JSONObject root = null;
        try {
            root = new JSONObject(jsonString);
            if (root.has("results")) {
                JSONArray result = root.getJSONArray("results");
                for (int i = 0; i < result.length(); i++) {
                    JSONObject item = result.getJSONObject(i);
                    Movie movie = new Movie();
                    if (item.has("backdrop_path")) {
                        String image = item.optString("backdrop_path");
                        movie.setImage("http://image.tmdb.org/t/p/w780/" + image);
                    }
                    if (item.has("vote_average")) {
                        int rating = item.optInt("vote_average");
                        movie.setRating(rating);
                    }
                    if (item.has("title")) {
                        String title = item.optString("title");
                        movie.setName(title);
                    }
                    if (item.has("overview")) {
                        String desc = item.optString("overview");
                        movie.setBriefDescription(desc);
                    }
                    if (item.has("release_date")) {
                        String release_date = item.optString("release_date");
                        movie.setReleaseDate(release_date);
                    }
                    if (item.has("id")) {
                        String id = item.optString("id");
                        movie.setId(id);
                        movie.setVideosUrl(videoUrl(id));
                    }
                    moviesList.add(movie);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return moviesList;
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


    ArrayList<String> videoUrl(String id) {
        URL videoUrl = buildVideoUrl(id);
        String jsonString = getResponseFromHttpUrl(videoUrl);
        ArrayList<String> url = getMovieVideoUrl(jsonString);
        return url.size() > 0 ? url : null;
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
}
