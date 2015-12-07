package com.example.zhang.popmovies.app;

import android.content.Context;
import android.content.SharedPreferences;
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
}
