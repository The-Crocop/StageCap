package icap.vv.de.subtitlepresenter.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.verbavoice.parser.Subtitle;
import icap.vv.de.subtitlepresenter.adapters.InfinotedProjectArrayAdapter;

import icap.vv.de.subtitlepresenter.dao.SubtitleProjectDao;
import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;
import vv.de.subtitlepresenter.R;

/**
 * Created by Marko Nalis on 26.10.2015.
 */
public class ProjectFragment extends ListFragment implements View.OnClickListener{

    private final static String TAG = "ProjectFragment";



    private InfinotedProjectArrayAdapter adapter;
    List<InfinotedProject> projects = new ArrayList<>();
    private SubtitleProject project;



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.footer).setOnClickListener(this);
        registerForContextMenu(getListView());

        project = (SubtitleProject) getArguments().getSerializable("project");
        projects = project.getProjects();
        adapter = new InfinotedProjectArrayAdapter(getActivity(),projects);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_list,container,false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_subtitleproject, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        final InfinotedProject infinotedProject = projects.get(position);
        switch (item.getItemId()){
            case R.id.delete:
              //  project.remove(project.getProjects().get(position));
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Delete Project");
                alertDialog.setMessage("Really delete this entry?");
                alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        infinotedProject.delete();
                        projects.remove(infinotedProject);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(),"deleted entry",Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
                return true;
            case R.id.edit:
                editInfinotedProject(infinotedProject);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        InfinotedProject project = adapter.getProject(position);
        editInfinotedProject(project);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.footer){
            createInfinotedProject();
        }
    }

    public void createInfinotedProject(){
        editInfinotedProject(null);
    };
    public void editInfinotedProject(InfinotedProject infinotedProject){
        AddProjectFragment frag = new AddProjectFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("subtitleProject", project);
        if(infinotedProject != null)bundle.putSerializable("infinotedProject",infinotedProject);
        frag.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, frag);
        transaction.addToBackStack("editInfi");
        transaction.commit();
    }

}
