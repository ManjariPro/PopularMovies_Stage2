package com.propelld.app.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
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

public class MovieDetails extends AppCompatActivity
{
    ImageButton imageButton;
    SQLiteDatabase db;
    YouTubeAdapter youtubeAdapter;
    ArrayList<YouTubeTrailer> trailerList;
    boolean isFavourite = false;
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.detail);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView title = (TextView) findViewById(R.id.original_title);
        TextView overview = (TextView) findViewById(R.id.overview);
        TextView releaseDate = (TextView) findViewById(R.id.releasedate);
        TextView voteAvg = (TextView) findViewById(R.id.voteAverage);
        ImageView imageView = (ImageView) findViewById(R.id.thumbnail);
        imageButton = (ImageButton) findViewById(R.id.favorite);
        ImageView playImg = (ImageView) findViewById(R.id.playImage);

        Intent intent = getIntent();
        movie = (Movie) intent.getParcelableExtra("movie");

        // setting the content
        Picasso.with(getApplicationContext())
                .load(Configuration.IMAGE_BASE_URL + movie.getPoster_path())
                .into(imageView);

        title.setText(movie.getOriginal_title());
        overview.setText(movie.getOverview());
        releaseDate.setText(movie.getRelease_date());
        voteAvg.setText(movie.getVote_average() + "/10");

        // Checking if the movie was marked as favourite
        FavoriteMovies favoriteMovies = new FavoriteMovies(this);
        db = favoriteMovies.getWritableDatabase();

        if (isStarMarked(movie.getId()))
        {
            imageButton.setBackgroundColor(Color.YELLOW);
            isFavourite = true;
        }
        else
        {
            imageButton.setBackgroundColor(Color.WHITE);
        }

        // Load the trailer list in recycler view
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerYoutube);
        YoutubeTask youtubeTask = new YoutubeTask();

        trailerList = new ArrayList<YouTubeTrailer>();
        youtubeAdapter = new YouTubeAdapter(this, trailerList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(youtubeAdapter);

        youtubeTask.execute(Configuration.Movie_Trailer
                + movie.getId()
                + Configuration.Movie);
    }

    public void reviews(View view)
    {
        Intent intent = new Intent(MovieDetails.this, ReviewActivity.class);
        intent.putExtra("id", movie.getId());
        startActivity(intent);
    }

    public void favoriteMovies(View view)
    {
        if (!isFavourite)
        {
            ContentValues contentValues = new ContentValues();

            contentValues.put(FavoriteMoviesContract.TITLE_COLUMN_NAME, movie.getTitle());
            contentValues.put(FavoriteMoviesContract.ID_COLUMN_NAME, movie.getId());
            contentValues.put(FavoriteMoviesContract.ORIGINAL_TITLE_COLUMN_NAME, movie.getOriginal_title());
            contentValues.put(FavoriteMoviesContract.VOTE_COLUMN_NAME, movie.getVote_average());
            contentValues.put(FavoriteMoviesContract.POSTER_COLUMN_NAME, movie.getPoster_path());
            contentValues.put(FavoriteMoviesContract.OVERVIEW_COLUMN_NAME, movie.getOverview());
            contentValues.put(FavoriteMoviesContract.RELEASEDATE_COLUMN_NAME, movie.getRelease_date());

            getContentResolver().insert(FavoriteMoviesContract.CONTENT_URI_FINAL, contentValues);

            imageButton.setBackgroundColor(Color.YELLOW);
            isFavourite = true;
        }
        else
        {
            deleteStarMarked(movie.getId());
            imageButton.setBackgroundColor(Color.WHITE);
            isFavourite =false;
        }
    }

    public boolean deleteStarMarked(int Id)
    {
        return db.delete(FavoriteMoviesContract.TABLE_NAME,
                FavoriteMoviesContract.ID_COLUMN_NAME + " = "+ Integer.toString(Id) +"",
                null) > 0;
    }

    public boolean isStarMarked(int Id)
    {
        String sql = "SELECT * FROM "
                + FavoriteMoviesContract.TABLE_NAME
                + " WHERE "
                + FavoriteMoviesContract.ID_COLUMN_NAME
                + " = "
                + Integer.toString(Id)
                + "";

        Cursor cursor = db.rawQuery(sql, null);

        boolean result = false;
        if (cursor.getCount() != 0)
        {
            result = true;
        }
        else
        {
            result = false;
        }

        cursor.close();
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
        return true;
    }

    public class YoutubeTask extends AsyncTask<String, Void, String>
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

            YouTubeTrailer trailer;

            try
            {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    String key = jsonObj.getString("key");

                    trailer = new YouTubeTrailer();
                    trailer.setTrailer(getString(R.string.trailer1));
                    trailer.setKey(key);

                    trailerList.add(trailer);
                }

                youtubeAdapter.notifyDataSetChanged();

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}