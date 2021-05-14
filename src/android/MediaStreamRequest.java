package com.lucasnpinheiro.mediastreaming;

import java.util.List;

public class MediaStreamRequest {

    private String channelId;
    private String channelName;
    private int notificationId;
    private List<MediaStream> mediaStreams;
    private int selectedIndex;

    public MediaStreamRequest(String channelId, String channelName, int notificationId, List<MediaStream> mediaStreams, int selectedIndex) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.notificationId = notificationId;
        this.mediaStreams = mediaStreams;
        this.selectedIndex = selectedIndex;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public List<MediaStream> getMediaStreams() {
        return mediaStreams;
    }

    public void setMediaStreams(List<MediaStream> mediaStreams) {
        this.mediaStreams = mediaStreams;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    public String toString() {
        return channelName;
    }
}
