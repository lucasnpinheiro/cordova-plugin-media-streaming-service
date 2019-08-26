package com.paulkjoseph.mediastreaming;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.exoplayer2.util.Util;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class MediaStreamingPlugin extends CordovaPlugin {

    private final static String TAG = MediaStreamingPlugin.class.getName();

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext command) throws JSONException {
        Log.i(TAG, "execute-[action]: " + action + ", [args]: " + args + ", [command]: " + command);

        Context context = cordova.getActivity().getApplicationContext();

        handleAction(context, action, args, command);

        return true;
    }

    private void handleAction(final Context context, final String action, final JSONArray args, final CallbackContext command) {
        String message = null;
        try {
            if (action != null && args != null) {
                MediaPlayerState requestedAction = null;
                try {
                    requestedAction = MediaPlayerState.valueOf(action);
                } catch (Exception ex) {
                    Log.e(TAG, "handleAction[error-action]: " + action, ex);
                }
                if (requestedAction == MediaPlayerState.start) {
                    if (args.length() > 4) {
                        Log.i(TAG, "handleAction: start");
                        Intent intent = new Intent(context, MediaStreamingService.class);
                        intent.setAction("start");
                        intent.putExtra("channelId", args.getString(0))
                                .putExtra("channelName", args.getString(1))
                                .putExtra("notificationId", args.getString(2))
                                .putExtra("mediaStreams", args.getString(3))
                                .putExtra("selectedIndex", args.getString(4));
                        Util.startForegroundService(context, intent);
                    } else {
                        message = "Invalid request. Args should contain atleast 5 elements but found " + args.length();
                        Log.e(TAG, "handleAction-start[error]:  " + message);
                    }
                } else if (requestedAction == MediaPlayerState.play) {
                    if (args.length() > 0) {
                        Log.i(TAG, "handleAction: play");
                        Intent intent = new Intent(context, MediaStreamingService.class);
                        intent.setAction("start");
                        intent.putExtra("selectedIndex", args.getString(0));
                        Util.startForegroundService(context, intent);
                    } else {
                        message = "Invalid request. Args should contain atleast 5 elements but found " + args.length();
                        Log.e(TAG, "handleAction-play[error]:  " + message);
                    }
                } else if (requestedAction == MediaPlayerState.pause) {
                    Log.i(TAG, "handleAction: pause");
                    Intent intent = new Intent(context, MediaStreamingService.class);
                    intent.setAction("pause");
                    Util.startForegroundService(context, intent);
                } else if (requestedAction == MediaPlayerState.stop) {
                    Log.i(TAG, "handleAction: stop");
                    Intent intent = new Intent(context, MediaStreamingService.class);
                    intent.setAction("stop");
                    Util.startForegroundService(context, intent);
                } else {
                    message = "Requested action [" + action + "] not yet implemented";
                    Log.e(TAG, "handleAction[action]: " + message);
                }
            }
        } catch (Exception ex) {
            message = "Unable to process action [" + action + "]. Error: " + ex.getMessage();
            Log.e(TAG, "handleAction[error]: " + message, ex);
        }

        if (command != null) {
            if (message != null) {
                command.error(message);
            } else {
                message = "Service executed successfully";
                Log.i(TAG, "handleAction[command.success(message)]: " + message);
                command.success(message);
            }
        } else {
            message = "Unable to execute command since its either null or empty!";
            Log.i(TAG, "handleAction[command.error(message)]: " + message);
        }

    }
}
