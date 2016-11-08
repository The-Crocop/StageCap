package icap.vv.de.subtitlepresenter.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import de.lits.adopted.exception.RequestException;
import de.verbavoice.parser.Subtitle;
import icap.vv.de.subtitlepresenter.MyApp;
import icap.vv.de.subtitlepresenter.adapters.SrtArrayAdapter;
import icap.vv.de.subtitlepresenter.player.PlayerState;
import icap.vv.de.subtitlepresenter.player.SubtitleProjectPlayer;
import icap.vv.de.subtitlepresenter.player.SubtitleProjectPlayerListener;
import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;
import vv.de.subtitlepresenter.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Marko on 11.08.2015.
 */
public class SrtListFragment extends ListFragment implements View.OnClickListener,SeekBar.OnSeekBarChangeListener,AbsListView.OnScrollListener,SubtitleProjectPlayerListener {
    private final static String TAG = "SrtListFragment";
    private static int pointer;
    private static SubtitleProjectPlayer player;
    long end;
    int percent;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private View rootView;
    private Bus bus;
    private SrtArrayAdapter adapter;

   // private SubtitleProjectPlayer player;
   private SubtitleProject project;
    private List<Subtitle> subtitles;
    /*
    This stuff shoud be moved to the tab host because it is used for all languages
     */
    private SeekBar seekBar;
    private ImageButton playBtn;
    private ImageButton loopBtn;
    private FloatingActionButton holdButton;
    private TextView seekerPosition;
    private TextView endTime;
    private ListView listView;
    private SimpleDateFormat sdf =  new SimpleDateFormat("HH:mm:ss");
    private InfinotedProject infinotedProject;
    private View.OnTouchListener holdButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && player.isPlaying())
                player.waitAt(infinotedProject.getNextEndTime());
            else if (event.getAction() == MotionEvent.ACTION_UP) {
                player.releaseWait(infinotedProject.getNextStartTime());
            }

            return true;
        }
    };
    private boolean touching;
    private boolean listTouch;
    private PlayerState lastPlayerState = PlayerState.STOPPED;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.srt_fragment_layout,container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewGroup group = (ViewGroup)view.findViewById(R.id.srt_fragment_root);
        setAllButtonListener(group);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHidden changed " + hidden);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

      //  queue = Volley.newRequestQueue(getActivity());
      //  String id =getArguments().getString("id");//+ getArguments().getString("id");
        project = (SubtitleProject) getArguments().getSerializable("project");
        player = SubtitleProjectPlayer.getInstance();
      //TODO  player.init(project);
           // playIntent.putExtra("SubtitleProject", (SubtitleProject) project);


        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        listView = getListView();
        listView.setOnScrollListener(this);


        seekBar = (SeekBar) getActivity().findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        playBtn = (ImageButton)getActivity().findViewById(R.id.playbutton);
        loopBtn = (ImageButton)getActivity().findViewById(R.id.loopbutton);

        holdButton = (FloatingActionButton)getActivity().findViewById(R.id.floating_hold);
        holdButton.setOnTouchListener(holdButtonListener);
        seekerPosition = (TextView)getActivity().findViewById(R.id.current_time_text);
        endTime = (TextView)getActivity().findViewById(R.id.end_time_txt);


        infinotedProject = (InfinotedProject)getArguments().getSerializable("infProject");
        subtitles = infinotedProject.getSubtitles();
        pointer = infinotedProject.getSubForTimestamp(project.getSeeker());
        end = subtitles.get(subtitles.size()-1).endTimeMillis();

        endTime.setText(sdf.format(new Date(end)));

        infinotedProject.setSubtitles(subtitles);


        Log.d("UPD", "update Position");

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        adapter = new SrtArrayAdapter(getActivity(), subtitles);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus = MyApp.bus;
        bus.register(this);
        player.add(this);
        if(project.getSeeker() != 0)pointer = infinotedProject.getNearestSubForTimestamp(project.getSeeker());
        updatePosition(pointer);
        if(player.isPlaying())holdButton.show();
        else holdButton.hide();
        updateConsole();
    }

    @Override
    public void onPause() {
        super.onPause();
        player.remove(this);
        bus.unregister(this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        player.block();
        boolean wasPlaying = player.isPlaying();
        if(wasPlaying)player.pause();

        pointer = position;
        try {
            long timestamp = subtitles.get(position).startTimeMillis();
            player.jump(timestamp);
            seekerPosition.setText(sdf.format(new Date(timestamp)));
            project.updatePosition(timestamp);
            updateSeekBar(timestamp);
            updatePosition(pointer, true);
        } catch (RequestException e) {
            e.printStackTrace();
        }
        if(wasPlaying)player.play();
        player.releaseBlock();

    }

    private void prev() throws RequestException {
        if(pointer>0){
            pointer--;
            long timestamp = infinotedProject.getPrevStartTime();
            seekerPosition.setText(sdf.format(new Date(timestamp)));
           // project.undo();//TODO more ellegant
            project.updatePosition(timestamp);
            player.jump(timestamp);
            updatePosition(pointer);
        }

    }

    private void next() throws RequestException {

        if(pointer< subtitles.size()-1){
            pointer++;
            long timestamp = infinotedProject.getNextStartTime();
            seekerPosition.setText(sdf.format(new Date(timestamp)));
            project.updatePosition(timestamp);
            player.jump(timestamp);
            updatePosition(pointer);
        }

        //project.next();

    }

    private void clear() throws RequestException {
        project.clear();
    }

    private void updatePosition(int pos, boolean click){
        Log.d(TAG,"Pointer: "+pointer);

        if(listView.getSelectedItemPosition()!= pos &&(click || !listTouch) && pos>-1 && pos <adapter.getCount()){
            pointer = pos;
            try {
                //  focusedView = getActivity().getCurrentFocus();
                //  listView.clearFocus();
                listView.requestFocusFromTouch();
                listView.setItemChecked(pos, true);
                listView.setSelection(pos);
                //  if(focusedView != null)focusedView.requestFocus();
                // listView.requestFocus();
                Log.d("POSITION", "p:" + pos);
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
    }

    private void updatePosition(int pos) {
        updatePosition(pos, false);
        //  getListView().setSelectionFromTop(pos,getListView().getHeight()/2);
    }

    private void updateSeekBar(long timemillis) {
        percent = (int) (1000 * timemillis / end);
        seekBar.setProgress(percent);
        seekerPosition.setText(sdf.format(new Date(timemillis)));
    }

    private void updateConsole() {
        switch (player.getState()) {
            case PLAYING:
                playBtn.setImageResource(R.drawable.ic_pause_black_24dp);
                break;
            case STOPPED:
                playBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                break;
            case PAUSED:
                playBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                break;
            default:
                playBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        loopBtn.setImageResource(player.isLoop() ? R.drawable.ic_repeat_black_24dp_pressed : R.drawable.ic_repeat_black_24dp);
        updateSeekBar(project.getSeeker());

    }

    public void setAllButtonListener(ViewGroup viewGroup) {

        View v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                setAllButtonListener((ViewGroup) v);
            } else if (v instanceof ImageButton) {
                ((ImageButton) v).setOnClickListener(this);
            } else if (v instanceof Button) {
                ((Button) v).setOnClickListener(this);
            } else if (v instanceof FloatingActionButton) {
                ((FloatingActionButton) v).setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.prevbutton:
                try {
                    prev();
                } catch (RequestException e) {
                   Toast.makeText(getContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nextbutton:
            case R.id.floating_next:
                try {
                    next();
                } catch (RequestException e) {
                    Toast.makeText(getContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                } catch (IndexOutOfBoundsException e) {
                    Log.d(TAG, "No next subtitle!");

                }
                break;
            case R.id.clearbutton:
                try {
                    clear();
                } catch (RequestException e) {
                    Toast.makeText(getContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.playbutton:
                if(!player.isPlaying()){
                    player.play();
                    holdButton.show();
                    updateConsole();

                }
                else {
                    player.pause();
                    holdButton.hide();
                    updateConsole();
                }
                break;
            case R.id.loopbutton:
                Log.d(TAG,"set loop to: "+!player.isLoop());
                player.setLoop(!player.isLoop());
                updateConsole();
                break;
            case R.id.stopbutton:
                player.setLoop(false);
                player.stop();
                holdButton.hide();
                updateConsole();
                //TODO stop the player and jump to the start
                break;
            default:;
        }
    }

    @Subscribe
    public void onUpdate(long timemillis) {
        if(!touching){
            updatePosition(infinotedProject.getPosition());
            updateSeekBar(timemillis);
        }
    }

    @Override
    public void onPlayerStop() {
        updatePosition(0);
        updateConsole();
    }

    @Override
    public void onPlayerPaused() {
        updateConsole();
    }

    @Override
    public void onPlay() {
        //updateConsole();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                long point = (long)((progress/1000.0)* end);
                seekerPosition.setText(sdf.format(new Date(point)));
                int myPos = infinotedProject.getNearestSubForTimestamp(point);
                updatePosition(myPos);

            }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        touching = true;
        lastPlayerState = player.getState();
        player.pause();

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
            touching = false;

            long point = (long)((seekBar.getProgress()/1000.0)* end);
            try {
                project.updatePosition(point);
                updatePosition(infinotedProject.getNearestSubForTimestamp(point));

            } catch (RequestException e) {
                e.printStackTrace();
            }
            player.jump(point);

            if(lastPlayerState == PlayerState.PLAYING)player.play();

    }





    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState != SCROLL_STATE_IDLE){
            listTouch = true;
        }
        else {
            new Handler().postDelayed(new Runnable(){

                @Override
                public void run() {
                    listTouch = false;
                }
            },1500);
        }


    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


}