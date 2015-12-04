package com.example.zhang.popmovies.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Vector;

/**
 * Created by zhang on 16/11/15.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: this means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without both the movie table, the trailer" +
                " table and review table", tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);
        assertTrue("Error: this means we could not query the movie entry table", c.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_IS_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_IS_HIGHEST_RATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_IS_FAVOURITE);

        int movieColumnIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(movieColumnIndex);
            movieColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all the designed movie entry columns ",
                movieColumnHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + ")", null);
        assertTrue("Error: this means we could not query the trailer table", c.moveToFirst());

        final HashSet<String> trailerColumnHashSet = new HashSet<String>();
        trailerColumnHashSet.add(MovieContract.TrailerEntry._ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_MOVIE_ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_PATH);

        int trailerColumnIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(trailerColumnIndex);
            trailerColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all the designed movie entry columns ",
                trailerColumnHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.ReviewEntry.TABLE_NAME + ")", null);
        assertTrue("Error: this means we could not query the review table", c.moveToFirst());

        final HashSet<String> reviewColumnHashSet = new HashSet<String>();
        reviewColumnHashSet.add(MovieContract.ReviewEntry._ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_MOVIE_ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_URL);

        int reviewColumnIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(reviewColumnIndex);
            reviewColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all the designed movie entry columns ",
                reviewColumnHashSet.isEmpty());

        db.close();
    }

    public void testMovieTable() {

        long movieRowId = insertMovie();
    }

    public void testTrailerTable() {
        MovieDbHelper db = new MovieDbHelper(mContext);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        long movieId = insertMovie();
        assertTrue("Error: Movie not insert correctly", movieId == 102899);

        Vector<ContentValues> testTrailerValues = TestUtilities.createAntManTrailerValues(movieId);
        //ContentValues testTrailerValues = TestUtilities.createAntManTrailerValues(movieId);

        for (ContentValues testValue : testTrailerValues) {
            sqLiteDatabase.insert(MovieContract.TrailerEntry.TABLE_NAME,
                    null,
                    testValue);
        }

        Cursor cursor = sqLiteDatabase.query(MovieContract.TrailerEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Error: no records found from trailer query", cursor.moveToFirst());

        ContentValues[] testValues = new ContentValues[testTrailerValues.size()];
        testTrailerValues.toArray(testValues);

        int testValuesIndex = 0;
        do {
            TestUtilities.validateCurrentRecord("Failed to validate trailer insert" , cursor, testValues[testValuesIndex]);
            testValuesIndex++;
        } while (cursor.moveToNext());

        cursor.close();
        sqLiteDatabase.close();
    }

    public void testReviewTable() {
        MovieDbHelper db = new MovieDbHelper(mContext);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        long movieId = insertMovie();
        assertTrue("Error: Movie not insert correctly", movieId == 102899);

        Vector<ContentValues> testReviewValues = TestUtilities.createAntManReviewValues(movieId);
        //ContentValues testTrailerValues = TestUtilities.createAntManTrailerValues(movieId);

        for (ContentValues testValue : testReviewValues) {
            sqLiteDatabase.insert(MovieContract.ReviewEntry.TABLE_NAME,
                    null,
                    testValue);
        }

        Cursor cursor = sqLiteDatabase.query(MovieContract.ReviewEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Error: no records found from review query", cursor.moveToFirst());

        ContentValues[] testValues = new ContentValues[testReviewValues.size()];
        testReviewValues.toArray(testValues);

        int testValuesIndex = 0;
        do {
            TestUtilities.validateCurrentRecord("Failed to validate review insert" , cursor, testValues[testValuesIndex]);
            testValuesIndex++;
        } while (cursor.moveToNext());

        cursor.close();
        sqLiteDatabase.close();
    }

    public long insertMovie() {
        MovieDbHelper db = new MovieDbHelper(mContext);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

        ContentValues testValues = TestUtilities.createAntManValues();
        long movieRowId = sqLiteDatabase.insert(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                testValues);

        assertTrue(movieRowId != -1);

        Cursor cursor = sqLiteDatabase.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("No movie returned from movie query", cursor.moveToFirst());
        long movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

        TestUtilities.validateCurrentRecord("Error: movie query validation failed",
                cursor, testValues);

        assertFalse("More than one result returned from movie query", cursor.moveToNext());
        cursor.close();
        sqLiteDatabase.close();

        return movieId;
    }
}
