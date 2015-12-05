package com.example.zhang.popmovies.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by zhang on 16/11/15.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_MOVIE_ID = 101;
    static final int TRAILER = 200;
    static final int TRAILER_WITH_MOVIE_ID = 201;
    static final int TRAILER_WITH_MOVIE_ID_AND_TRAILER_ID = 202;
    static final int REVIEW = 300;
    static final int REVIEW_WITH_MOVIE_ID = 301;
    static final int REVIEW_WITH_MOVIE_ID_AND_REVIEW_ID = 302;

    private static final String sMovieWithMovieIdSelection
            = MovieContract.MovieEntry.TABLE_NAME + "."
            + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";

    private static final String sTrailerWithMovieIdSelection
            = MovieContract.TrailerEntry.TABLE_NAME + "."
            + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + "=?";

    private static final String sTrailerWithMovieIdAndTrailerIdSelection
            = MovieContract.TrailerEntry.TABLE_NAME + "."
            + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + "=? AND "
            + MovieContract.TrailerEntry.COLUMN_TRAILER_ID + "=?";

    private static final String sReviewWithMovieIdSelection
            = MovieContract.ReviewEntry.TABLE_NAME + "."
            + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=?";

    private static final String sReviewWithMovieIdAndReviewIdSelection
            = MovieContract.ReviewEntry.TABLE_NAME + "."
            + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=? AND "
            + MovieContract.ReviewEntry.COLUMN_REVIEW_ID + "=?";

    private Cursor getMovieWithMovieId (Uri uri, String[] projection, String sortOrder) {

        String movieId = Long.toString(MovieContract.MovieEntry.getMovieIdFromUri(uri));

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                sMovieWithMovieIdSelection,
                new String[]{movieId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailerWithMovieId (Uri uri, String[] projection, String sortOrder) {

        String movieId = Long.toString(MovieContract.TrailerEntry.getMovieIdFromUri(uri));

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.TrailerEntry.TABLE_NAME,
                projection,
                sTrailerWithMovieIdSelection,
                new String[]{movieId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailerWithMovieIdAndTrailerId (Uri uri, String[] projection,
                                                        String sortOrder) {

        String movieId = Long.toString(MovieContract.TrailerEntry.getMovieIdFromUri(uri));
        String trailerId = MovieContract.TrailerEntry.getMovieTrailerIdFromUri(uri);

        return  mOpenHelper.getReadableDatabase().query(
                MovieContract.TrailerEntry.TABLE_NAME,
                projection,
                sTrailerWithMovieIdAndTrailerIdSelection,
                new String[]{movieId, trailerId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewWithMovieId (Uri uri, String[] projection, String sortOrder) {

        String movieId = Long.toString(MovieContract.ReviewEntry.getMovieIdFromUri(uri));

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.ReviewEntry.TABLE_NAME,
                projection,
                sReviewWithMovieIdSelection,
                new String[]{movieId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewWithMovieIdAndReviewId (Uri uri, String[] projection,
                                                      String sortOrder) {

        String movieId = Long.toString(MovieContract.ReviewEntry.getMovieIdFromUri(uri));
        String reviewId = MovieContract.ReviewEntry.getReviewIdFromUri(uri);

        return  mOpenHelper.getReadableDatabase().query(
                MovieContract.ReviewEntry.TABLE_NAME,
                projection,
                sReviewWithMovieIdAndReviewIdSelection,
                new String[]{movieId, reviewId},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        mUriMatcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        mUriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_MOVIE_ID);

        mUriMatcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        mUriMatcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_WITH_MOVIE_ID);
        mUriMatcher.addURI(authority, MovieContract.PATH_TRAILER + "/#/*", TRAILER_WITH_MOVIE_ID_AND_TRAILER_ID);

        mUriMatcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        mUriMatcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_MOVIE_ID);
        mUriMatcher.addURI(authority, MovieContract.PATH_REVIEW + "/#/*", REVIEW_WITH_MOVIE_ID_AND_REVIEW_ID);
        return mUriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_WITH_MOVIE_ID:
                retCursor = getMovieWithMovieId(uri, projection, sortOrder);
                break;
            case TRAILER:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILER_WITH_MOVIE_ID:
                retCursor = getTrailerWithMovieId(uri, projection, sortOrder);
                break;
            case TRAILER_WITH_MOVIE_ID_AND_TRAILER_ID:
                retCursor = getTrailerWithMovieIdAndTrailerId(uri, projection, sortOrder);
                break;
            case REVIEW:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEW_WITH_MOVIE_ID:
                retCursor = getReviewWithMovieId(uri, projection, sortOrder);
                break;
            case REVIEW_WITH_MOVIE_ID_AND_REVIEW_ID:
                retCursor = getReviewWithMovieIdAndReviewId(uri, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIE_ID:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIE_ID_AND_TRAILER_ID:
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_MOVIE_ID:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_WITH_MOVIE_ID_AND_REVIEW_ID:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                } else {
                    _id = db.update(
                            MovieContract.MovieEntry.TABLE_NAME,
                            values,
                            sMovieWithMovieIdSelection,
                            new String[]{values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID)}
                    );
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                }
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
            }
            default:
                throw new UnsupportedOperationException("Failed to insert row into " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final  SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) {
            selection = "1";
        }

        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case REVIEW:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE: {
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case TRAILER: {
                rowsUpdated = db.update(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            case REVIEW: {
                rowsUpdated = db.update(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri "+ uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    public int bulkInsert(Uri uri, ContentValues[] contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues values : contentValues) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                                null,
                                values);
                        if (_id != -1) {
                            returnCount++;
                        } else {
                            _id = db.update(
                                    MovieContract.MovieEntry.TABLE_NAME,
                                    values,
                                    sMovieWithMovieIdSelection,
                                    new String[]{values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID)}
                            );
                            if (_id != -1) {
                                returnCount++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TRAILER: {
                db.beginTransaction();;
                int returnCount = 0;
                try {
                    for (ContentValues values : contentValues) {
                        long _id = db.insert(
                                MovieContract.TrailerEntry.TABLE_NAME,
                                null,
                                values
                        );
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case REVIEW: {
                db.beginTransaction();;
                int returnCount = 0;
                try {
                    for (ContentValues values : contentValues) {
                        long _id = db.insert(
                                MovieContract.ReviewEntry.TABLE_NAME,
                                null,
                                values
                        );
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, contentValues);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
