package com.example.zhang.popmovies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PreviewFragment extends Fragment {

    private final String LOG_TAG = PreviewFragment.class.getSimpleName();

    private PreviewAdapter mPreviewAdapter = null;

    public PreviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_main, container, false);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //String[] fakeData = {"1","2","3","4"};
        //List<String> fakeList = new ArrayList<String>(Arrays.asList(fakeData));

        mPreviewAdapter = new PreviewAdapter(
                getActivity(),
                R.layout.grid_item_preview,
                new ArrayList<MovieInfo>()
        );

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_preview);
        gridView.setAdapter(mPreviewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieInfo movieInfo = mPreviewAdapter.getItem(position);
                Bundle movieInfoBundle = new Bundle();
                movieInfoBundle.putParcelable("movieInfo", movieInfo);
                Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtras(movieInfoBundle);
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    public void updateMovies() {
        //Read sort order method
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String sortMethod = sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_defValue));
        new FetchMoviesTask().execute(sortMethod);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieInfo[]> {

        //IN CASE IT IS NEEDED
        //"http://api.themoviedb.org/3/discover/movie?sort_by" +
        // "=popularity.desc&api_key=9010bf2f7e77213c6022420cf369a037"

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        //Get poster uri from json
        private MovieInfo[] getMovieInfoFromJson(String resultJSONStr)
        throws JSONException{

            final String MDB_RESULTS = "results";
            final String MDB_ID = "id";
            final String MDB_ORIGINAL_TITLE = "original_title";
            final String MDB_RELEASE_DATE = "release_date";
            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_VOTE_AVERAGE = "vote_average";
            final String MDB_OVERVIEW = "overview";

            JSONObject resultJSON = new JSONObject(resultJSONStr);
            JSONArray resultArray = resultJSON.getJSONArray(MDB_RESULTS);
            int pageMovieCounts = resultArray.length();
            MovieInfo[] movieInfos = new MovieInfo[pageMovieCounts];

            for (int i = 0; i < pageMovieCounts; i++) {
                JSONObject movieObject = resultArray.getJSONObject(i);
                MovieInfo movieInfo = new MovieInfo(movieObject.getString(MDB_ID),
                        movieObject.getString(MDB_ORIGINAL_TITLE),
                        movieObject.getString(MDB_RELEASE_DATE),
                        movieObject.getString(MDB_POSTER_PATH),
                        movieObject.getDouble(MDB_VOTE_AVERAGE),
                        movieObject.getString(MDB_OVERVIEW)
                );


                movieInfos[i] = movieInfo;
            }

            return movieInfos;
        }

        @Override
        protected MovieInfo[]doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resultJSONStr = null;

            String method = "discover";

            String category = "movie";

            String minCountNum = "500";

            String sortMethod = params[0];

            String API_KEY = getString(R.string.api_key);

            //Try block fetch JSON data from the cloud
            try{

                final String FETCH_BASE_URI = "http://api.themoviedb.org/3/";
                final String SORT_PARAM = "sort_by";
                final String COUNT_PARAM = "vote_count.gte";
                final String API_KEY_PARAM = "api_key";
                Uri uri = null;

                if (sortMethod == getString(R.string.sort_entries_popularity)) {
                    uri = Uri.parse(FETCH_BASE_URI).buildUpon()
                            .appendPath(method)
                            .appendPath(category)
                            .appendQueryParameter(SORT_PARAM, sortMethod)
                            .appendQueryParameter(API_KEY_PARAM, API_KEY)
                            .build();
                } else {

                    // When fetch the highest rated movies, restrict those which
                    // have been voted over 500 times
                    uri = Uri.parse(FETCH_BASE_URI).buildUpon()
                            .appendPath(method)
                            .appendPath(category)
                            .appendQueryParameter(SORT_PARAM, sortMethod)
                            .appendQueryParameter(COUNT_PARAM, minCountNum)
                            .appendQueryParameter(API_KEY_PARAM, API_KEY)
                            .build();
                }



                URL url = new URL(uri.toString());

                //Log.v(LOG_TAG, "URL is " + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line =null;

                while ((line = reader.readLine()) != null){
                    buffer.append(line + "/");
                }

                if (buffer.length() == 0){
                    return null;
                }

                resultJSONStr = buffer.toString();

            }catch(IOException e){
                Log.e(LOG_TAG, "error", e);
            }finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error", e);
                    }
                }
            }

            try {
                MovieInfo[] movieInfos = getMovieInfoFromJson(resultJSONStr);
                return  movieInfos;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieInfo[] movieInfos) {

            if (movieInfos != null) {
                mPreviewAdapter.clear();
                mPreviewAdapter.addAll(movieInfos);
            }
        }

    }
}
