package com.example.zhang.popmovies.app;

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
                new ArrayList<Uri>()
        );

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_preview);
        gridView.setAdapter(mPreviewAdapter);

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

    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        //IN CASE IT IS NEEDED
        //"http://api.themoviedb.org/3/discover/movie?sort_by" +
        // "=popularity.desc&api_key=9010bf2f7e77213c6022420cf369a037"

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /*private final String API_KEY_ADDRESS
                = "/home/zhang/AndroidStudioProjects/PopMovies/API_KEY";


        private String getAPIKEY (String apiKeyAdd) {
            String API_KEY = null;
            String line = null;
            File file = new File(apiKeyAdd);
            try {
                BufferedReader apiReader = new BufferedReader(new FileReader(file));
                while ((line = apiReader.readLine()) != null ) {
                    API_KEY = line;
                }
            } catch (IOException e) {
                Log.v(LOG_TAG, "API KEY READING ERROR", e);
            }
            return API_KEY;
        }*/



        //Get poster uri from json
        private String[] getPosterUriFromJson(String resultJSONStr)
        throws JSONException{

            final String OWM_RESULTS = "results";
            final String OWM_POSTER_PATH = "poster_path";

            JSONObject resultJSON = new JSONObject(resultJSONStr);
            JSONArray resultArray = resultJSON.getJSONArray(OWM_RESULTS);
            int pageMovieCounts = resultArray.length();
            String[] posterUris = new String[pageMovieCounts];

            for (int i = 0; i < pageMovieCounts; i++) {
                JSONObject movieObject = resultArray.getJSONObject(i);
                posterUris[i] = movieObject.getString(OWM_POSTER_PATH);
            }



            return posterUris;
        }

        @Override
        protected String[]doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String resultJSONStr = null;

            String method = "discover";

            String category = "movie";

            String minCountNum = "500";

            String sortMethod = params[0];

            String API_KEY = "";

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
                String[] posterUris = getPosterUriFromJson(resultJSONStr);
                return  posterUris;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {

            final String IMAGE_BASE_URI = "http://image.tmdb.org/t/p";
            final String IMAGE_SIZE = "w500";

            if (strings != null){
                mPreviewAdapter.clear();
                for (int i = 0; i < strings.length; i++) {
                    Uri uri = Uri.parse(IMAGE_BASE_URI).buildUpon()
                            .appendEncodedPath(IMAGE_SIZE)
                            .appendEncodedPath(strings[i])
                            .build();

                    //Log.v(LOG_TAG, "Poster are " + firstUri + " and " + secondUri);
                    mPreviewAdapter.add(uri);
                }
            }
        }


    }
}
