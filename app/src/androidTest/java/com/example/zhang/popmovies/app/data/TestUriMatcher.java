package com.example.zhang.popmovies.app.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by zhang on 17/11/15.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final String LOG_TAG = TestUriMatcher.class.getSimpleName();

    private static final int TEST_MOVIE_ID = 200000;
    private static final String TEST_TRAILER_ID = "12345";
    private static final String TEST_REVIEW_ID = "12345";

    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_MOVIE_ID = MovieContract.MovieEntry.buildMovieWithMovieId(TEST_MOVIE_ID);

    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.CONTENT_URI;
    private static final Uri TEST_TRAILER_WITH_MOVIE_ID = MovieContract.TrailerEntry.buildTrailerWithMovieId(TEST_MOVIE_ID);
    private static final Uri TEST_TRAILER_WITH_MOVIE_ID_AND_TRAILER_ID = MovieContract.TrailerEntry.buildTrailerWithMovieIdAndTrailerId(TEST_MOVIE_ID, TEST_TRAILER_ID);

    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.CONTENT_URI;
    private static final Uri TEST_REVIEW_WITH_MOVIE_ID = MovieContract.ReviewEntry.buildReviewWithMovieId(TEST_MOVIE_ID);
    private static final Uri TEST_REVIEW_WITH_MOVIE_ID_AND_REVIEW_ID = MovieContract.ReviewEntry.buildReviewWithMovieIdAndReviewId(TEST_MOVIE_ID, TEST_REVIEW_ID);

    public void testUriMatcher() {
        UriMatcher testUriMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The Movie Uri was matched incorrectly",
                testUriMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The Movie with movie id was matched incorrectly",
                testUriMatcher.match(TEST_MOVIE_WITH_MOVIE_ID), MovieProvider.MOVIE_WITH_MOVIE_ID);

        assertEquals("Error: The Trailer Uri was matched incorrectly",
                testUriMatcher.match(TEST_TRAILER_DIR), MovieProvider.TRAILER);
        assertEquals("Error: The Trailer with movie id was matched incorrectly",
                testUriMatcher.match(TEST_TRAILER_WITH_MOVIE_ID), MovieProvider.TRAILER_WITH_MOVIE_ID);
        assertEquals("Error: The Trailer with movie id and trailer name was matched incorrectly",
                testUriMatcher.match(TEST_TRAILER_WITH_MOVIE_ID_AND_TRAILER_ID), MovieProvider.TRAILER_WITH_MOVIE_ID_AND_TRAILER_ID);

        assertEquals("Error: The Review Uri was matched incorrectly",
                testUriMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEW);
        assertEquals("Error: The Review with movie id was matched incorrectly",
                testUriMatcher.match(TEST_REVIEW_WITH_MOVIE_ID), MovieProvider.REVIEW_WITH_MOVIE_ID);
        assertEquals("Error: The Review with movie id and review id was matched incorrectly",
                testUriMatcher.match(TEST_REVIEW_WITH_MOVIE_ID_AND_REVIEW_ID), MovieProvider.REVIEW_WITH_MOVIE_ID_AND_REVIEW_ID);
    }
}
