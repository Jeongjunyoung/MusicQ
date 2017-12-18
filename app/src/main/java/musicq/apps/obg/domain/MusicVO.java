package musicq.apps.obg.domain;

import java.io.Serializable;

/**
 * Created by d1jun on 2017-12-14.
 */

public class MusicVO implements Serializable {
    private String id;
    private String albumId;
    private String title;
    private String artist;
    public MusicVO(){}
    public MusicVO(String id, String albumId, String title, String artist) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
