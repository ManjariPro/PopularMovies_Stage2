package com.propelld.app.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by manjari on 20/3/18.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>
{
    private ArrayList<Movie> movieList;
    private Context context;
    private String baseUrl = Configuration.IMAGE_BASE_URL;

    MoviesAdapter(ArrayList<Movie> movieList, Context context)
    {
        this.movieList = movieList;
        this.context = context;
    }

    public  class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView imageView;

        MoviesViewHolder(View view)
        {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int i = getAdapterPosition();

            Movie movie = movieList.get(i);
            Context context = v.getContext();

            Intent intent = new Intent(context, MovieDetails.class);
            intent.putExtra("movie",movie);
            context.startActivity(intent);
        }
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater  inflater = LayoutInflater.from(context);

        boolean shouldAttachParentImmediately = false;

        View view = inflater.inflate(R.layout.moviesposter, parent,shouldAttachParentImmediately);
        MoviesViewHolder moviesViewHolder = new MoviesViewHolder(view);

        return moviesViewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position)
    {
        Movie movie = movieList.get(position);

        Picasso.with(context)
                .load(Configuration.IMAGE_BASE_URL + movie.getPoster_path())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount()
    {
        return movieList.size();
    }
}