package icap.vv.de.subtitlepresenter.player;

import java.util.HashMap;
import java.util.Map;


import de.lits.adopted.exception.RequestException;
import de.lits.infinoted.InfinotedClientException;
import icap.vv.de.subtitlepresenter.infinoted.InfinotedWriter;
import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;

/**
 * Created by Marko on 28.11.2015.
 *
 * Controller takes infomration from the player delegates it and writes it out to the infinoted servers
 */
public class SubtitleProjectController {

    private SubtitleProject project;
    private Map<InfinotedProject,InfinotedWriter> infinotedMap = new HashMap<>();
    private SubtitleProjectPlayer player;

    public SubtitleProjectController() {
        player = SubtitleProjectPlayer.getInstance();
    }

    public void load(SubtitleProject project){
        this.project = project;
        for(InfinotedProject infinotedProject:project.getProjects()){
            infinotedMap.put(infinotedProject,new InfinotedWriter());
        }
    }

    public void connect() throws InfinotedClientException {
        for (Map.Entry<InfinotedProject,InfinotedWriter> entry:infinotedMap.entrySet()){
            InfinotedProject project = entry.getKey();
            InfinotedWriter writer = entry.getValue();
            writer.connect(project.getUrl(), project.getDocPath());
        }
    }
    public void disconnect(){
        for(InfinotedWriter writer: infinotedMap.values())writer.close();
    }

  /*
   TODO
   public void updatePosition(long millis) throws RequestException {
        for(InfinotedProject proj:getProjects())proj.sendText(millis);
        setSeeker(millis);
    }
    public void sendText(long timestamp) throws RequestException {
        int pos = getSubForTimestamp(timestamp);
        if(pos != position){
            sendText(pos);
        }
        if(pos == -1)clear();
    }*/
}
