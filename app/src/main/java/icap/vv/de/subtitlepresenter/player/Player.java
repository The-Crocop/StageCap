package icap.vv.de.subtitlepresenter.player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marko Nalis on 30.10.2015.
 */
public abstract class Player {
    private List<StateChangeListener> listeners = new ArrayList<>();


    private PlayerState state = PlayerState.STOPPED;

    public void play(){
        state = PlayerState.PLAYING;
        notifyListeners();
    }

    public void pause(){
        state = PlayerState.PAUSED;
        notifyListeners();
    }
    public void stop(){
        state = PlayerState.STOPPED;
        notifyListeners();
    }
    public void error(){
        state = PlayerState.ERROR;
        notifyListeners();
    }
    public PlayerState getState() {
        return state;
    }

    private void notifyListeners(){
        for(StateChangeListener listener:listeners)listener.onStateChange(state);
    }
    public boolean addStateChangeListener(StateChangeListener object) {
        return listeners.add(object);
    }

    public void clear() {
        listeners.clear();
    }

    public boolean removeStateChangeListener(StateChangeListener object) {
        return listeners.remove(object);
    }
}
