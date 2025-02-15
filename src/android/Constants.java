package com.lucasnpinheiro.mediastreaming;

public final class Constants {

    public static final String PACKAGE_NAME = "br.com.williarts.radio";

    public static final String MEDIA_SESSION_TAG = "media_streaming";

    public static final String DEFAULT_CHANNEL_ID = "playback_channel";
    public static final int DEFAULT_NOTIFICATION_ID = 1;
    public static final int DEFAULT_SELECTED_INDEX = 0;

    public static final String KEY_CHANNEL_ID = "channelId";
    public static final String KEY_CHANNEL_NAME = "channelName";
    public static final String KEY_NOTIFICATION_ID = "notificationId";
    public static final String KEY_MEDIA_STREAMS = "mediaStreams";
    public static final String KEY_SELECTED_INDEX = "selectedIndex";
    public static final String KEY_DATA = "data";
    public static final String KEY_CURRENT_WINDOW_INDEX = "currentWindowIndex";

    public static final String MSG_MEDIA_STREAM_FAILED = "Não foi possivel execultar o conteudo do stream informado.";

    private Constants() {
        throw new IllegalStateException("Constants class");
    }

}
