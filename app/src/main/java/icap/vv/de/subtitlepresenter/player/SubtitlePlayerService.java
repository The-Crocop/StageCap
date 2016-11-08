package icap.vv.de.subtitlepresenter.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.squareup.otto.Bus;

import icap.vv.de.subtitlepresenter.MainActivity;
import icap.vv.de.subtitlepresenter.MyApp;
import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;
import vv.de.subtitlepresenter.R;

/**
 * Created by Marko on 16.11.2015.
 */
public class SubtitlePlayerService extends Service implements SubtitleProjectPlayerListener{

    private final static int NOTIFICATION_ID = 123;
    private final static String TAG = "SubtitlePlayerService";
    private static SubtitleProjectPlayer player;

    private final IBinder playerBind = new SubtitlePlayerBinder();
    private PowerManager.WakeLock wakeLock;

    private PlayActionReceiver playActionReceiver;
    private NotificationManager mNotificationManager;


    public class SubtitlePlayerBinder extends Binder {
        public SubtitlePlayerService getService(){
            return SubtitlePlayerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager mgr = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");

        player = SubtitleProjectPlayer.getInstance();
        player.add(this);
        playActionReceiver = new PlayActionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayActionReceiver.PLAY);
        filter.addAction(PlayActionReceiver.PAUSE);
        filter.addAction(PlayActionReceiver.NEXT);
        registerReceiver(playActionReceiver,filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();;
        player.remove(this);
        unregisterReceiver(playActionReceiver);
    }


    public PlayerState getState() {
        return player.getState();
    }

    public void dismissNotification(){
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    public void addNotification() {
        boolean running = player.isPlaying();
        // create the notification

        Intent pauseIntent = new Intent();
        pauseIntent.setAction(PlayActionReceiver.PAUSE);
        PendingIntent pendingPauseIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), pauseIntent, 0);

        Intent playIntent = new Intent();
        pauseIntent.setAction(PlayActionReceiver.PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), playIntent, 0);

        Notification.InboxStyle myStyle = new Notification.InboxStyle();

        for(String s:player.getCurrentSubs()){
            myStyle.addLine(s);
        }

        Notification.Builder m_notificationBuilder = new Notification.Builder(this)
                .setContentTitle("StageCap")
                .setContentText("SubtitlePlayer")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(myStyle)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(player.isPlaying())
                .addAction(R.drawable.ic_pause_black_24dp, "Pause", pendingPauseIntent)
                .addAction(R.drawable.ic_fast_forward_white_24dp,"Next",pendingPlayIntent)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 10,
                        new Intent(getApplicationContext(), MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                        0));


        // send the notification


        mNotificationManager.notify(NOTIFICATION_ID, m_notificationBuilder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        dismissNotification();
        return playerBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
       // project.disconnect();
        return false;
    }

    @Override
    public void onUpdate(long timemillis) {

    }

    @Override
    public void onPlayerStop() {

        if(wakeLock.isHeld())wakeLock.release();
    }


    @Override
    public void onPlayerPaused() {
        if(wakeLock.isHeld())wakeLock.release();
    }

    @Override
    public void onPlay() {
        wakeLock.acquire();
    }

    class PlayActionReceiver extends BroadcastReceiver {


        public static  final String PAUSE = "icap.vv.de.subtitlepresenter.PAUSE";
        public static final String PLAY = "icap.vv.de.subtitlepresenter.PLAY";
        public static final String NEXT = "icap.vv.de.subtitlepresenter.NEXT";


        @Override
        public void onReceive(Context context, Intent intent) {

            if(PAUSE.equals(intent.getAction()))player.pause();
            else if (PLAY.equals(intent.getAction()))player.play();

            addNotification();
        }
    }
}
