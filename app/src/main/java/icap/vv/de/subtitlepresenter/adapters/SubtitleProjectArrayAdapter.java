package icap.vv.de.subtitlepresenter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;
import vv.de.subtitlepresenter.R;

/**
 * Created by Marko Nalis on 14.09.2015.
 */
public class SubtitleProjectArrayAdapter extends ArrayAdapter<SubtitleProject>{

    private final Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss,SSS");
    private List<SubtitleProject> values = new ArrayList<>();

    public SubtitleProjectArrayAdapter(Context context, List<SubtitleProject> resource) {
        super(context,-1, resource);
        this.context = context;
        this.values = resource;
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    public void setValues(List<SubtitleProject> values) {
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.subtitle_project_layout, parent, false);

        SubtitleProject proj = values.get(position);

        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView projects = (TextView) rowView.findViewById(R.id.count);
        TextView time = (TextView) rowView.findViewById(R.id.time);

        title.setText(proj.getTitle());
        projects.setText(proj.getProjects().size()+"");
        time.setText(sdf.format(new Date(proj.getSeeker())));

        return rowView;

    }

    public SubtitleProject getProject(int position){
        return values.get(position);
    }

}
