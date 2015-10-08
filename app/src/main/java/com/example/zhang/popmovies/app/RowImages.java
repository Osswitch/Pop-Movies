package com.example.zhang.popmovies.app;

import android.net.Uri;


/**
 * Created by zhang on 05/10/15.
 */
public class RowImages {

    private Uri firstRowImageUri;
    private Uri secondRowImageUri;

    public RowImages(Uri firstRowImageUri, Uri secondRowImageUri){
        this.firstRowImageUri = firstRowImageUri;
        this.secondRowImageUri = secondRowImageUri;
    }

    public Uri getFirstRowImageUri() {
        return firstRowImageUri;
    }

    private void setFirstRowImageUri(Uri firstRowImageUri) {
        this.firstRowImageUri = firstRowImageUri;
    }

    public Uri getSecondRowImageUri() {
        return secondRowImageUri;
    }

    private void setSecondRowImageUri(Uri secondRowImageUri) {
        this.secondRowImageUri = secondRowImageUri;
    }
}
