package com.ji.movieapp.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ji.movieapp.R;

import java.util.ArrayList;

/**
 * Created by jonathanimperato on 19/02/18.
 */

public class VideoAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<String> video;


    public VideoAdapter(Context mContext, ArrayList<String> video) {
        this.mContext = mContext;
        this.video = video;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.video_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        return new VideoAdapter.VideoAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((VideoAdapter.VideoAdapterViewHolder) holder).text.setText(mContext.getString(R.string.play_trailer) + String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return video != null ? video.size() : 0;
    }

    private class VideoAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageButton playBtn;
        TextView text;

        public VideoAdapterViewHolder(View view) {
            super(view);
            playBtn = view.findViewById(R.id.playBtn);
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.get(getAdapterPosition())));
                    mContext.startActivity(browserIntent);
                }
            });
            text = view.findViewById(R.id.trailern);

        }

    }
}