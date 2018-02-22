package com.ji.movieapp.widget;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by jonathanimperato on 15/02/18.
 */

public class Movie implements Parcelable {
    String name;
    String image;
    String releaseDate;
    String briefDescription;
    ArrayList<String> videosUrl;
    String id;
    double rating;

    public Movie() {
    }

    public Movie(String name, String image, String releaseDate, String briefDescription, ArrayList<String> videosUrl, String id, int rating) {
        this.name = name;
        this.image = image;
        this.releaseDate = releaseDate;
        this.briefDescription = briefDescription;
        this.videosUrl = videosUrl;
        this.id = id;
        this.rating = rating;
    }


    protected Movie(Parcel in) {
        name = in.readString();
        image = in.readString();
        releaseDate = in.readString();
        briefDescription = in.readString();
        videosUrl = in.createStringArrayList();
        id = in.readString();
        rating = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getName() {
        return name;
    }

    public ArrayList<String> getVideosUrl() {
        return videosUrl;
    }

    public void setVideosUrl(ArrayList<String> videosUrl) {
        this.videosUrl = videosUrl;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(releaseDate);
        parcel.writeString(briefDescription);
        parcel.writeStringList(videosUrl);
        parcel.writeString(id);
        parcel.writeDouble(rating);
    }
}
