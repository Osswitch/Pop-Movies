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
import com.example.zhang.popmovies.app.sync.PopMovieSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class PreviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = PreviewFragment.class.getSimpleName();

    private static final int FETCH_PREVIEW_MOVIE_LOADER_ID = 0;
    private PreviewAdapter mPreviewAdapter;
    private GridView mPreviewGridView;

    // indicate the position of gridview
    private int mPosition = GridView.INVALID_POSITION;
    // key to find mPosition in savedInstanceState
    private static final String SELECTED_KEY = "selected_position";

    // Using to indicate the column of cursor
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

    // interface to perform item selected action
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
            // refresh button to refresh the preview movie thumbnails.
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

        // initialize the adapter to displaying thumbnail
        mPreviewAdapter = new PreviewAdapter(
                getActivity(),
                null,
                0
        );

        mPreviewGridView = (GridView) rootView.findViewById(R.id.gridView_preview);
        mPreviewGridView.setAdapter(mPreviewAdapter);

        // if one item is clicked before save the position it is clicked in mPosition.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mPreviewGridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mPosition = position;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    // after changing the sort method of preview page, reload the content inside.
    public void onSortMethodChange() {
        updateMovies();
        getLoaderManager().restartLoader(FETCH_PREVIEW_MOVIE_LOADER_ID, null, this);
    }

    public void updateMovies() {
//        //Read sort order method
//        String sortMethod = Utility.getPreferredSortMethod(getActivity());
//
//        Intent alarmIntent = new Intent(getActivity(), PopService.AlarmReceiver.class);
//        alarmIntent.putExtra(PopService.SORT_METHOD_EXTRA, sortMethod);
//
//        //Wrap in a pending intent which only fires once.
//        PendingIntent pendingIntent = PendingIntent
//                .getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);
//
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//
//        //Set the AlarmManager to wake up the system.
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent);
        PopMovieSyncAdapter.syncImmediately(getActivity());
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

        // if item is clicked before, jump to the previous clicked position
        if (mPosition != GridView.INVALID_POSITION) {
            mPreviewGridView.setSelection(mPosition);
        }

        // in tablet display the detail info of first movie as default
        if (getActivity().findViewById(R.id.detail_movie_container) != null) {
            if (mPosition == GridView.INVALID_POSITION) {
                mPreviewGridView.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                mPreviewGridView.performItemClick(null, 0, 0);
                            }
                        }
                );
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mPreviewAdapter.swapCursor(null);
    }
}
