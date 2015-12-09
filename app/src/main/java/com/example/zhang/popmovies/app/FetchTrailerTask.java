package com.example.zhang.popmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.example.zhang.popmovies.app.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhang on 08/12/15.
 */
public class FetchTrailerTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    private final Context mContext;

    public FetchTrailerTask(Context context) {
        mContext = context;
    }

    private void getTrailerFromJson(int movieId, String retJSONStr) throws JSONException{

        final String TDB_RESULT = "result";
        final String TDB_ID = "id";
        final String TDB_NAME = "name";
        final String TDB_KEY = "key";

        try {
            JSONObject resultJSON = new JSONObject(retJSONStr);
            JSONArray resultArray = resultJSON.getJSONArray(TDB_RESULT);

            int trailerNum = resultArray.length();

            for (int i = 0; i < trailerNum; i++) {
                JSONObject trailerObject = resultArray.getJSONObject(i);

                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
                        movieId);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
                        trailerObject.getString(TDB_ID));
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
                        trailerObject.getString(TDB_NAME));
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_PATH,
                        trailerObject.getString(TDB_KEY));
            }
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        return null;
    }
}
