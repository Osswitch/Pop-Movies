package com.example.zhang.popmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by zhang on 09/12/15.
 */
public class TrailerAdapter extends CursorAdapter {

    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    public static class ViewHolder {
        public final TextView trailerNameTextView;

        public ViewHolder(View view) {
            trailerNameTextView = (TextView) view.findViewById(R.id.list_item_trailer_name_textview);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String trailerName = cursor.getString(MovieDetailActivityFragment.COLUMN_TRAILER_NAME);

        viewHolder.trailerNameTextView.setText(trailerName);

    }
}