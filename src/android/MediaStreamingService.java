package com.paulkjoseph.mediastreaming;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.ui.PlayerNotificationManager.BitmapCallback;
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter;
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.apache.cordova.CordovaActivity;

import java.util.List;

import static com.paulkjoseph.mediastreaming.Constants.DEFAULT_CHANNEL_ID;
import static com.paulkjoseph.mediastreaming.Constants.DEFAULT_NOTIFICATION_ID;
import static com.paulkjoseph.mediastreaming.Constants.DEFAULT_SELECTED_INDEX;
import static com.paulkjoseph.mediastreaming.Constants.KEY_CHANNEL_ID;
import static com.paulkjoseph.mediastreaming.Constants.KEY_CHANNEL_NAME;
import static com.paulkjoseph.mediastreaming.Constants.KEY_MEDIA_STREAMS;
import static com.paulkjoseph.mediastreaming.Constants.KEY_NOTIFICATION_ID;
import static com.paulkjoseph.mediastreaming.Constants.KEY_SELECTED_INDEX;
import static com.paulkjoseph.mediastreaming.Constants.MEDIA_SESSION_TAG;
import static com.paulkjoseph.mediastreaming.Constants.PACKAGE_NAME;

public class MediaStreamingService extends Service {

    private final static String TAG = MediaStreamingService.class.getName();

    private Context context;
    private SimpleExoPlayer player;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSessionCompat mediaSession;
    private MediaSessionConnector mediaSessionConnector;

    private MediaStreamRequest mediaStreamRequest;
    private MediaPlayerState currentState = MediaPlayerState.play;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Log.i(TAG, "onCreate[context]: " + (context != null));
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy[context]: " + (context != null));
        close();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind[intent]: " + intent);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand[intent]: " + intent + ", [flags]: " + flags + ", [startId]: " + startId);
        handleIntent(intent);
        loadPlayer(intent);
        Log.i(TAG, "onStartCommand[START_STICKY]: " + START_STICKY);
        return START_STICKY;
    }

    private void initPlayer(@NonNull final Context context) {
        player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_SPEECH)
                .build();
        player.setAudioAttributes(audioAttributes, true);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, mediaStreamRequest.getChannelName()));
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        for (MediaStream mediaStream : mediaStreamRequest.getMediaStreams()) {
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .setLoadErrorHandlingPolicy(new CustomLoadErrorHandlingPolicy())
                    .createMediaSource(Uri.parse(mediaStream.getUri()));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        player.prepare(concatenatingMediaSource);

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                final int currentWindowIndex = player.getCurrentWindowIndex();
                Log.e(TAG, "onPlayerError[url]: " + mediaStreamRequest.getMediaStreams().get(player.getCurrentWindowIndex()).getUri(), error);
                MediaStreamUtils.broadcastMessage(context, "PLAYBACK_ERROR", currentWindowIndex);
                stopSelf();
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                final int currentWindowIndex = player.getCurrentWindowIndex();
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    Log.i(TAG, "onPlayerStateChanged[Active playback]: ");
                    MediaStreamUtils.broadcastMessage(context, "ACTIVE_PLAYBACK", currentWindowIndex);
                } else if (playWhenReady) {
                    Log.i(TAG, "onPlayerStateChanged[Not playing because playback ended, the player is buffering, stopped or failed. Check playbackState and player.getPlaybackError for details.]: ");
                    MediaStreamUtils.broadcastMessage(context, "PLAYBACK_ENDED", currentWindowIndex);
                } else {
                    Log.i(TAG, "onPlayerStateChanged[Paused by app.]: ");
                    MediaStreamUtils.broadcastMessage(context, "PAUSED_BY_APP", currentWindowIndex);
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.i(TAG, "onPositionDiscontinuity[reason]: " + reason);
            }
        });

        player.seekTo(mediaStreamRequest.getSelectedIndex(), C.TIME_UNSET);
        player.setPlayWhenReady(true);

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                context,
                mediaStreamRequest.getChannelId(),
                com.google.android.exoplayer2.ui.R.string.exo_track_stereo,
                mediaStreamRequest.getNotificationId(),
                new MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return mediaStreamRequest.getMediaStreams().get(player.getCurrentWindowIndex()).getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        return PendingIntent.getActivity(context, 0, getParentActivityIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return mediaStreamRequest.getMediaStreams().get(player.getCurrentWindowIndex()).getDescription();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, BitmapCallback callback) {
                        MediaStreamUtils.loadBitmapFromUrl(context, mediaStreamRequest.getMediaStreams().get(player.getCurrentWindowIndex()).getCover(), callback);
                        return null;
                    }
                }
        );
        playerNotificationManager.setNotificationListener(new NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                stopSelf();
            }
        });
        playerNotificationManager.setUseNavigationActions(mediaStreamRequest.getMediaStreams().size() > 1);
        playerNotificationManager.setFastForwardIncrementMs(0);
        playerNotificationManager.setRewindIncrementMs(0);
        playerNotificationManager.setPlayer(player);

        mediaSession = new MediaSessionCompat(context, MEDIA_SESSION_TAG);
        mediaSession.setActive(true);
        playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());

        mediaSessionConnector = new MediaSessionConnector(mediaSession);
        mediaSessionConnector.setQueueNavigator(new TimelineQueueNavigator(mediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return MediaStreamUtils.getMediaDescription(context, mediaStreamRequest.getMediaStreams().get(windowIndex));
            }
        });
        mediaSessionConnector.setPlayer(player, null);
    }

    private void handleIntent(final Intent intent) {
        Log.i(TAG, "handleIntent[intent]: " + intent);
        if (intent != null) {
            try {
                currentState = MediaPlayerState.valueOf(intent.getAction());
            } catch (Exception ex) {
                Log.e(TAG, "handleIntent[currentState]: " + currentState, ex);
                currentState = MediaPlayerState.play;
            }
            Log.i(TAG, "handleIntent[currentState]: " + currentState);
            final String channelId = intent.getStringExtra(KEY_CHANNEL_ID);
            Log.i(TAG, "handleIntent[channelId]: " + channelId);
            final String channelName = intent.getStringExtra(KEY_CHANNEL_NAME);
            Log.i(TAG, "handleIntent[channelName]: " + channelName);
            int notificationId = DEFAULT_NOTIFICATION_ID;
            try {
                notificationId = Integer.getInteger(intent.getStringExtra(KEY_NOTIFICATION_ID)).intValue();
            } catch (Exception ex) {
            }
            Log.i(TAG, "handleIntent[notificationId]: " + notificationId);
            List<MediaStream> mediaStreams = MediaStreamUtils.deserializeMediaStreams(intent.getStringExtra(KEY_MEDIA_STREAMS));
            Log.i(TAG, "handleIntent[mediaStreams]: " + mediaStreams);
            int selectedIndex = DEFAULT_SELECTED_INDEX;
            try {
                selectedIndex = Integer.valueOf(intent.getStringExtra(KEY_SELECTED_INDEX)).intValue();
            } catch (Exception ex) {
            }
            Log.i(TAG, "handleIntent[selectedIndex]: " + selectedIndex);
            mediaStreamRequest = new MediaStreamRequest(channelId == null ? DEFAULT_CHANNEL_ID : channelId, channelName, notificationId, mediaStreams, selectedIndex);
        } else {
            Log.w(TAG, "handleIntent[intent]: intent == nul");
        }
    }

    private void loadPlayer(final Intent intent) {
        Log.i(TAG, "loadPlayer[currentState]: " + currentState + ", [mediaStreamRequest]: " + mediaStreamRequest);
        if (player == null && intent != null) {
            Log.i(TAG, "loadPlayer[player]: player == null");
            if (mediaStreamRequest != null && mediaStreamRequest.getMediaStreams() != null && mediaStreamRequest.getMediaStreams().size() > 0) {
                initPlayer(context);
            }
        } else if (intent != null) {
            Log.i(TAG, "loadPlayer[player]: player != null");
            switch (currentState) {
                case pause:
                    pause();
                    break;
                case stop:
                    stop();
                    break;
                case close:
                    close();
                    break;
                default:
                    play(mediaStreamRequest.getSelectedIndex());
            }
        }
    }

    private void pause() {
        Log.i(TAG, "pause[player != null]: " + (player != null));
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    private void play() {
        Log.i(TAG, "play[player != null]: " + (player != null));
        player.setPlayWhenReady(true);
    }

    private void play(int windowIndex) {
        play();
        player.seekTo(windowIndex, C.TIME_UNSET);
    }

    private void stop() {
        Log.i(TAG, "stop[player != null]: " + (player != null));
        player.stop();
    }

    private void close() {
        Log.i(TAG, "close[mediaSession != null]: " + (mediaSession != null)
                + ", [mediaSessionConnector != null]: " + (mediaSessionConnector != null)
                + ", [playerNotificationManager != null]: " + (playerNotificationManager != null)
                + ", [player != null]: " + (player != null));
        if (mediaSession != null) {
            mediaSession.release();
        }
        if (mediaSessionConnector != null) {
            mediaSessionConnector.setPlayer(null, null);
        }
        if (playerNotificationManager != null) {
            playerNotificationManager.setPlayer(null);
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private Intent getParentActivityIntent() {
        String packageName = MediaStreamUtils.getPackageStartsWith(PACKAGE_NAME);
        Log.i(TAG, "getParentActivityIntent[packageName]: " + packageName);
        try {
            if (packageName != null) {
                return new Intent(context, Class.forName(packageName + ".MainActivity"));
            } else {
                return new Intent(context, CordovaActivity.class);
            }
        } catch (Exception ex) {
            Log.e(TAG, "getParentActivityIntent[packageName]: " + packageName, ex);
            return new Intent(context, CordovaActivity.class);
        }
    }

}
