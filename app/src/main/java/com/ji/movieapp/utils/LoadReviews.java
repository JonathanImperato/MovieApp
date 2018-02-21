package com.ji.movieapp.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.ji.movieapp.widget.Review;

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
 * Created by jonathanimperato on 17/02/18.
 */

public class LoadReviews extends android.support.v4.content.AsyncTaskLoader<ArrayList<Review>> {

    static Context mContext;
    static String id;

    String KEY = "YOUR KEY HERE";
    public LoadReviews(Context context, String _id) {
        super(context);
        mContext = context;
        id = _id;
    }

    @Override
    public ArrayList<Review> loadInBackground() {
        return getReviews(id);
    }

    @Override
    public void deliverResult(ArrayList<Review> data) {
        super.deliverResult(data);
    }


    private java.net.URL buildReviewsUrl(String id) {
        String BASE_URL = "https://api.themoviedb.org/3/movie/" + id + "/reviews" + "?api_key=";
        Uri moviesUri = Uri.parse(BASE_URL + KEY).buildUpon()
                //  .appendQueryParameter("app_key", KEY)
                .build();

        try {
            URL movieUrl = new URL(moviesUri.toString());
            Log.w("url", movieUrl.toString());
            return movieUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public ArrayList<Review> getMovieReviewsUrl(String jsonString) {
        ArrayList<Review> reviewsList = new ArrayList<>();
        JSONObject root = null;
        String id = null, author = null, content = null;
        if (jsonString != null && jsonString.length() > 0)
            try {
                root = new JSONObject(jsonString);
                if (root.has("results")) {
                    JSONArray result = root.getJSONArray("results");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject item = result.getJSONObject(i);
                        if (item.has("id")) {
                            id = item.optString("id");
                        }
                        if (item.has("author")) {
                            author = item.optString("author");
                        }
                        if (item.has("content")) {
                            content = item.optString("content");
                        }
                        Review review = new Review(author, content, id);
                        reviewsList.add(review);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return reviewsList;
    }

    ArrayList<Review> getReviews(String id) {
        URL url = buildReviewsUrl(id);
        String jsonString = getResponseFromHttpUrl(url);
        return getMovieReviewsUrl(jsonString).size() > 0 ? getMovieReviewsUrl(jsonString) : null;
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
