package com.example.zhang.popmovies.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.zhang.popmovies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    private static final int DETAIL_MOVIE_LOADER = 0;
    private static final int DETAIL_TRAILER_LOADER = 1;
    private static final int DETAIL_REVIEW_LOADER = 2;

    private static final String detailPosterSize = "w500";

    private Long movie_id;
    private Uri movieSelectedUri;
    private Uri trailerSelectedUri;
    private Uri reviewSelectedUri;

    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;

    private ListView trailerListView;
    private ListView reviewListView;

    private TextView titleTextView;
    private TextView releaseDateTextView;
    private ImageView posterImageView;

    private TextView voteAverageTextView;
    private TextView plotSynopsisTextView;

    public static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailerEntry.COLUMN_TRAILER_PATH
    };

    public static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT
    };

    static final int COLUMN_TRAILER_ID = 0;
    static final int COLUMN_TRAILER_NAME = 1;
    static final int COLUMN_TRAILER_PATH = 2;

    static final int COLUMN_REVIEW_ID = 0;
    static final int COLUMN_REVIEW_AUTHOR = 1;
    static final int COLUMN_REVIEW_CONTENT = 2;


    public MovieDetailActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_MOVIE_LOADER, null, this);
        getLoaderManager().initLoader(DETAIL_TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(DETAIL_REVIEW_LOADER, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.fragment_movie_detail, container, false);
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        titleTextView = (TextView) rootView.findViewById(R.id.detail_title_textView);
        releaseDateTextView
                = (TextView) rootView.findViewById(R.id.detail_release_date_textView);
        posterImageView
                = (ImageView) rootView.findViewById(R.id.detail_poster_imageView);
        voteAverageTextView
                = (TextView) rootView.findViewById(R.id.detail_vote_average_textView);
        plotSynopsisTextView
                = (TextView) rootView.findViewById(R.id.detail_plot_synopsis_textView);

        Intent intent = getActivity().getIntent();

        if (intent != null && intent.getData() != null) {
            movieSelectedUri = intent.getData();
            movie_id = MovieContract.MovieEntry.getMovieIdFromUri(movieSelectedUri);
            trailerSelectedUri = MovieContract.TrailerEntry.buildTrailerWithMovieId(movie_id);
            reviewSelectedUri = MovieContract.ReviewEntry.buildReviewWithMovieId(movie_id);
        }


        mTrailerAdapter = new TrailerAdapter(
                getActivity(),
                null,
                0
        );

        mReviewAdapter = new ReviewAdapter(
                getActivity(),
                null,
                0
        );

        trailerListView = (ListView) rootView.findViewById(R.id.listView_trailer);
        trailerListView.setAdapter(mTrailerAdapter);
        trailerListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                        if (cursor != null) {
                            Intent trailerIntent = new Intent(Intent.ACTION_VIEW);
                            Uri path = Uri.parse("https://www.youtube.com/watch").buildUpon()
                                    .appendQueryParameter("v", cursor.getString(COLUMN_TRAILER_PATH))
                                    .build();

                            trailerIntent.setData(path);
                            startActivity(trailerIntent);

                        }
                    }
                }
        );

        reviewListView = (ListView) rootView.findViewById(R.id.listView_review);
        reviewListView.setAdapter(mReviewAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == DETAIL_MOVIE_LOADER) {
            if (movieSelectedUri == null) {
                return null;
            }
            return new CursorLoader(
                    getActivity(),
                    movieSelectedUri,
                    PreviewFragment.MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        } else if (id == DETAIL_TRAILER_LOADER) {
            if (trailerSelectedUri == null) {
                return null;
            }
            return new CursorLoader(
                    getActivity(),
                    trailerSelectedUri,
                    TRAILER_COLUMNS,
                    null,
                    null,
                    null
            );
        } else if (id == DETAIL_REVIEW_LOADER) {
            if (reviewSelectedUri == null) {
                return null;
            }
            return new CursorLoader(
                    getActivity(),
                    reviewSelectedUri,
                    REVIEW_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (loader.getId() == DETAIL_MOVIE_LOADER) {
            if (!cursor.moveToFirst()){
                return;
            }

            titleTextView.setText(cursor.getString(PreviewFragment.COL_MOVIE_ORIGINAL_TITLE));
            releaseDateTextView.setText(cursor.getString(PreviewFragment.COL_MOVIE_RELEASE_DATE));
            Picasso.with(getContext()).load(Utility.getPreviewImage(cursor, detailPosterSize))
                    .into(posterImageView);
            voteAverageTextView.setText(cursor.getString(PreviewFragment.COL_MOVIE_VOTE_AVERAGE));
            plotSynopsisTextView.setText(cursor.getString(PreviewFragment.COL_MOVIE_OVERVIEW));
        } else if (loader.getId() == DETAIL_TRAILER_LOADER) {
            mTrailerAdapter.swapCursor(cursor);
            setListViewHeightBasedOnChildren(trailerListView);
            Log.v(LOG_TAG, Integer.toString(cursor.getCount()));
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Log.v(LOG_TAG, cursor.getString(COLUMN_TRAILER_NAME));
                cursor.moveToNext();
            }
        } else if (loader.getId() == DETAIL_REVIEW_LOADER) {
            mReviewAdapter.swapCursor(cursor);
            setListViewHeightBasedOnChildren(reviewListView);
            Log.v(LOG_TAG, " review has " + Integer.toString(cursor.getCount()));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (loader.getId() == DETAIL_TRAILER_LOADER) {
            mTrailerAdapter.swapCursor(null);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
