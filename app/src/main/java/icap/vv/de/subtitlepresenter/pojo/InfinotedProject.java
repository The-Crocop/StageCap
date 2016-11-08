package icap.vv.de.subtitlepresenter.pojo;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import de.lits.adopted.exception.RequestException;
import de.lits.infinoted.InfinotedClientException;
import de.verbavoice.parser.Subtitle;
import icap.vv.de.subtitlepresenter.MyApp;
import icap.vv.de.subtitlepresenter.infinoted.InfinotedWriter;
import icap.vv.de.subtitlepresenter.infinoted.VltConnectionListener;
import icap.vv.de.subtitlepresenter.infinoted.VltIntent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Table(name = "InfinotedProjects")
public class InfinotedProject  extends Model implements Serializable, Parcelable {

    public static final Parcelable.Creator<InfinotedProject> CREATOR = new Parcelable.Creator<InfinotedProject>() {
        public InfinotedProject createFromParcel(Parcel source) {
            return new InfinotedProject(source);
        }

        public InfinotedProject[] newArray(int size) {
            return new InfinotedProject[size];
        }
    };
    private final static String TAG = "InfinotedProject";
    @Column(name = "Url")
	private String url;
    @Column(name = "DocPath")
	private String docPath;
    @Column
    private String language;
    @Column(name = "subtitles")
    private List<Subtitle> subtitles;
    @Column(name= "SubtitleProject",index = true , onDelete = Column.ForeignKeyAction.CASCADE)
    private SubtitleProject subtitleProject;
    private int position = -1;
    private InfinotedWriter writer;
    private VltConnectionListener listener;
    private Mode mode = Mode.NORMAL;

    public InfinotedProject() {
        super();
    }


    public InfinotedProject(String url, String docPath, List<Subtitle> subtitles) {
        // this.id = UUID.randomUUID().toString();
        this.url = url;
        this.docPath = docPath;
        this.subtitles = subtitles;
    }

    protected InfinotedProject(Parcel in) {
        this.url = in.readString();
        this.docPath = in.readString();
        this.language = in.readString();
        this.subtitles = new ArrayList<Subtitle>();
        in.readList(this.subtitles, List.class.getClassLoader());
        this.subtitleProject = (SubtitleProject) in.readSerializable();
        this.position = in.readInt();
    }

    public SubtitleProject getSubtitleProject() {
        return subtitleProject;
    }

    public void setSubtitleProject(SubtitleProject subtitleProject) {
        this.subtitleProject = subtitleProject;
    }

    public int getPosition() {
        return position;
    }

    public List<Subtitle> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDocPath() {
		return docPath;
	}

    public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

    public boolean isConnected(){
        if(writer != null)return writer.isConnected();

        return  false;
    }

	public void connect() throws InfinotedClientException{
        if (writer == null) {
            writer = new InfinotedWriter();
            if (listener != null) writer.setDelegate(listener);
        }
        writer.connect(url, docPath);
	}

    public void disconnect(){
        writer.close();
        writer = null;
    }

    public void sendText(int position) throws RequestException{
		if(writer.isConnected()){
			if(position > -1 && position<subtitles.size()){
                writer.removeAll();//if(mode != Mode.ADDITIVE)
				writer.appendText(subtitles.get(position).text());
                this.position = position;
			}
		}else {
            Log.e(TAG, "NOT CONNECTED");
            LocalBroadcastManager.getInstance(MyApp.context).sendBroadcast(new Intent(VltIntent.SESSION_ERROR));
        }

    }

    public void clear() throws RequestException {
		if(writer.isConnected()){
			writer.removeAll();

        }else System.out.println("NOT CONNECTED");
	}

    public void doWait(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendText(long timestamp) throws RequestException {
        int pos = getSubForTimestamp(timestamp);
        if(pos != position){
            sendText(pos);
        }
        if(pos == -1)clear();
    }

    public int getSubForTimestamp(long timestamp){
        return getSubForTimestamp(false,timestamp);
    }

    public int getNearestSubForTimestamp(long timestamp){
        return getSubForTimestamp(true,timestamp);
    }

    public String getTextForTimestamp(long timestamp){
        int pos = getNearestSubForTimestamp(timestamp);
        return subtitles.get(pos).text();
    }

    private int getSubForTimestamp(boolean nearest,long timestamp){
        for (int i=0;i<subtitles.size();i++){
            Subtitle subtitle = subtitles.get(i);
            if(subtitle.startTimeMillis()<=timestamp && subtitle.endTimeMillis()>= timestamp){
                return i;
            }else if(nearest && subtitle.endTimeMillis() <= timestamp  && i<subtitles.size()-1 && subtitles.get(i+1).startTimeMillis() > timestamp)// is between to subtitles
                   return i;
        }
        return nearest?subtitles.size()-1:-1;
    }

    private Subtitle getNext()throws IndexOutOfBoundsException{
        if(position<subtitles.size()-1)return subtitles.get(position+1);
        else throw new IndexOutOfBoundsException("There is no next subtitle!");
    }

    private Subtitle getPrev() throws IndexOutOfBoundsException{
        if(!subtitles.isEmpty() && position>0)return subtitles.get(position-1);
        else throw new IndexOutOfBoundsException("There is no previous subtitle!");
    }

    public long getNextStartTime()throws IndexOutOfBoundsException{
        return getNext().startTimeMillis();
    }

    public long getPrevStartTime() throws IndexOutOfBoundsException{
        return getPrev().startTimeMillis();
    }

    public long getNextEndTime(){
        return subtitles.get(position).endTimeMillis();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setListener(VltConnectionListener listener) {
        this.listener = listener;
    }

    @Override
	public String toString() {
		return "InfinotedProject [url=" + url + ", docPath=" + docPath + "]";
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.docPath);
        dest.writeString(this.language);
        dest.writeList(this.subtitles);
        dest.writeSerializable(this.subtitleProject);
        dest.writeInt(this.position);
    }

    enum Mode {
        NORMAL, ADDITIVE
    }
}
