package com.example.zhang.popmovies.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * Created by zhang on 07/12/15.
 */
public class Utility {

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

}
