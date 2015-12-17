package com.example.zhang.popmovies.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        //int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
//            if (i == 0) {
//                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
//            }
            //view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            view.measure(0, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
