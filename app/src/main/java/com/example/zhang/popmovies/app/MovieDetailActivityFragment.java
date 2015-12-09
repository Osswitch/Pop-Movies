package com.example.zhang.popmovies.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhang.popmovies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    private static final int DETAIL_MOVIE_LOADER = 0;

    private Uri movieSelectedUri;

    private long movie_id;

    private TextView titleTextView;
    private TextView releaseDateTextView;
    private ImageView posterImageView;

    private TextView voteAverageTextView;
    private TextView plotSynopsisTextView;

    public static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW
    };

    static final int COLUMN_DETAIL_ID = 0;
    static final int COLUMN_MOVIE_ID = 1;
    static final int COLUMN_DETAIL_ORIGINAL_TITLE = 2;
    static final int COLUMN_DETAIL_RELEASE_DATE = 3;
    static final int COLUMN_DETAIL_POSTER_PATH = 4;
    static final int COLUMN_DETAIL_VOTE_AVERAGE = 5;
    static final int COLUMN_DETAIL_OVERVIEW = 6;


    public MovieDetailActivityFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_MOVIE_LOADER, null, this);
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
        }
        new FetchTrailerTask(getActivity()).execute(Long.toString(movie_id));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (movieSelectedUri == null) {
            return null;
        }
        return new CursorLoader(
                getActivity(),
                movieSelectedUri,
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (!cursor.moveToFirst()){
            return;
        }

        titleTextView.setText(cursor.getString(COLUMN_DETAIL_ORIGINAL_TITLE));
        releaseDateTextView.setText(cursor.getString(COLUMN_DETAIL_RELEASE_DATE));
        Picasso.with(getContext()).load(Utility.getPreviewImage(cursor))
                .into(posterImageView);
        voteAverageTextView.setText(cursor.getString(COLUMN_DETAIL_VOTE_AVERAGE));
        plotSynopsisTextView.setText(cursor.getString(COLUMN_DETAIL_OVERVIEW));

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
