package com.example.zhang.popmovies.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
                new ArrayList<String>()
        );

        GridView gridView = (GridView) rootView.findViewById(R.id.gridView_preview);
        gridView.setAdapter(mPreviewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MovieInfo movieInfo = mPreviewAdapter.getItem(position);
//                Bundle movieInfoBundle = new Bundle();
//                movieInfoBundle.putParcelable("movieInfo", movieInfo);
//                Intent detailIntent = new Intent(getActivity(), MovieDetailActivity.class)
//                        .putExtras(movieInfoBundle);
//                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    public void updateMovies() {
        //Read sort order method
        String sortMethod = Utility.getPreferredSortMethod(getActivity());
        new FetchMoviesTask(getActivity(), mPreviewAdapter).execute(sortMethod);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }
}
