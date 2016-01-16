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

    private static final int VIEW_UNCLICKED = 0;
    private static final int VIEW_CLICKED = 1;

    // Indicate which rows of the review listview have been clicked.
    private static Integer[] clickedArrays;

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
    public int getItemViewType(int position) {
        return (clickedArrays[position] == 0) ? VIEW_UNCLICKED : VIEW_CLICKED;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);

        int viewType = getItemViewType(cursor.getPosition());

        ViewHolder viewHolder = new ViewHolder(view);
        if (viewType == VIEW_UNCLICKED) {
            viewHolder.contentTextView.setLines(1);
        } else if (viewType == VIEW_CLICKED) {
            viewHolder.contentTextView.setMaxLines(Integer.MAX_VALUE);
        }
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.authorTextView.setText(cursor.getString(MovieDetailActivityFragment.COLUMN_REVIEW_AUTHOR));
        viewHolder.contentTextView.setText(cursor.getString(MovieDetailActivityFragment.COLUMN_REVIEW_CONTENT));

    }

    // get the clicked rows position
    public void insertClickedFlag(Integer[] clickedFlag) {
        clickedArrays = clickedFlag;
    }
}
