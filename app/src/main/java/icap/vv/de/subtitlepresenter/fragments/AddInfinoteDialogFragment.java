package icap.vv.de.subtitlepresenter.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import vv.de.subtitlepresenter.R;

/**
 * Created by Marko Nalis on 26.10.2015.
 */
public class AddInfinoteDialogFragment extends AppCompatDialogFragment{

    AddInfinoteDialogListener addInfinoteDialogListener;

    private TextView host;
    private TextView document;


    public interface AddInfinoteDialogListener{
        void onDialogPositiveClick(InfinotedProject project, DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    public AddInfinoteDialogListener getAddInfinoteDialogListener() {
        return addInfinoteDialogListener;
    }

    public void setAddInfinoteDialogListener(AddInfinoteDialogListener addInfinoteDialogListener) {
        this.addInfinoteDialogListener = addInfinoteDialogListener;
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.infinote_doc,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        host = (TextView) view.findViewById(R.id.server);
        document = (TextView) view.findViewById(R.id.document);
        if(getArguments() != null){
            InfinotedProject proj = (InfinotedProject) getArguments().getSerializable("infinotedProject");
            host.setText(proj.getUrl());
            document.setText(proj.getDocPath());
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

     return    super.onCreateDialog(savedInstanceState);





    }
}
