package com.example.zhang.popmovies.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhang on 16/11/15.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME
                + " (" + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_IS_POPULARITY + " INTEGER DEFAULT 0, "
                + MovieContract.MovieEntry.COLUMN_IS_HIGHEST_RATE + " INTEGER DEFAULT 0, "
                + MovieContract.MovieEntry.COLUMN_IS_FAVOURITE + " INTEGER DEFAULT 0, "
                + "UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE "
                + MovieContract.TrailerEntry.TABLE_NAME
                + " (" + MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_TRAILER_PATH + " TEXT NOT NULL, "
                + "UNIQUE (" + MovieContract.TrailerEntry.COLUMN_TRAILER_ID
                + ") ON CONFLICT REPLACE, "
                + "FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                + MovieContract.MovieEntry.TABLE_NAME + " ("
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "));";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE "
                + MovieContract.ReviewEntry.TABLE_NAME
                + " (" + MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL, "
                + "FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES "
                + MovieContract.MovieEntry.TABLE_NAME + " ("
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "), "
                + "UNIQUE (" + MovieContract.ReviewEntry.COLUMN_REVIEW_ID
                + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXIST " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXIST " + MovieContract.TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXIST " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(db);

    }
}
