package com.example.zhang.popmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by zhang on 24/11/15.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private final Context mContext;

    public FetchMoviesTask(Context context) {
        mContext = context;
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

                if (sortMethod.equals(mContext.getString(R.string.sort_entryValue_popularity))) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_IS_POPULARITY, 1);
                } else if (sortMethod.equals(mContext.getString(R.string.sort_entryValue_highestRate))) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_IS_HIGHEST_RATE, 1);
                }

                contentValuesVector.add(movieValues);
            }

            Cursor cursor = mContext.getContentResolver().query(
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
                mContext.getContentResolver().bulkInsert(
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

                if (sortMethod.equals(mContext.getString(R.string.sort_entryValue_popularity))) {
                    updateValues.put(MovieContract.MovieEntry.COLUMN_IS_POPULARITY, 0);

                    mContext.getContentResolver().update(
                            MovieContract.MovieEntry.CONTENT_URI,
                            updateValues,
                            sSelection,
                            sSelectionArgs
                    );
                } else if (sortMethod.equals(mContext.getString(R.string.sort_entryValue_highestRate))) {
                    updateValues.put(MovieContract.MovieEntry.COLUMN_IS_HIGHEST_RATE, 0);

                    mContext.getContentResolver().update(
                            MovieContract.MovieEntry.CONTENT_URI,
                            updateValues,
                            sSelection,
                            sSelectionArgs
                    );
                }

            }

            cursor = mContext.getContentResolver().query(
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

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJSONStr = null;

        String method = "discover";

        String category = "movie";

        String minCountNum = "500";

        String sortMethod = params[0];

        String API_KEY = mContext.getString(R.string.api_key);

        //Try block fetch JSON data from the cloud
        try{

            final String FETCH_BASE_URI = "http://api.themoviedb.org/3/";
            final String SORT_PARAM = "sort_by";
            final String COUNT_PARAM = "vote_count.gte";
            final String API_KEY_PARAM = "api_key";
            Uri uri = null;

            if (sortMethod.equals(mContext.getString(R.string.sort_entryValue_popularity))) {
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
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line =null;

            while ((line = reader.readLine()) != null){
                buffer.append(line + "/");
            }

            if (buffer.length() == 0){
                return null;
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
        return null;
    }

}
