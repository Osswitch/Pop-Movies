package com.example.zhang.popmovies.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by zhang on 04/02/16.
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class PopMovieAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private PopMovieAuthenticator mPopMovieAuthenticator;

    // Create a new authenticator object
    @Override
    public void onCreate() {
        mPopMovieAuthenticator = new PopMovieAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mPopMovieAuthenticator.getIBinder();
    }
}
