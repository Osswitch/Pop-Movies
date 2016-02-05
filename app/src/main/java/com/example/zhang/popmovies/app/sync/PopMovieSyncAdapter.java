package com.example.zhang.popmovies.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.zhang.popmovies.app.R;
import com.example.zhang.popmovies.app.Utility;
import com.example.zhang.popmovies.app.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by zhang on 04/02/16.
 */
public class PopMovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = PopMovieSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;


    public PopMovieSyncAdapter(Context context, Boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.v(LOG_TAG, "starting sync");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJSONStr = null;

        String method = "discover";

        String category = "movie";

        String minCountNum = "500";

        String sortMethod = Utility.getPreferredSortMethod(getContext());

        String API_KEY = getContext().getString(R.string.api_key);

        //Try block fetch JSON data from the cloud
        try{

            final String FETCH_BASE_URI = "http://api.themoviedb.org/3/";
            final String SORT_PARAM = "sort_by";
            final String COUNT_PARAM = "vote_count.gte";
            final String API_KEY_PARAM = "api_key";
            Uri uri = null;

            if (sortMethod.equals(getContext().getString(R.string.sort_entryValue_popularity))) {
                uri = Uri.parse(FETCH_BASE_URI).buildUpon()
                        .appendPath(method)
                        .appendPath(category)
                        .appendQueryParameter(SORT_PARAM, sortMethod)
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();
            } else {

                // When fetch the highest rated movies, restrict those which
                // have been voted over 500 times
                uri = Uri.parse(FETCH_BASE_URI).buildUpon()
                        .appendPath(method)
                        .appendPath(category)
                        .appendQueryParameter(SORT_PARAM, sortMethod)
                        .appendQueryParameter(COUNT_PARAM, minCountNum)
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();
            }



            URL url = new URL(uri.toString());

            //Log.v(LOG_TAG, "URL is " + url);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null){
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line =null;

            while ((line = reader.readLine()) != null){
                buffer.append(line + "/");
            }

            if (buffer.length() == 0){
                return;
            }

            movieJSONStr = buffer.toString();
            getMovieFromJson(movieJSONStr, sortMethod);

        } catch(IOException e) {
            Log.e(LOG_TAG, "error", e);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    //Get poster uri from json
    private void getMovieFromJson(String movieJSONStr, String sortMethod)
            throws JSONException {

        final String MDB_RESULTS = "results";
        final String MDB_ID = "id";
        final String MDB_ORIGINAL_TITLE = "original_title";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_POPULARITY = "popularity";
        final String MDB_VOTE_AVERAGE = "vote_average";
        final String MDB_VOTE_COUNT = "vote_count";

        try {
            JSONObject resultJSON = new JSONObject(movieJSONStr);
            JSONArray resultArray = resultJSON.getJSONArray(MDB_RESULTS);
            int perPageMovieCounts = resultArray.length();
            Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(perPageMovieCounts);

            // Restore every movie id from json in use of update movies.
            String[] sSelectionArgs = new String[perPageMovieCounts];

            for (int i = 0; i < perPageMovieCounts; i++) {
                JSONObject movieObject = resultArray.getJSONObject(i);

                sSelectionArgs[i] = Integer.toString(movieObject.getInt(MDB_ID));

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                        movieObject.getInt(MDB_ID));
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
                        movieObject.getString(MDB_ORIGINAL_TITLE));
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,
                        movieObject.getString(MDB_OVERVIEW));
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                        movieObject.getString(MDB_RELEASE_DATE));
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                        movieObject.getString(MDB_POSTER_PATH));
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,
                        movieObject.getDouble(MDB_POPULARITY));
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                        movieObject.getDouble(MDB_VOTE_AVERAGE));
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
                        movieObject.getInt(MDB_VOTE_COUNT));

                if (sortMethod.equals(getContext().getString(R.string.sort_entryValue_popularity))) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_IS_POPULARITY, 1);
                } else if (sortMethod.equals(getContext().getString(R.string.sort_entryValue_highestRate))) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_IS_HIGHEST_RATE, 1);
                }

                contentValuesVector.add(movieValues);
            }

            Cursor cursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            int storedMovieNum = cursor.getCount();
            cursor.close();

            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValues = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValues);
                getContext().getContentResolver().bulkInsert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        contentValues);

                String sSelection = "("
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "!=? AND "
                        + MovieContract.MovieEntry.COLUMN_MOVIE_ID
                        + "!=?)";

                ContentValues updateValues = new ContentValues();

                if (sortMethod.equals(getContext().getString(R.string.sort_entryValue_popularity))) {
                    updateValues.put(MovieContract.MovieEntry.COLUMN_IS_POPULARITY, 0);

                    getContext().getContentResolver().update(
                            MovieContract.MovieEntry.CONTENT_URI,
                            updateValues,
                            sSelection,
                            sSelectionArgs
                    );
                } else if (sortMethod.equals(getContext().getString(R.string.sort_entryValue_highestRate))) {
                    updateValues.put(MovieContract.MovieEntry.COLUMN_IS_HIGHEST_RATE, 0);

                    getContext().getContentResolver().update(
                            MovieContract.MovieEntry.CONTENT_URI,
                            updateValues,
                            sSelection,
                            sSelectionArgs
                    );
                }

            }

            cursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            Log.d(LOG_TAG, "Fetch movie task complete. "
                    + (cursor.getCount() - storedMovieNum) + " New Movies Inserted ");

            cursor.close();

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */

        PopMovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter (Context context) {
        getSyncAccount(context);
    }
}
