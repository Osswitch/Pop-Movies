package com.example.zhang.popmovies.app.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Vector;

/**
 * Created by zhang on 17/11/15.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: records not deleted from trailer table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
            This test checks to make sure that the content provider is registered correctly.
            Students: Uncomment this test to make sure you've correctly registered the WeatherProvider.
         */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        int testMovieId = 10000;
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieWithMovieId(testMovieId));
        assertEquals("Error: the MovieEntry CONTENT_URI with movie id should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.TrailerEntry.CONTENT_URI);
        assertEquals("Error: the TrailerEntry CONTENT_URI should return TrailerEntry.CONTENT_TYPE",
                MovieContract.TrailerEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.TrailerEntry.buildTrailerWithMovieId(testMovieId));
        assertEquals("Error: the TrailerEntry CONTENT_URI with movie id should return TrailerEntry.CONTENT_TYPE",
                MovieContract.TrailerEntry.CONTENT_TYPE, type);

        String testTrailerName = "First Look";
        type = mContext.getContentResolver().getType(MovieContract.TrailerEntry.buildTrailerWithMovieIdAndTrailerName(testMovieId, testTrailerName));
        assertEquals("Error: the TrailerEntry CONTENT_URI with movie id and trailer name should return TrailerEntry.CONTENT_ITEM_TYPE",
                MovieContract.TrailerEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testBasicMovieQuery() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createAntManValues();
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
        assertTrue("Unable to insert Ant-Man into the Database", movieRowId != -1);

        db.close();

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testMovieQuery", movieCursor, testValues);
    }

    public void testBasicTrailerQuery() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        long movieId = TestUtilities.insertAntManValues(mContext);
        assertTrue("Error: Movie not insert correctly", movieId == 102899);

        Vector<ContentValues> testTrailerValues = TestUtilities.createAntManTrailerValues(movieId);

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

    public void testInsertReadProvider() {
        ContentValues testValue = TestUtilities.createAntManValues();
        TestUtilities.TestContentObserver testContentObserver
                = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI,
                true, testContentObserver);
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                testValue);

        testContentObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(testContentObserver);

        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValue);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();
        TestUtilities.TestContentObserver testContentObserver
                = TestUtilities.TestContentObserver.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI,
                true, testContentObserver);

        deleteAllRecordsFromProvider();

        testContentObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(testContentObserver);
    }

    public void testUpdateMovie() {
        ContentValues testValues = TestUtilities.createAntManValues();
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                testValues);
        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "new row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(testValues);
        updatedValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "THE ANT MAN");

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
                null
        );

        TestUtilities.TestContentObserver testContentObserver
                = TestUtilities.TestContentObserver.getTestContentObserver();
        movieCursor.registerContentObserver(testContentObserver);

        int count = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                updatedValues,
                MovieContract.MovieEntry._ID + "= ?",
                new String[]{Long.toString(movieRowId)}
        );

        assertEquals(count, 1);

        testContentObserver.waitForNotificationOrFail();
        movieCursor.unregisterContentObserver(testContentObserver);
        movieCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry._ID + "=?",
                new String[]{Long.toString(movieRowId)},
                null
        );
        TestUtilities.validateCursor("testUpdateMovie. Error validating movie entry update.",
                cursor,
                updatedValues);
        cursor.close();
    }
}
