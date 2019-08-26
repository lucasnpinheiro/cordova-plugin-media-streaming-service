package com.paulkjoseph.mediastreaming;

public final class Constants {

    public static final String MEDIA_SESSION_TAG = "media_streaming";

    public static final String DEFAULT_CHANNEL_ID = "playback_channel";
    public static final int DEFAULT_NOTIFICATION_ID = 1;
    public static final int DEFAULT_SELECTED_INDEX = 0;

    public static final String KEY_CHANNEL_ID = "channelId";
    public static final String KEY_CHANNEL_NAME = "channelName";
    public static final String KEY_NOTIFICATION_ID = "notificationId";
    public static final String KEY_MEDIA_STREAMS = "mediaStreams";
    public static final String KEY_SELECTED_INDEX = "selectedIndex";

    private Constants() {
        throw new IllegalStateException("Constants class");
    }

}
