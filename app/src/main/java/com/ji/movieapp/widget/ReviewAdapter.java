package com.ji.movieapp.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ji.movieapp.R;

import java.util.ArrayList;

/**
 * Created by jonathanimperato on 17/02/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter {

    ArrayList<Review> reviews;
    Context mContext;

    public ReviewAdapter(ArrayList<Review> reviews, Context mContext) {
        this.reviews = reviews;
        this.mContext = mContext;
    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.review_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ReviewAdapterViewHolder) holder).author.setText(reviews.get(position).getAuthor());
        ((ReviewAdapterViewHolder) holder).content.setText(reviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return  (reviews == null || reviews.size() == 0) ? 0 : reviews.size();
    }

    private class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView author, content;
        public ReviewAdapterViewHolder(View view) {
            super(view);
            author = view.findViewById(R.id.author);
            content = view.findViewById(R.id.content);
        }

    }
}
