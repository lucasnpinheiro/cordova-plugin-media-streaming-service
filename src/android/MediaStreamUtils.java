package com.paulkjoseph.mediastreaming;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MediaStreamUtils {

    private static final String TAG = MediaStreamUtils.class.getName();

    public static MediaDescriptionCompat getMediaDescription(Context context, MediaStream mediaStream) {
        Log.i(TAG, "getMediaDescription[mediaStream]: " + mediaStream);
        Bundle extras = new Bundle();
        Bitmap bitmap = getBitmap(context, android.R.drawable.stat_sys_headset);
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, Uri.parse(mediaStream.getCover()));
        extras.putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, Uri.parse(mediaStream.getCover()));
        String description = mediaStream.getDescription() != null && !mediaStream.getDescription().equals(mediaStream.getTitle()) ? mediaStream.getDescription() : null;
        return new MediaDescriptionCompat.Builder()
                .setMediaId(mediaStream.getIdentifier())
                .setIconUri(mediaStream.getCover() != null ? Uri.parse(mediaStream.getCover()) : null)
                .setIconBitmap(mediaStream.getCover() == null ? bitmap : null)
                .setTitle(mediaStream.getTitle())
                .setDescription(description)
                .setExtras(extras)
                .build();
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int bitmapResource) {
        return ((BitmapDrawable) context.getResources().getDrawable(bitmapResource)).getBitmap();
    }

    public static void loadBitmapFromUrl(Context context, String url, final PlayerNotificationManager.BitmapCallback callback) {
        Log.i(TAG, "loadBitmapFromUrl[url]: " + url);
        Picasso.with(context).load(Uri.parse(url)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                callback.onBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                 callback.onBitmap(getBitmap(context, android.R.drawable.stat_sys_headset));
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.i(TAG, "onPrepareLoad[placeHolderDrawable]: " + placeHolderDrawable);
            }
        });

    }

    public static List<MediaStream> deserializeMediaStreams(String jsonString) {
        List<MediaStream> list = Collections.emptyList();
        try {
            Log.i(TAG, "deserializeMediaStreams[jsonString]: " + jsonString);
            MediaStream[] mediaStreams = (new Gson()).fromJson(jsonString, MediaStream[].class);
            list = Arrays.asList(mediaStreams);
        } catch (Exception ex) {
            Log.e(TAG, "deserializeMediaStreams[jsonString]: " + jsonString, ex);
        }
        return list;
    }

}
