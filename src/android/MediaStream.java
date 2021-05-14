package com.lucasnpinheiro.mediastreaming;

public class MediaStream {
    private String uri;
    private String identifier;
    private String title;
    private String description;
    private String cover;

    public MediaStream(
            String uri, String identifier, String title, String description, String cover) {
        this.uri = uri;
        this.identifier = identifier;
        this.title = title;
        this.description = description;
        this.cover = cover;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return title;
    }
}
