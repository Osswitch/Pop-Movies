package com.example.zhang.popmovies.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * Created by zhang on 07/12/15.
 */
public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getPreferredSortMethod(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_defValue));
    }

    public static Uri getPreviewImage(Cursor cursor, String size) {

        final String IMAGE_BASE_URI = "http://image.tmdb.org/t/p";
        final String IMAGE_SIZE = size;

        String posterPath = cursor.getString(PreviewFragment.COL_MOVIE_POSTER_PATH);

        Uri previewImageUri = Uri.parse(IMAGE_BASE_URI).buildUpon()
                .appendEncodedPath(IMAGE_SIZE)
                .appendEncodedPath(posterPath)
                .build();

        return previewImageUri;
    }

    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.getType();
        } else {
            return -1;
        }
    }

}
