package icap.vv.de.subtitlepresenter.pojo;


import android.os.Parcel;
import android.os.Parcelable;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import de.lits.adopted.exception.RequestException;
import de.lits.infinoted.InfinotedClientException;
import icap.vv.de.subtitlepresenter.infinoted.VltConnectionListener;

import java.io.Serializable;
import java.util.List;


@Table(name ="SubtitleProjects")
public class SubtitleProject  extends Model implements Serializable, Parcelable {


	public static final Parcelable.Creator<SubtitleProject> CREATOR = new Parcelable.Creator<SubtitleProject>() {
		public SubtitleProject createFromParcel(Parcel source) {
			return new SubtitleProject(source);
		}

		public SubtitleProject[] newArray(int size) {
			return new SubtitleProject[size];
		}
	};
	//private String id;
	@Column(name= "Title", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private String title;
	@Column(name="Seeker")
	private long seeker;
	private int connections = 0;//TODO this should be handled in its own class
	private SubtitleProjectListener listener;
	private VltConnectionListener connectionListener = new VltConnectionListener() {
		@Override
		public void onConnected() {
			connections++;
			if (listener != null) {
				if (connections == getProjects().size()) {
					listener.onAllConnected();
				}
			}
		}

		@Override
		public void onConnectionError() {
			if (listener != null) listener.onConnectionError();
		}
	};
	
    public SubtitleProject() {
        super();
    }

	public SubtitleProject(String title){
		this.title = title;
	}

	public SubtitleProject(String title , InfinotedProject... projs){
		this.title = title;
        for(InfinotedProject proj:projs)add(proj);
		//projects.addAll(Arrays.asList(projs));
	}

	protected SubtitleProject(Parcel in) {
		this.title = in.readString();
		this.seeker = in.readLong();
	}

	public boolean add(InfinotedProject proj) {
        proj.setSubtitleProject(this);
        proj.save();
        return true;
	}

	public boolean remove(Object proj) {
        ((InfinotedProject)proj).delete();
        return true;
	}

	public int size() {
		return getProjects().size();
	}

	public void connect() throws InfinotedClientException{
		connections = 0;
		for (InfinotedProject proj : getProjects()) {
			proj.setListener(connectionListener);
			proj.connect();
		}
	}

	public void disconnect(){
		for(InfinotedProject proj:getProjects())proj.disconnect();
	}

	public boolean isConnected(){
		for(InfinotedProject proj:getProjects()){
			if(!proj.isConnected())return false;
		}
		return true;
	}

	public List<InfinotedProject> getProjects() {

			return getMany(InfinotedProject.class,"SubtitleProject");


        //return projects;
	}

	public void clear() throws RequestException {
		for(InfinotedProject proj:getProjects())proj.clear();
	}

	public void updatePosition(int position) throws RequestException {
		for(InfinotedProject proj:getProjects())proj.sendText(position);
	}

    public void updatePosition(long millis) throws RequestException {
        for(InfinotedProject proj:getProjects())proj.sendText(millis);
        setSeeker(millis);
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

    public long getSeeker() {
        return seeker;
    }

    public void setSeeker(long seeker) {
        this.seeker = seeker;
    }

	public void setListener(SubtitleProjectListener listener) {
		this.listener = listener;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.title);
		dest.writeLong(this.seeker);
	}
}
