package com.example.zhang.popmovies.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.zhang.popmovies.app.utils.PollingCheck;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Created by zhang on 20/11/15.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor cursor, ContentValues contentValues) {
        assertTrue("Empty cursor returned. " + error, cursor.moveToFirst());

    }

    static void validateCurrentRecord(String error, Cursor cursor, ContentValues contentValues) {
        Set<Map.Entry<String, Object>> valueSet = contentValues.valueSet();
        for(Map.Entry<String, Object> entry: valueSet) {
            String columnName = entry.getKey();
            int idx = cursor.getColumnIndex(columnName);
            assertFalse("Column " + columnName + " not found." + error, idx == -1);
            String value = entry.getValue().toString();
            assertEquals("Value " + cursor.getString(idx) + " did not match the expected value " + value,
                    value, cursor.getString(idx));
        }
    }

    static ContentValues createAntManValues() {
        ContentValues testValues = new ContentValues();

        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 102899);
        testValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Ant-Man");
        testValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Armed with the astonishing ability to shrink in scale but increase in strength, con-man Scott Lang must embrace his inner-hero and help his mentor, Dr. Hank Pym, protect the secret behind his spectacular Ant-Man suit from a new generation of towering threats. Against seemingly insurmountable obstacles, Pym and Lang must plan and pull off a heist that will save the world.");
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-07-17");
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/7SGGUiTE6oc2fh9MjIk5M00dsQd.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 70.0038);
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.1);
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 1581);

        return testValues;
    }

    static Vector<ContentValues> createAntManTrailerValues(long movieId) {
        Vector<ContentValues> trailerValues = new Vector<ContentValues>();

        ContentValues trailerValue1 = new ContentValues();
        ContentValues trailerValue2 = new ContentValues();

        trailerValue1.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
        trailerValue1.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, "12345");
        trailerValue1.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, "First look");
        trailerValue1.put(MovieContract.TrailerEntry.COLUMN_TRAILER_PATH, "xInh3VhAWs8");

        trailerValue2.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
        trailerValue2.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, "23456");
        trailerValue2.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, "Official Trailer");
        trailerValue2.put(MovieContract.TrailerEntry.COLUMN_TRAILER_PATH, "pWdKf3MneyI");

        trailerValues.add(trailerValue1);
        trailerValues.add(trailerValue2);

        return trailerValues;
    }

    static Vector<ContentValues> createAntManReviewValues(long movieId) {
        Vector<ContentValues> reviewValues = new Vector<ContentValues>();

        ContentValues reviewValue1 = new ContentValues();
        ContentValues reviewValue2 = new ContentValues();

        reviewValue1.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
        reviewValue1.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, "55d5a00792514102cf0041f2");
        reviewValue1.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "owen333");
        reviewValue1.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "I loved it it was so " +
                "much fun and very funny, another marvel masterpiece");
        reviewValue1.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, "http://j.mp/1MzokBf");

        reviewValue2.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
        reviewValue2.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, "55dabef092514143f3006a24");
        reviewValue2.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "Amirbabaii");
        reviewValue2.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "چجوری بایددانلودکنم فیلم رو");
        reviewValue2.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, "http://j.mp/1hV8UMD");

        reviewValues.add(reviewValue1);
        reviewValues.add(reviewValue2);

        return reviewValues;
    }


    static long insertAntManValues(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = createAntManValues();

        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
        assertTrue("Error: Failure to insert Ant-Man values", movieRowId != -1);

        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Error: no result returned", cursor.moveToFirst());

        long movieId = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));

        cursor.close();
        db.close();

        return movieId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        private TestContentObserver (HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
