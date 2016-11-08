package icap.vv.de.subtitlepresenter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import vv.de.subtitlepresenter.R;

/**
 * Created by Marko Nalis on 26.10.2015.
 */
public class InfinotedProjectArrayAdapter extends ArrayAdapter<InfinotedProject>{

    private final Context context;
    private List<InfinotedProject> values = new ArrayList<>();

    public InfinotedProjectArrayAdapter(Context context, List<InfinotedProject> resource) {
        super(context, -1, resource);
        this.context = context;
        this.values = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.infinoted_project_layout,parent,false);
        InfinotedProject proj = values.get(position);

        TextView host = (TextView)rowView.findViewById(R.id.host);
        TextView document = (TextView) rowView.findViewById(R.id.document);

        host.setText(proj.getUrl());
        document.setText(proj.getDocPath());

        return rowView;
    }

    public InfinotedProject getProject(int position){
        return values.get(position);
    }
}
