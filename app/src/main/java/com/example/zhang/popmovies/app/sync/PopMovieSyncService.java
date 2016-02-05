package com.example.zhang.popmovies.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by zhang on 04/02/16.
 */
public class PopMovieSyncService extends Service {

    private static final String LOG_TAG = PopMovieSyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static PopMovieSyncAdapter sPopMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate PopMovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (sPopMovieSyncAdapter == null) {
                sPopMovieSyncAdapter = new PopMovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopMovieSyncAdapter.getSyncAdapterBinder();
    }
}
