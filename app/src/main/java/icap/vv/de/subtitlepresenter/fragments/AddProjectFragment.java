package icap.vv.de.subtitlepresenter.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.verbavoice.parser.Subtitle;
import icap.vv.de.subtitlepresenter.dao.SubtitleProjectDao;
import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;
import vv.de.subtitlepresenter.R;

/**
 * Created by Marko Nalis on 16.09.2015.
 */
public class AddProjectFragment extends Fragment implements View.OnClickListener {


    private final static String TAG = "AddProjectFragment";
    private final static int REQUEST_SRT = 555;


    private EditText host;
    private EditText document;
    private TextView filePicker;
    private  List<Subtitle> subs;

    private SubtitleProject subtitleProject;
    private InfinotedProject infinotedProject;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.infinote_doc, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subtitleProject = (SubtitleProject)getArguments().getSerializable("subtitleProject");
        infinotedProject = (InfinotedProject)getArguments().getSerializable("infinotedProject");


        ((Button) getActivity().findViewById(R.id.save_btn)).setOnClickListener(this);
        ((Button) getActivity().findViewById(R.id.cancel_btn)).setOnClickListener(this);
        ((Button) getActivity().findViewById(R.id.pick_srt)).setOnClickListener(this);

        host = (EditText) getActivity().findViewById(R.id.server);
        document = (EditText) getActivity().findViewById(R.id.document);
        filePicker = (TextView) getActivity().findViewById(R.id.pick_srt_txtview);

        if(infinotedProject != null){
            host.setText(infinotedProject.getUrl());
            document.setText(infinotedProject.getDocPath());
            subs = infinotedProject.getSubtitles();
            filePicker.setText(infinotedProject.getSubtitles().size()+ " subtitle entries");
        }else{
            infinotedProject = new InfinotedProject();
        }


    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,REQUEST_SRT);
    }

    private void save(){
        String hostTxt = host.getText().toString();
        String documentTxt = document.getText().toString();

        if(!hostTxt.isEmpty() && !documentTxt.isEmpty() && subs != null){
            infinotedProject.setUrl(hostTxt.trim());
            infinotedProject.setDocPath(documentTxt.trim());
            infinotedProject.setSubtitles(subs);

            infinotedProject.setSubtitleProject(subtitleProject);
            infinotedProject.save();
            Toast.makeText(getContext(),"saved", Toast.LENGTH_SHORT).show();
            cancel();
        }
        else Toast.makeText(getContext(),"Please fill out all fields", Toast.LENGTH_SHORT).show();



    }
    private void cancel(){
        getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == REQUEST_SRT && resultCode == Activity.RESULT_OK  && resultData != null) {
                Uri uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
            try {
                this.subs = SubtitleProjectDao.loadSubs(getActivity().getContentResolver().openInputStream(uri));
                filePicker.setText(new File(uri.getPath()).getName());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(),"Error loading subtitles",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick (View v){
        switch (v.getId()){
            case R.id.pick_srt:
                performFileSearch();
                break;
            case R.id.save_btn:
                save();
                break;
            case R.id.cancel_btn:
                cancel();
                break;
            default:
                //do nothing
                break;
        }


    }
}




