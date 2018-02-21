package com.ji.movieapp.widget;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jonathanimperato on 17/02/18.
 */

public class Review implements Parcelable{
    String author;
    String content;
    String id;

    public Review(String author, String content, String id) {
        this.author = author;
        this.content = content;
        this.id = id;
    }

    protected Review(Parcel in) {
        author = in.readString();
        content = in.readString();
        id = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(id);
    }
}
