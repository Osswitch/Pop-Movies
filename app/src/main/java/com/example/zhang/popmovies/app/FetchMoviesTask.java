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
public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private PreviewAdapter mPreviewAdapter;
    private final Context mContext;

    public FetchMoviesTask(Context context, PreviewAdapter previewAdapter) {
        mContext = context;
        mPreviewAdapter = previewAdapter;
    }

    private String[] convertContentValueToUXFormat(Vector<ContentValues> contentValuesVector) {
        String[] retPostPaths = new String[contentValuesVector.size()];
        for (int i = 0; i < contentValuesVector.size(); i++) {
            ContentValues movieValues = contentValuesVector.elementAt(i);
            retPostPaths[i] = movieValues.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        }
        return retPostPaths;
    }

    //Get poster uri from json
    private String[] getMovieFromJson(String resultJSONStr)
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
            JSONObject resultJSON = new JSONObject(resultJSONStr);
            JSONArray resultArray = resultJSON.getJSONArray(MDB_RESULTS);
            int perPageMovieCounts = resultArray.length();
            Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(perPageMovieCounts);

            for (int i = 0; i < perPageMovieCounts; i++) {
                JSONObject movieObject = resultArray.getJSONObject(i);
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
                        movieObject.getLong(MDB_POPULARITY));
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                        movieObject.getLong(MDB_VOTE_AVERAGE));
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
                        movieObject.getInt(MDB_VOTE_COUNT));
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
            }

            cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                    );


            Log.d(LOG_TAG, "Fetch movie task complete. "
                    + (cursor.getCount() - storedMovieNum) + " New Movies Inserted");
            cursor.close();

            String[] posterPaths;
            posterPaths = convertContentValueToUXFormat(contentValuesVector);

            return posterPaths;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String[]doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String resultJSONStr = null;

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

            if (sortMethod == mContext.getString(R.string.sort_entries_popularity)) {
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

            resultJSONStr = buffer.toString();

        }catch(IOException e){
            Log.e(LOG_TAG, "error", e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error", e);
                }
            }
        }

        try {
            String[] posterPaths = getMovieFromJson(resultJSONStr);
            return  posterPaths;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] posterPaths) {

        if (posterPaths != null) {
            mPreviewAdapter.clear();
            mPreviewAdapter.addAll(posterPaths);
        }
    }

}
