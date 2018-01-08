package musicq.apps.obg.domain;

/**
 * Created by d1jun on 2018-01-05.
 */

public class Top100VO {
    private int rank;
    private String title;
    private String videoId;
    public Top100VO(int rank, String title, String videoId) {
        this.rank = rank;
        this.title = title;
        this.videoId = videoId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Top100VO() {
        super();
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
