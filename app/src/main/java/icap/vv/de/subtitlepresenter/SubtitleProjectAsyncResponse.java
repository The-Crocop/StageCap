package icap.vv.de.subtitlepresenter;

import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;

/**
 * Created by Marko Nalis on 14.09.2015.
 */
public interface SubtitleProjectAsyncResponse {
    void processResult(SubtitleProject[] projects);
}
