package com.example.zhang.popmovies.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.zhang.popmovies.app.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class PreviewFragment extends Fragment {

    private final String LOG_TAG = PreviewFragment.class.getSimpleName();

    private MovieAdapter mMovieAdapter = null;

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
        String sortMethod = Utility.getPreferredSortMethod(getActivity());

        String sPreviewSelection = null;

        if (sortMethod.equals(getString(R.string.sort_entryValue_popularity))) {
            sPreviewSelection = MovieContract.MovieEntry.COLUMN_IS_POPULARITY + "=?";
        } else if (sortMethod.equals(getString(R.string.sort_entryValue_highestRate))) {
            sPreviewSelection = MovieContract.MovieEntry.COLUMN_IS_HIGHEST_RATE + "=?";
        }

        Cursor previewCursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                sPreviewSelection,
                new String[]{"1"},
                MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC"
        );

        mMovieAdapter = new MovieAdapter(
                getActivity(),
                previewCursor,
                0
        );

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_preview);
        gridView.setAdapter(mMovieAdapter);

        return rootView;
    }

    public void updateMovies() {
        //Read sort order method
        String sortMethod = Utility.getPreferredSortMethod(getActivity());
        new FetchMoviesTask(getActivity()).execute(sortMethod);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }
}
