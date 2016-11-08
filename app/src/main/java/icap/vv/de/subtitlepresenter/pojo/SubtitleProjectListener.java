package icap.vv.de.subtitlepresenter.pojo;

import java.io.Serializable;

/**
 * Created by crocop on 04.01.16.
 */
public interface SubtitleProjectListener extends Serializable {
    void onAllConnected();

    void onConnectionError();
}
