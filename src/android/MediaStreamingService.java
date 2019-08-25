package com.paulkjoseph.mediastreaming;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MediaStreamingService extends Service {

    private final static String TAG = MediaStreamingService.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand[intent]: " + intent);

        if (intent != null) {
            Log.i(TAG, "onStartCommand[action]: " + intent.getAction());
            Log.i(TAG, "onStartCommand[channelId]: " + intent.getStringExtra("channelId"));
            Log.i(TAG, "onStartCommand[channelName]: " + intent.getStringExtra("channelName"));
            Log.i(TAG, "onStartCommand[notificationId]: " + intent.getStringExtra("notificationId"));
            Log.i(TAG, "onStartCommand[mediaStreams]: " + intent.getStringExtra("mediaStreams"));
            Log.i(TAG, "onStartCommand[selectedIndex]: " + intent.getStringExtra("selectedIndex"));
        } else {
            Log.i(TAG, "onStartCommand[intent]: intent is null or empty");
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind[intent]: " + intent);
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
