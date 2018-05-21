package com.propelld.app.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by manjari on 16/5/18.
 */

public class FavoriteMovies extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "favoriteMovies_New.db";

    private static final int DATABASE_VERSION = 1;

    public FavoriteMovies(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        final String create_table = "CREATE TABLE "
                + FavoriteMoviesContract.TABLE_NAME
                + " ("+ FavoriteMoviesContract._ID + " AUTO_INCREMENT,"
                + FavoriteMoviesContract.ID_COLUMN_NAME + " MOVIE_ID,"
                + FavoriteMoviesContract.VOTE_COLUMN_NAME + " INT,"
                + FavoriteMoviesContract.ORIGINAL_TITLE_COLUMN_NAME + " TEXT,"
                + FavoriteMoviesContract.OVERVIEW_COLUMN_NAME + " TEXT,"
                + FavoriteMoviesContract.POSTER_COLUMN_NAME + " TEXT,"
                + FavoriteMoviesContract.RELEASEDATE_COLUMN_NAME+ " TEXT,"
                + FavoriteMoviesContract.TITLE_COLUMN_NAME + " TEXT NOT NULL"

                + ");";

        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesContract.TABLE_NAME);

        onCreate(db);
    }
}