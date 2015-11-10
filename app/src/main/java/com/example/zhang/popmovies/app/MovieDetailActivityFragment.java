package com.example.zhang.popmovies.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    MovieInfo movieInfo = new MovieInfo();

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String IMAGE_BASE_URI = "http://image.tmdb.org/t/p";
        final String IMAGE_SIZE = "w500";

        //return inflater.inflate(R.layout.fragment_movie_detail, container, false);
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();

        TextView titleTextView = (TextView) rootView.findViewById(R.id.detail_title_textView);
        ImageView posterImageView
                = (ImageView) rootView.findViewById(R.id.detail_poster_imageView);
        TextView releaseDateTextView
                = (TextView) rootView.findViewById(R.id.detail_release_date_textView);
        TextView voteAverageTextView
                = (TextView) rootView.findViewById(R.id.detail_vote_average_textView);
        TextView plotSynopsisTextView
                = (TextView) rootView.findViewById(R.id.detail_plot_synopsis_textView);

        if (intent != null && intent.getExtras() != null) {
            Bundle mBundle = intent.getExtras();
            movieInfo = (MovieInfo) mBundle.getParcelable("movieInfo");

            Uri uri = Uri.parse(IMAGE_BASE_URI).buildUpon()
                    .appendEncodedPath(IMAGE_SIZE)
                    .appendEncodedPath(movieInfo.posterPath)
                    .build();

            titleTextView.setText(movieInfo.originalTitle);
            Picasso.with(getContext()).load(uri).into(posterImageView);
            releaseDateTextView.setText(movieInfo.releaseDate);
            voteAverageTextView.setText(movieInfo.voteAverage.toString());
            plotSynopsisTextView.setText(movieInfo.overview);
        }
        return rootView;
    }
}
