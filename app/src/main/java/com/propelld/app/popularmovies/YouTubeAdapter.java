package com.propelld.app.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by manjari on 18/5/18.
 */

public class YouTubeAdapter extends RecyclerView.Adapter<YouTubeAdapter.TrailerViewHolder>
{
    Context context;
    ArrayList<YouTubeTrailer> trailers;

    YouTubeAdapter(Context context, ArrayList<YouTubeTrailer> trailers)
    {
        this.context = context;
        this.trailers = trailers;
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView trailer;
        ImageView imageView;

        public TrailerViewHolder(View view)
        {
            super(view);

            trailer = (TextView) view.findViewById(R.id.trail);
            imageView = (ImageView) view.findViewById(R.id.playImage);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int i = getAdapterPosition();

            YouTubeTrailer trailer = trailers.get(i);

            String key = trailer.getKey();

            Context context = v.getContext();

            Intent intent = new Intent(context, YouTube.class);
            intent.putExtra("key", key);
            context.startActivity(intent);
        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.youtubrow, parent, false);

        TrailerViewHolder myViewHolder = new TrailerViewHolder(view);

        return myViewHolder;
    }

    public void onBindViewHolder(TrailerViewHolder holder, int position)
    {
        YouTubeTrailer db = trailers.get(position);

        holder.trailer.setText(db.getTrailer() +" " +(position +1));
    }

    @Override
    public int getItemCount()
    {
        return trailers.size();
    }
}