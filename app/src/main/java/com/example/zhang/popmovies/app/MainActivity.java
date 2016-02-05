package com.example.zhang.popmovies.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zhang.popmovies.app.sync.PopMovieSyncAdapter;

public class MainActivity extends AppCompatActivity implements PreviewFragment.CallBack{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String PREVIEW_FRAGMENT_TAG = "PFTAG";
    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";

    private Boolean mTwoPane;
    private String mSortMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSortMethod = Utility.getPreferredSortMethod(this);

        if (findViewById(R.id.detail_movie_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_movie_container, new MovieDetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_main, new PreviewFragment(), PREVIEW_FRAGMENT_TAG)
                        .commit();
            }
        }

        PopMovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String sortMethod = Utility.getPreferredSortMethod(this);
        if (sortMethod != null && sortMethod != mSortMethod) {
            PreviewFragment previewFragment = (PreviewFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_main);
            if (previewFragment != null) {
                previewFragment.onSortMethodChange();
            }
            mSortMethod = sortMethod;
        }

        MovieDetailActivityFragment movieDetailActivityFragment
                = (MovieDetailActivityFragment) getSupportFragmentManager()
                .findFragmentByTag(DETAIL_FRAGMENT_TAG);
    }

    @Override
    public void onItemSelected(Uri movieUri) {

        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailActivityFragment.DETAIL_MOVIE_URI, movieUri);

            MovieDetailActivityFragment movieDetailActivityFragment
                    = new MovieDetailActivityFragment();
            movieDetailActivityFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, movieDetailActivityFragment).commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class).setData(movieUri);
            startActivity(intent);
        }
    }
}
