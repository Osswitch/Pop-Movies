package com.example.zhang.popmovies.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.zhang.popmovies.app.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class PreviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = PreviewFragment.class.getSimpleName();

    private static final int FETCH_PREVIEW_MOVIE_LOADER_ID = 0;
    private PreviewAdapter mPreviewAdapter;
    private GridView gridView;

    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,   //necessary for CursorAdapter
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_IS_FAVOURITE

    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_MOVIE_ID = 1;
    static final int COL_MOVIE_ORIGINAL_TITLE = 2;
    static final int COL_MOVIE_OVERVIEW = 3;
    static final int COL_MOVIE_RELEASE_DATE = 4;
    static final int COL_MOVIE_POSTER_PATH = 5;
    static final int COL_MOVIE_VOTE_AVERAGE = 6;
    static final int COL_MOVIE_IS_FAVOURITE = 7;


    public PreviewFragment() {
    }

    public interface CallBack {
        public void onItemSelected(Uri movieUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_main, container, false);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mPreviewAdapter = new PreviewAdapter(
                getActivity(),
                null,
                0
        );

        gridView = (GridView) rootView.findViewById(R.id.gridView_preview);
        gridView.setAdapter(mPreviewAdapter);

        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                        if (cursor != null) {

                            long movie_id = cursor.getLong(COL_MOVIE_MOVIE_ID);
                            new FetchTrailerAndReviewTask(getActivity()).execute(Long.toString(movie_id));
                            ((CallBack) getActivity()).onItemSelected(MovieContract
                                    .MovieEntry.buildMovieWithMovieId(movie_id));

                        }
                    }
                }
        );

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FETCH_PREVIEW_MOVIE_LOADER_ID, null, this);
    }

    void onSortMethodChange() {
        updateMovies();
        getLoaderManager().restartLoader(FETCH_PREVIEW_MOVIE_LOADER_ID, null, this);
    }

    public void updateMovies() {
        //Read sort order method
        String sortMethod = Utility.getPreferredSortMethod(getActivity());
        new FetchMoviesTask(getActivity()).execute(sortMethod);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortMethod = Utility.getPreferredSortMethod(getActivity());

        String sPreviewSelection = null;

        String sortOrder = null;

        if (sortMethod.equals(getString(R.string.sort_entryValue_popularity))) {
            sPreviewSelection = MovieContract.MovieEntry.COLUMN_IS_POPULARITY + "=?";
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else if (sortMethod.equals(getString(R.string.sort_entryValue_highestRate))) {
            sPreviewSelection = MovieContract.MovieEntry.COLUMN_IS_HIGHEST_RATE + "=?";
            sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        } else if (sortMethod.equals(getString(R.string.sort_entryValue_favourite))) {
            sPreviewSelection = MovieContract.MovieEntry.COLUMN_IS_FAVOURITE + "=?";
        }

        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                sPreviewSelection,
                new String[]{"1"},
                sortOrder

        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mPreviewAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mPreviewAdapter.swapCursor(null);
    }
}
