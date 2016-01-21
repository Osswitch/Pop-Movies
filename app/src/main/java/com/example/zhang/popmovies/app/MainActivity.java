package com.example.zhang.popmovies.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_main, new PreviewFragment(), PREVIEW_FRAGMENT_TAG)
                    .commit();
        }

        if (findViewById(R.id.detail_movie_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_movie_container, new MovieDetailActivityFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
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
                    .findFragmentByTag(PREVIEW_FRAGMENT_TAG);
            if (previewFragment != null) {
                previewFragment.onSortMethodChange();
            }
            mSortMethod = sortMethod;
        }

        MovieDetailActivityFragment movieDetailActivityFragment
                = (MovieDetailActivityFragment) getSupportFragmentManager()
                .findFragmentByTag(DETAIL_FRAGMENT_TAG);
    }
}
