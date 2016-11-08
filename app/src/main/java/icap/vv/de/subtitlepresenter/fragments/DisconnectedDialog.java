package icap.vv.de.subtitlepresenter.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import vv.de.subtitlepresenter.R;

/**
 * Created by crocop on 15.12.15.
 */
public class DisconnectedDialog extends DialogFragment {

    public interface ClickListener{
        void onReconnectClick();
    }
    private ClickListener clickListener;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Disconnected from Server. Try to reconnect?")
                .setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(clickListener != null)clickListener.onReconnectClick();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
