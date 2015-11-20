package com.example.zhang.popmovies.app.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by zhang on 17/11/15.
 */
public class TestUriMathcer extends AndroidTestCase {

    private static final String LOG_TAG = TestUriMathcer.class.getSimpleName();

    private static final int TEST_MOVIE_ID = 200000;

    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_MOVIE_ID = MovieContract.MovieEntry.buildMovieWithMovieId(TEST_MOVIE_ID);

    public void testUriMatcher() {
        UriMatcher testUriMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The Movie Uri was matched incorrectly",
                testUriMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The Movie with movie id was mathched incorrectly",
                testUriMatcher.match(TEST_MOVIE_WITH_MOVIE_ID), MovieProvider.MOVIE_WITH_MOVIE_ID);
    }
}
