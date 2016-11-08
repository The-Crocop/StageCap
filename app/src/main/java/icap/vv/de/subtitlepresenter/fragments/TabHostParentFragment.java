package icap.vv.de.subtitlepresenter.fragments;

import android.app.ProgressDialog;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.lits.infinoted.InfinotedClientException;
import icap.vv.de.subtitlepresenter.infinoted.VltIntent;
import icap.vv.de.subtitlepresenter.player.SubtitlePlayerService;
import icap.vv.de.subtitlepresenter.player.SubtitleProjectPlayer;
import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProjectListener;
import vv.de.subtitlepresenter.R;

/**
 * Created by Marko Nalis on 16.09.2015.
 */
public class TabHostParentFragment extends Fragment {

    private final static String TAG = "TabHostParentFragment";

    private FragmentTabHost mTabHost;
    private SubtitleProject project;
    private SubtitleProjectPlayer player;


    /*
    Player Service variables
     */
    private SubtitlePlayerService playerService;
    private Intent playIntent;
    private boolean playerBound;

    private ProgressDialog pdialog;
    private DisconnectedDialog dialog;
    private ServiceConnection playerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SubtitlePlayerService.SubtitlePlayerBinder binder = (SubtitlePlayerService.SubtitlePlayerBinder) service;
            playerService = binder.getService();
            playerService.dismissNotification();
            playerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playerBound = false;
        }
    };
    private SubtitleProjectListener subtitleProjectListener = new SubtitleProjectListener() {
        @Override
        public void onAllConnected() {
            pdialog.dismiss();
        }

        @Override
        public void onConnectionError() {
            pdialog.dismiss();
            Toast.makeText(getContext(), "Connection error", Toast.LENGTH_SHORT).show();
            showDisconnectDialog();
        }
    };
    private BroadcastReceiver vltReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(), "An error occured try to reconnect!", Toast.LENGTH_SHORT).show();
            showDisconnectDialog();

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabHost = new FragmentTabHost(getActivity());

        player = SubtitleProjectPlayer.getInstance();

        mTabHost.setup(getActivity(), getChildFragmentManager(), R.layout.tab_host_fragment);

        project = (SubtitleProject) getArguments().getSerializable("project");

        for (InfinotedProject inf : project.getProjects()) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("project", project);
            bundle.putSerializable("infProject", inf);
            bundle.putString("id", inf.getId().toString());
            bundle.putString("projectTitle", project.getTitle());
            mTabHost.addTab(mTabHost.newTabSpec(inf.getId().toString()).setIndicator(inf.getDocPath()),
                    SrtListFragment.class, bundle);
        }
        player.init(project);

        return mTabHost;
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(vltReceiver,
                new IntentFilter(VltIntent.SESSION_ERROR));
        createProgressDialog();
        if (!player.isPlaying()) connect();
        if (playIntent == null) {
            playIntent = new Intent(getActivity(), SubtitlePlayerService.class);
            player = SubtitleProjectPlayer.getInstance();
            player.init(project);
            // playIntent.putExtra("SubtitleProject", (SubtitleProject) project);
            getActivity().bindService(playIntent, playerConnection, Context.BIND_AUTO_CREATE);
        }
        if (playerService != null) playerService.dismissNotification();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(vltReceiver);

        project.setListener(null);
        if (pdialog != null && pdialog.isShowing()) pdialog.dismiss();

        if (!player.isPlaying()) {
            project.disconnect();
            if (playerBound) getActivity().unbindService(playerConnection);
            playerBound = false;
            playIntent = null;
            //playerService.stopService(playIntent);
            //playerService = null;
            Log.d(TAG, "stop service");
            //getActivity().getSupportFragmentManager().popBackStack();
        } else playerService.addNotification();
        project.save();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

    private void showDisconnectDialog(){
        player.pause();
        if (dialog != null && dialog.getShowsDialog()) dialog.dismiss();
        dialog = new DisconnectedDialog();
        dialog.setClickListener(new DisconnectedDialog.ClickListener() {
            @Override
            public void onReconnectClick() {
                connect();
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(),"alert");
    }

    private void createProgressDialog() {
        pdialog = new ProgressDialog(getContext());
        pdialog.setCancelable(false);
        pdialog.setMessage("Connecting ....");
    }

    private void connect(){
        try {
            pdialog.show();
            project.setListener(subtitleProjectListener);
            project.connect();
        } catch (InfinotedClientException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Connection Failed press back and try again", Toast.LENGTH_SHORT).show();
        }
    }

}
