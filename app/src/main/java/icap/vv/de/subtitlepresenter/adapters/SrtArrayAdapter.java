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

import de.verbavoice.parser.Subtitle;
import vv.de.subtitlepresenter.R;

/**
 * Created by Marko on 11.08.2015.
 */
public class SrtArrayAdapter extends ArrayAdapter<Subtitle> {

    private final Context context;
    private List<Subtitle> values = new ArrayList<>();
    private  SimpleDateFormat SDF;


    public SrtArrayAdapter(Context context, List<Subtitle> resource) {
        super(context,-1, resource);
        this.context = context;
        this.values = resource;
        this.SDF = new SimpleDateFormat("HH:mm:ss,SSS");
        SDF.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.subtitle, parent, false);

        TextView startTimeView = (TextView) rowView.findViewById(R.id.startTimeView);
        TextView idView = (TextView) rowView.findViewById(R.id.number);

        TextView endTimeView = (TextView)  rowView.findViewById(R.id.endTimeView);
        TextView subtitleView = (TextView) rowView.findViewById(R.id.subtitleView);

        Subtitle entry = values.get(position);
        startTimeView.setText(SDF.format(new Date(entry.startTimeMillis())));//
        idView.setText(entry.index()+"");

        endTimeView.setText(SDF.format(new Date(entry.endTimeMillis())));//
        subtitleView.setText(entry.text());

        // change the icon for Windows and iPhone



        return rowView;
    }

}
