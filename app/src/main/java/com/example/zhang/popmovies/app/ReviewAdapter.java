package com.example.zhang.popmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by zhang on 15/12/15.
 */
public class ReviewAdapter extends CursorAdapter {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    private static class ViewHolder {

        public final TextView authorTextView;
        public final TextView contentTextView;

        public ViewHolder (View view) {
            authorTextView = (TextView) view.findViewById(R.id.list_item_review_author_textview);
            contentTextView = (TextView) view.findViewById(R.id.list_item_review_content_textview);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.authorTextView.setText(cursor.getString(MovieDetailActivityFragment.COLUMN_REVIEW_AUTHOR));
        viewHolder.contentTextView.setText(cursor.getString(MovieDetailActivityFragment.COLUMN_REVIEW_CONTENT));

    }
}
