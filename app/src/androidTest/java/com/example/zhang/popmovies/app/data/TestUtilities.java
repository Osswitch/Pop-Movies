package com.example.zhang.popmovies.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

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
        testValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 57.576639);
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.0);
        testValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, 1581);

        return testValues;
    }

    static long insertAntManValues(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = createAntManValues();

        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
        assertTrue("Error: Failure to insert Ant-Man values", movieRowId != -1);

        return movieRowId;
    }
}