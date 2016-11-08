package icap.vv.de.subtitlepresenter.fragments;

import android.content.DialogInterface;
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
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.verbavoice.parser.SRTParser;
import de.verbavoice.parser.Subtitle;
import icap.vv.de.subtitlepresenter.adapters.SubtitleProjectArrayAdapter;
import icap.vv.de.subtitlepresenter.dao.SubtitleProjectDao;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;
import vv.de.subtitlepresenter.R;

/**
 * Created by Marko Nalis on 09.09.2015.
 */
public class MenuFragment extends ListFragment implements View.OnClickListener{

    private SubtitleProjectArrayAdapter adapter;
    private List<SubtitleProject> projects = new ArrayList<>();
    private SubtitleProjectDao subtitleProjectDao;

    private final String TAG = "MenuFragment";



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.footer).setOnClickListener(this);
        subtitleProjectDao = new SubtitleProjectDao(getContext());
        registerForContextMenu(getListView());
    }

    @Override
    public void onPause() {
        super.onPause();
        projects = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            loadProjects();
        } catch (IOException e) {
            Toast.makeText(getContext(),"Couldnt load files!",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.menu_list,container,false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_subtitleproject,menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final SubtitleProject subProject = projects.get(info.position);
        switch (item.getItemId()){
            case R.id.delete:
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Delete Project");
                alertDialog.setMessage("Really delete " + subProject.getTitle() + " ?");
                alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        subProject.delete();
                        projects.remove(subProject);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "deleted "+subProject.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
                return true;
            case R.id.edit:
                showProjectDetails(subProject);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        SubtitleProject project =  adapter.getProject(position);

        if(project.getProjects().size()>0){
            TabHostParentFragment frag = new TabHostParentFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("project", adapter.getProject(position));
            frag.setArguments(bundle);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, frag);
            transaction.addToBackStack("openSub");
            transaction.commit();
        }else showProjectDetails(project);

        //Toast.makeText(getActivity(),"we are here",Toast.LENGTH_SHORT).show();
    }

    private void loadProjects() throws IOException {

       // boolean firstUse = subtitleProjectDao.isFirstUse();
       // subtitleProjectDao.setFirstUse(true);
            projects = subtitleProjectDao.getAll();
          /*  for(SubtitleProject proj:projects){
                for(InfinotedProject inf: proj.getProjects()){
                    inf.setSubtitles(loadSubs(inf.getFilePath()));
                }
            }*/


        adapter = new SubtitleProjectArrayAdapter(getActivity(), projects);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private List<Subtitle> loadSubs(String filePath) throws IOException {
        SRTParser parser = new SRTParser();
        InputStream in;
        in = getActivity().getAssets().open(filePath);
        return parser.parse(in).subtitles();
    }


    private void createNewDialog(){
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        final EditText edittext = new EditText(getContext());

        alert.setTitle("Project Name");

        alert.setView(edittext);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                //OR
                String projectName = edittext.getText().toString();
                SubtitleProject project = new SubtitleProject(projectName);
                projects.add(project);
                subtitleProjectDao.save(project);
                adapter.notifyDataSetChanged();
                dialog.dismiss();

                showProjectDetails(project);
            }
        });

        alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alert.show();
    }

    private void showProjectDetails(SubtitleProject project){
        ProjectFragment frag = new ProjectFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("project", project);
        frag.setArguments(bundle);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, frag);
        transaction.addToBackStack("addproj");
        transaction.commit();
    }
    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.footer){
            createNewDialog();
        }


    }



   /* <Button android:id="@+id/footer" android:layout_alignParentBottom="true"
    android:layout_width="fill_parent"
    android:layout_height="wrap_content"
    android:text="Add"/>*/
}
