package com.propelld.app.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
{
    SQLiteDatabase sqLiteDatabase;
    RecyclerView recyclerView;
    ArrayList<Movie> moviesArrayList;
    MoviesAdapter moviesAdapter;

    int movieFilterSelected;
    public static final String MOVIE_FILTER_PREFERENCES = "Movie_Filter";
    public static final String MOVIE_FILTER_KEY = "MovieFilterKey";
    SharedPreferences sharedpreferences;;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTitle(R.string.pop);
        setContentView(R.layout.activity_main);

        moviesArrayList = new ArrayList<Movie>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        moviesAdapter = new MoviesAdapter(moviesArrayList, MainActivity.this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, Configuration.NO_OF_COLUMN_IN_GRID));
        recyclerView.setAdapter(moviesAdapter);

        if (savedInstanceState != null
                && (savedInstanceState.getSerializable("movies") != null))
        {
            ArrayList<Movie> movies =
                    (ArrayList<Movie>)savedInstanceState.getSerializable("movies");
            String title = savedInstanceState.getString("title");

            setTitle(title);

            moviesArrayList.clear();
            for(Movie movie: movies)
            {
                moviesArrayList.add(movie);
            }
            moviesAdapter.notifyDataSetChanged();
        }
        else
        {
            sharedpreferences =
                    getSharedPreferences(MOVIE_FILTER_PREFERENCES, Context.MODE_PRIVATE);

            int filter = sharedpreferences.getInt(MOVIE_FILTER_KEY, -1);

            if (filter > 0 )
            {
                applyFilter(filter);
            }
            else
            {
                try
                {
                    MoviesTask moviesTask = new MoviesTask();
                    moviesTask
                            .execute(Configuration.POPULAR_MOVIES_URL)
                            .get();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch (ExecutionException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("movies", moviesArrayList);
        state.putString("title",getTitle().toString());

        // Storing the filter selection for later to show
        // the same sate being closed.
        if (movieFilterSelected != 0)
        {
            sharedpreferences =
                    getSharedPreferences(MOVIE_FILTER_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(MOVIE_FILTER_KEY, movieFilterSelected);
            editor.commit();
        }
    }

    public  class  MoviesTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... strings)
        {
            StringBuilder stringBuilder = new StringBuilder();

            try
            {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();
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

            return stringBuilder.toString() ;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            try
            {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                moviesArrayList.clear();

                for ( int i = 0; i <jsonArray.length(); i++)
                {
                    Movie movie = new Movie();

                    JSONObject jsonObj = jsonArray.getJSONObject(i);

                    String poster = jsonObj.getString("poster_path");
                    String originalTitle = jsonObj.getString("original_title");
                    String overview = jsonObj.getString("overview");
                    String release_date = jsonObj.getString("release_date");
                    int vote_average = jsonObj.getInt("vote_average");
                    int id = jsonObj.getInt("id");
                    String title = jsonObj.getString("title");

                    // setting movie
                    movie.setPoster_path(poster);
                    movie.setOriginal_title(originalTitle);
                    movie.setOverview(overview);
                    movie.setRelease_date(release_date);
                    movie.setVote_average(vote_average);
                    movie.setTitle(title);
                    movie.setId(id);

                    moviesArrayList.add(movie);
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            finally
            {
                moviesAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater()
                .inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //switch (item.getItemId())
        if (!applyFilter(item.getItemId()))
            return super.onOptionsItemSelected(item);
        else
            return true;
    }

    public boolean applyFilter(int filter)
    {
        movieFilterSelected = filter;

        switch (filter)
        {
            case R.id.menu_popular:

                setTitle(R.string.pop);
                MoviesTask moviesTask = new MoviesTask();
                moviesTask.execute(Configuration.POPULAR_MOVIES_URL);
                return true;

            case R.id.menu_rated:

                setTitle(R.string.top);
                MoviesTask moviesTask1 = new MoviesTask();
                moviesTask1.execute(Configuration.TOP_RATED_MOVIES_URL);
                return true;

            case R.id.menu_favorite:

                setTitle(R.string.menu_fav);

                FavoriteMovies favoriteMovies = new FavoriteMovies(this);
                sqLiteDatabase = favoriteMovies.getReadableDatabase();
                Cursor cursor = getAllData();

                    /*Setting values */
                moviesArrayList.clear();
                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(cursor.getColumnIndex(FavoriteMoviesContract.ID_COLUMN_NAME));
                    String title = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.TITLE_COLUMN_NAME));
                    int vote_average = cursor.getInt(cursor.getColumnIndex(FavoriteMoviesContract.VOTE_COLUMN_NAME));
                    String originalTitle = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.ORIGINAL_TITLE_COLUMN_NAME));;
                    String overview = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.OVERVIEW_COLUMN_NAME));
                    String release_date = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.RELEASEDATE_COLUMN_NAME));
                    String poster = cursor.getString(cursor.getColumnIndex(FavoriteMoviesContract.POSTER_COLUMN_NAME));

                    Movie movie = new Movie();
                    // setting movie
                    movie.setPoster_path(poster);
                    movie.setOriginal_title(originalTitle);
                    movie.setOverview(overview);
                    movie.setRelease_date(release_date);
                    movie.setVote_average(vote_average);
                    movie.setTitle(title);
                    movie.setId(id);

                    moviesArrayList.add(movie);
                }
                moviesAdapter.notifyDataSetChanged();
                return true;
                    /*end Setting value*/
            default:
                return false;
        }
    }

    public Cursor getAllData()
    {
       return getContentResolver().query(FavoriteMoviesContract.CONTENT_URI_FINAL,
               null,
               null,
               null,
               null);
    }
}