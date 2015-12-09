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
public class FetchTrailerTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    private final Context mContext;

    public FetchTrailerTask(Context context) {
        mContext = context;
    }

    private void getTrailerFromJson(int movieId, String resultJSONStr) throws JSONException{

        final String TDB_RESULT = "results";
        final String TDB_ID = "id";
        final String TDB_NAME = "name";
        final String TDB_KEY = "key";

        try {
            JSONObject resultJSON = new JSONObject(resultJSONStr);
            JSONArray resultArray = resultJSON.getJSONArray(TDB_RESULT);

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
                ContentValues[] trailerValues = new ContentValues[trailerValuesVector.size()];
                trailerValuesVector.toArray(trailerValues);

                mContext.getContentResolver().bulkInsert(
                        MovieContract.TrailerEntry.buildTrailerWithMovieId(movieId),
                        trailerValues
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

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String resultJSONStr = null;
        String API_KEY = mContext.getString(R.string.api_key);

        try {
            final String FETCH_BASE_URI = "http://api.themoviedb.org/3/";
            String path = "movie";
            String movie_id = params[0];
            String category = "videos";
            final String API_KEY_PARAM = "api_key";

            Uri uri = Uri.parse(FETCH_BASE_URI).buildUpon()
                    .appendPath(path)
                    .appendPath(movie_id)
                    .appendPath(category)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

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
            getTrailerFromJson(Integer.parseInt(movie_id), resultJSONStr);

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
