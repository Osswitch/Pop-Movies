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
 * Created by zhang on 08/12/15.
 */
public class FetchTrailerAndReviewTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = FetchTrailerAndReviewTask.class.getSimpleName();

    private final Context mContext;

    public FetchTrailerAndReviewTask(Context context) {
        mContext = context;
    }

    private void getTrailerFromJson(int movieId, String resultJSONStr) throws JSONException {

        final String TDB_RESULTS = "results";
        final String TDB_ID = "id";
        final String TDB_NAME = "name";
        final String TDB_KEY = "key";

        try {
            JSONObject resultJSON = new JSONObject(resultJSONStr);
            JSONArray resultArray = resultJSON.getJSONArray(TDB_RESULTS);

            int trailerNum = resultArray.length();

            Vector<ContentValues> trailerValuesVector = new Vector<ContentValues>(trailerNum);

            for (int i = 0; i < trailerNum; i++) {
                JSONObject trailerObject = resultArray.getJSONObject(i);

                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
                        trailerObject.getString(TDB_ID));
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
                        trailerObject.getString(TDB_NAME));
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_PATH,
                        trailerObject.getString(TDB_KEY));

                trailerValuesVector.add(trailerValues);
            }

            if (trailerValuesVector.size() > 0) {
                ContentValues[] trailerValuesArray = new ContentValues[trailerValuesVector.size()];
                trailerValuesVector.toArray(trailerValuesArray);

                mContext.getContentResolver().bulkInsert(
                        MovieContract.TrailerEntry.buildTrailerWithMovieId(movieId),
                        trailerValuesArray
                );
            }

            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.TrailerEntry.buildTrailerWithMovieId(movieId),
                    null,
                    null,
                    null,
                    null
            );

            Log.v(LOG_TAG, "Fetch trailer task completed " + cursor.getCount() + " inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void getReviewFromJson(int movieId, String resultJSONStr) throws JSONException {

        final String RDB_RESULTS = "results";
        final String RDB_ID = "id";
        final String RDB_AUTHOR = "author";
        final String RDB_CONTENT = "content";
        final String RDB_URL = "url";

        try {
            JSONObject resultJSON = new JSONObject(resultJSONStr);
            JSONArray resultArray = resultJSON.getJSONArray(RDB_RESULTS);

            int reviewNum = resultArray.length();

            Vector<ContentValues> reviewValuesVector = new Vector<ContentValues>(reviewNum);

            for (int i = 0; i < reviewNum; i++) {
                JSONObject reviewObject = resultArray.getJSONObject(i);

                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
                        reviewObject.getString(RDB_ID));
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
                        reviewObject.getString(RDB_AUTHOR));
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
                        reviewObject.getString(RDB_CONTENT));
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL,
                        reviewObject.getString(RDB_URL));


                reviewValuesVector.add(reviewValues);
            }

            if (reviewValuesVector.size() > 0) {
                ContentValues[] reviewValuesArray = new ContentValues[reviewValuesVector.size()];
                reviewValuesVector.toArray(reviewValuesArray);

                mContext.getContentResolver().bulkInsert(
                        MovieContract.ReviewEntry.buildReviewWithMovieId(movieId),
                        reviewValuesArray
                );
            }

            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.ReviewEntry.buildReviewWithMovieId(movieId),
                    null,
                    null,
                    null,
                    null
            );

            Log.v(LOG_TAG, "Fetch review task completed " + cursor.getCount() + " inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String trailerJSONStr = null;
        String reviewJSONStr = null;
        String API_KEY = mContext.getString(R.string.api_key);

        try {
            final String FETCH_BASE_URI = "http://api.themoviedb.org/3/";
            String path = "movie";
            String movie_id = params[0];
            String trailerCategory = "videos";
            String reviewCategory = "reviews";
            final String API_KEY_PARAM = "api_key";

            Uri trailerUri = Uri.parse(FETCH_BASE_URI).buildUpon()
                    .appendPath(path)
                    .appendPath(movie_id)
                    .appendPath(trailerCategory)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            Uri reviewUri = Uri.parse(FETCH_BASE_URI).buildUpon()
                    .appendPath(path)
                    .appendPath(movie_id)
                    .appendPath(reviewCategory)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL trailerUrl = new URL(trailerUri.toString());
            URL reviewUrl = new URL(reviewUri.toString());

            //Log.v(LOG_TAG, "URL is " + url);

            urlConnection = (HttpURLConnection) trailerUrl.openConnection();
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

            trailerJSONStr = buffer.toString();
            getTrailerFromJson(Integer.parseInt(movie_id), trailerJSONStr);

            urlConnection = (HttpURLConnection) reviewUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            buffer = new StringBuffer();

            if (inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            line =null;

            while ((line = reader.readLine()) != null){
                buffer.append(line + "/");
            }

            if (buffer.length() == 0){
                return null;
            }

            reviewJSONStr = buffer.toString();
            getReviewFromJson(Integer.parseInt(movie_id), reviewJSONStr);

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
