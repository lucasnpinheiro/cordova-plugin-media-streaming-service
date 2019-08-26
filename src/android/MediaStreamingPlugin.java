package com.paulkjoseph.mediastreaming;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class MediaStreamingPlugin extends CordovaPlugin {

    private final static String TAG = MediaStreamingPlugin.class.getName();

    @Override
    @TargetApi(26)
    public boolean execute(final String action, final JSONArray args, final CallbackContext command) throws JSONException {
        Log.i(TAG, "execute-[action]: " + action + ", [args]: " + args + ", [command]: " + command);

        if (android.os.Build.VERSION.SDK_INT >= 26 && action != null && args != null) {
            Activity activity = cordova.getActivity();
            Intent intent = new Intent(activity, MediaStreamingService.class);

            if (action.equals("start")) {
                if (args.length() > 4) {
                    Log.i(TAG, "execute: start");
                    intent.setAction("start");
                    intent.putExtra("channelId", args.getString(0))
                            .putExtra("channelName", args.getString(1))
                            .putExtra("notificationId", args.getString(2))
                            .putExtra("mediaStreams", args.getString(3))
                            .putExtra("selectedIndex", args.getString(4));
                    activity.getApplicationContext().startForegroundService(intent);
                } else {
                    String message = "Invalid request. Args should contain atleast 5 elements but found " + args.length();
                    Log.e(TAG, "execute-start[error]:  " + message);
                    command.error(message);
                }
            } else if (action.equals("play")) {
                if (args.length() > 0) {
                    Log.i(TAG, "execute: play");
                    intent.setAction("start");
                    intent.putExtra("selectedIndex", args.getString(0));
                    activity.getApplicationContext().startService(intent);
                } else {
                    String message = "Invalid request. Args should contain atleast 5 elements but found " + args.length();
                    Log.e(TAG, "execute-play[error]:  " + message);
                    command.error(message);
                }
            } else if (action.equals("pause")) {
                Log.i(TAG, "execute: pause");
                intent.setAction("pause");
                activity.getApplicationContext().startService(intent);
            } else if (action.equals("stop")) {
                Log.i(TAG, "execute: stop");
                intent.setAction("stop");
                activity.getApplicationContext().startService(intent);
            } else {
                String message = "Requested action [" + action + "] not yet implemented";
                Log.e(TAG, "execute[action]: " + message);
                command.error(message);
            }
        }

        if (command != null) {
            String message = "Service executed successfully";
            Log.i(TAG, "execute[command.success(message)]: " + message);
            command.success(message);
        } else {
            String message = "Unable to execute command since its either null or empty!";
            Log.i(TAG, "execute[command.error(message)]: " + message);
            command.error(message);
        }

        return true;
    }

}
