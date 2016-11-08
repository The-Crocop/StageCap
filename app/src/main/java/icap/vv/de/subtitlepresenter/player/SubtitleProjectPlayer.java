package icap.vv.de.subtitlepresenter.player;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import de.lits.adopted.exception.RequestException;
import de.verbavoice.parser.Subtitle;
import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;

/**
 * Created by Marko Nalis on 29.10.2015.
 */
public  class SubtitleProjectPlayer extends Player{

    private static SubtitleProjectPlayer player;

    private Long waitingFlag;

    public static SubtitleProjectPlayer getInstance(){
        if(player == null){
            player = new SubtitleProjectPlayer();
        }
        return player;
    }

    private static final String TAG = "SubtitleProjectPlayer!";


    private SubtitleProject project;
    private Handler handler;
    private List<InfinotedProject> infinotedProjects;

    private List<SubtitleProjectPlayerListener> listeners = new ArrayList<>();

    private long seeker = 0;
    private long startTime = 0;
    private long endTime = 0;
    private int currentPosition= 0;

    private boolean loop;

    private static boolean block = false;


    public void init(SubtitleProject project){
        handler = new Handler();
        this.project = project;

        this.seeker = project.getSeeker();
        this.infinotedProjects = project.getProjects();
        this.endTime = getEndTime();
    }
    public void updateInfinoted(long time) throws RequestException {
        project.updatePosition(time);
     /* for(InfinotedProject proj:infinotedProjects){
         proj.sendText(time);
      }*/
    };

    public boolean isPlaying() {
        return getState()==PlayerState.PLAYING;
    }




    @Override
    public void play(){
        super.play();
        startTime = System.currentTimeMillis()-seeker;
        handler.post(runnable);
        notifyListenersOnPlay();
    }
    @Override
    public void pause(){
        super.pause();
        handler.removeCallbacksAndMessages(null);
        notifyListenersOnPause();
    }
    public void jump(long timestamp){
        startTime = System.currentTimeMillis()-timestamp;
        seeker = timestamp;
    }

    @Override
    public void stop(){
        super.stop();
        seeker = 0;
        try {
            project.updatePosition(0L);
        } catch (RequestException e) {
            e.printStackTrace();
        }
        notifyListeners(seeker);
        notifyListenersOnStop();
        handler.removeCallbacksAndMessages(null);
    }

    public List<String> getCurrentSubs(){
        List<String> retVal = new ArrayList<>();
        for(InfinotedProject infProj:project.getProjects()){
            String sub = infProj.getTextForTimestamp(project.getSeeker());
            retVal.add(sub);
        }
        return retVal;
    }
    public boolean add(SubtitleProjectPlayerListener object) {
        return listeners.add(object);
    }

    public void clear() {
        listeners.clear();
    }

    public boolean remove(Object object) {
        return listeners.remove(object);
    }

    private long getEndTime(){
        List<Subtitle> subs = infinotedProjects.get(0).getSubtitles();
        long retVal = subs.get(subs.size()-1).endTimeMillis() ;
        for(int i = 1;i<infinotedProjects.size();i++){

            subs = infinotedProjects.get(i).getSubtitles();
            long subEnd = subs.get(subs.size()-1).endTimeMillis();
            if(subEnd > retVal)retVal = subEnd;
        }
        return retVal;
    }
    private Runnable runnable = new Runnable() {
       @Override
       public void run() {
           seeker = System.currentTimeMillis()-startTime;
           if(waitingFlag != null && seeker >= waitingFlag)pause();
           else if(seeker < endTime){
               try {
                  if(!block)updateInfinoted(seeker);
                   handler.postDelayed(runnable, 200);
                   if(!block)notifyListeners(seeker);
               } catch (RequestException e) {
                   e.printStackTrace();
               }
           }else if(loop){
               Log.d(TAG,"is in loop!");
               seeker = 0;
               player.play();
           }
           else stop();

       }
   };

    public void waitAt(long millis){
        waitingFlag = millis;
    }
    public void releaseWait(long nextTimeStamp){
       if(waitingFlag != null){
           waitingFlag = null;
           jump(nextTimeStamp);
           play();
       }

    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void block() {
        this.block = true;
    }
    public void releaseBlock(){
        this.block = false;
    }

    private void notifyListeners(long time){
        for(SubtitleProjectPlayerListener listener:listeners)listener.onUpdate(time);
    }
    private void notifyListenersOnStop(){
        for(SubtitleProjectPlayerListener listener:listeners)listener.onPlayerStop();
    }
    private void notifyListenersOnPause(){
        for(SubtitleProjectPlayerListener listener:listeners)listener.onPlayerPaused();
    }
    private void notifyListenersOnPlay(){
        for(SubtitleProjectPlayerListener listener:listeners)listener.onPlay();
    }
}
