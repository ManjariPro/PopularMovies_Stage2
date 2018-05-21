package com.propelld.app.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by manjari on 17/5/18.
 */

public class FavoriteMoviesContract implements BaseColumns
{
    public static final String TABLE_NAME = "Favorite_Movies_New";
    public static final String ID_COLUMN_NAME = "Movie_ID";
    public static final String TITLE_COLUMN_NAME = "Title";

    public static final String POSTER_COLUMN_NAME = "Poster";
    public static final String OVERVIEW_COLUMN_NAME = "Overview";
    public static final String RELEASEDATE_COLUMN_NAME = "ReleaseDate";
    public static final String VOTE_COLUMN_NAME = "Vote";
    public static final String ORIGINAL_TITLE_COLUMN_NAME = "OriginalTitle";

    public static final String Authority = "com.propelld.app.popularmovies";
    public static final String CONTENT_URI = "content://" +Authority ;

    public static final Uri Base_Content_Uri = Uri.parse(CONTENT_URI);
    public static final Uri CONTENT_URI_FINAL = Base_Content_Uri
            .buildUpon()
            .appendPath(TABLE_NAME)
            .build();
}
