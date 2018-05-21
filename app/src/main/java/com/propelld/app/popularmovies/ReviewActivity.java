package com.propelld.app.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity
{
    RecyclerView reviewRecyclerView;
    ReviewAdapter reviewAdapter;
    ArrayList<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.review);
        setContentView(R.layout.activity_review);

        reviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerReviews);

        ReviewLoadTask reviewLoadTask = new ReviewLoadTask();
        //Get the Adapter
        reviews = new ArrayList<Review>();
        reviewAdapter = new ReviewAdapter(reviews);

        //Get the RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        reviewRecyclerView.setLayoutManager(layoutManager);
        //Connect the Adapter
        reviewRecyclerView.setAdapter(reviewAdapter);

        // get movieId from intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int movieId = bundle.getInt("id");

        reviewLoadTask.execute(Configuration.Movie_Review
                + Integer.toString(movieId)
                + Configuration.Review);
    }

    public class ReviewLoadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            StringBuilder stringBuilder = new StringBuilder();

            try
            {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line;

                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return stringBuilder.toString();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            try
            {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    String content = jsonObj.getString("content");
                    String author = jsonObj.getString("author");

                    Review review = new Review();
                    review.setContent(content);
                    review.setAuthor(author);

                    reviews.add(review);
                }

                reviewAdapter.notifyDataSetChanged();

            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}