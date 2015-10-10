package com.example.ebram.popmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ebram.popmovies.Data.MovieContract.PopMovieEntry;
import com.example.ebram.popmovies.Data.MovieContract.RatedMovieEntry;
import com.example.ebram.popmovies.Data.MovieContract.FavoriteMovieEntry;
import com.example.ebram.popmovies.Data.MovieContract.ReviewsTrailersMovieEntry;

/**
 * Created by Ebram Samy on 9/15/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_POP_MOVIE_TABLE = "CREATE TABLE " + PopMovieEntry.TABLE_NAME + " (" +

                PopMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopMovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE," +
                PopMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                PopMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                PopMovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT, " +
                PopMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                PopMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL);";

        final String SQL_CREATE_RATED_MOVIE_TABLE = "CREATE TABLE " + RatedMovieEntry.TABLE_NAME + " (" +

                RatedMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RatedMovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE," +
                RatedMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                RatedMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                RatedMovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT, " +
                RatedMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                RatedMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL);";

        final String SQL_CREATE_FAVORITE_MOVIE_TABLE = "CREATE TABLE " + FavoriteMovieEntry.TABLE_NAME + " (" +

                FavoriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE," +
                FavoriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT, " +
                FavoriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteMovieEntry.COLUMN_MOVIE_VOTE_AVERAGE + " TEXT NOT NULL);";

        final String SQL_CREATE_REVIEWS_TRAILERS_MOVIE_TABLE = "CREATE TABLE " + ReviewsTrailersMovieEntry.TABLE_NAME + " (" +
                ReviewsTrailersMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_ID + " TEXT ," +
                ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_MOVIE_ID + " INTEGER," +
                ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_NAME + " TEXT NOT NULL, " +
                ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_CONTENT + " TEXT NOT NULL, " +
                ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_TYPE + " INTEGER NOT NULL,UNIQUE (" + ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_ID + "," + ReviewsTrailersMovieEntry.COLUMN_REVIEW_TRAILER_MOVIE_ID + ") ON CONFLICT REPLACE);";


        db.execSQL(SQL_CREATE_POP_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_RATED_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TRAILERS_MOVIE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PopMovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RatedMovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewsTrailersMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
