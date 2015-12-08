package com.example.zhang.popmovies.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
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
    private PreviewAdapter mPreviewAdapter = null;

    private static final String[] PREVIEW_COLUMNS = {
            MovieContract.MovieEntry._ID,   //necessary for CursorAdapter
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
    };

    static final int COL_PREVIEW_ID = 0;
    static final int COL_PREVIEW_MOVIE_ID = 1;
    static final int COL_PREVIEW_PATH = 2;

    public PreviewFragment() {
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

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_preview);
        gridView.setAdapter(mPreviewAdapter);

        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                        if (cursor != null) {
                            Intent intent = new Intent(getActivity(),MovieDetailActivity.class);
                            intent.setData(MovieContract.MovieEntry.buildMovieWithMovieId(
                                    cursor.getLong(COL_PREVIEW_MOVIE_ID)
                            ));

                            Long l = cursor.getLong(COL_PREVIEW_MOVIE_ID);
                            startActivity(intent);
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
        }

        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                PREVIEW_COLUMNS,
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
