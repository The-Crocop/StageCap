package icap.vv.de.subtitlepresenter;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;

/**
 * Created by Marko Nalis on 14.09.2015.
 */
public class GetSubtitleProjectsTask extends AsyncTask<Void,Void,SubtitleProject[]> {

    private final String url;

    private SubtitleProjectAsyncResponse delegate = null;

    public SubtitleProjectAsyncResponse getDelegate() {
        return delegate;
    }

    public void setDelegate(SubtitleProjectAsyncResponse delegate) {
        this.delegate = delegate;
    }

    public GetSubtitleProjectsTask(String url){
        super();
        this.url = url;
    }

    @Override
    protected SubtitleProject[] doInBackground(Void... params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate.getForObject(url,SubtitleProject[].class);
    }

    @Override
    protected void onPostExecute(SubtitleProject[] subtitleProjects) {
        if(delegate != null)delegate.processResult(subtitleProjects);
    }
}
