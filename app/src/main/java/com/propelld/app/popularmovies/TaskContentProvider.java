package com.propelld.app.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by manjari on 20/5/18.
 */

public class TaskContentProvider extends ContentProvider
{
    private FavoriteMovies favoriteMovies;

    private static final int Tasks = 100;

    private static final int Tasks_With_ID = 101;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher()
    {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoriteMoviesContract.Authority, FavoriteMoviesContract.TABLE_NAME, Tasks);

        uriMatcher.addURI(FavoriteMoviesContract.Authority, FavoriteMoviesContract.TABLE_NAME + "/#", Tasks_With_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate()
    {
        Context context = getContext();

        favoriteMovies = new FavoriteMovies(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        final SQLiteDatabase sqLiteDatabase = favoriteMovies.getReadableDatabase();

        int match = uriMatcher.match(uri);

        Cursor cursor;
        switch (match)
        {
            case Tasks :
                cursor = sqLiteDatabase.query(FavoriteMoviesContract.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                    break;

            default: throw new UnsupportedOperationException(""+uri);
         }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        final SQLiteDatabase sqLiteDatabase = favoriteMovies.getWritableDatabase();

        int match = uriMatcher.match(uri);

        Uri returnUri;
        switch (match)
        {
            case  Tasks :
                long id =  sqLiteDatabase.insert(FavoriteMoviesContract.TABLE_NAME, null, values);

                if (id > 0)
                {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesContract.CONTENT_URI_FINAL, id);
                }
                else
                {
                    throw new android.database.SQLException("failed"+uri);
                }
                break;

            default: throw new UnsupportedOperationException("unknown uri" +uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }
}