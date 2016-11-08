package icap.vv.de.subtitlepresenter.player;

/**
 * Created by Marko Nalis on 29.10.2015.
 */
public interface SubtitleProjectPlayerListener {
    void onUpdate(long timemillis);
    void onPlayerStop();
    void onPlayerPaused();
    void onPlay();

}
