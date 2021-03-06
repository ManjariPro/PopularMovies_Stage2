package com.propelld.app.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by manjari on 20/5/18.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>
{
    private ArrayList<Review> reviews;

    ReviewAdapter(ArrayList<Review> reviews)
    {
        this.reviews = reviews;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtReviewContentView;

        public ReviewViewHolder(View view)
        {
            super(view);
            txtReviewContentView = (TextView) view.findViewById(R.id.reviewContent);
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.reviewrow, parent, false);

        ReviewViewHolder reviewViewHolder = new ReviewViewHolder(view);

        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position)
    {
        Review review = reviews.get(position);

        holder.txtReviewContentView.setText(review.getContent());
    }

    @Override
    public int getItemCount()
    {
        return reviews.size();
    }
}