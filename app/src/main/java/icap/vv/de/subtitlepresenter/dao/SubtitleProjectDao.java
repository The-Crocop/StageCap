package icap.vv.de.subtitlepresenter.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.activeandroid.query.Select;
import de.verbavoice.parser.SRTParser;
import de.verbavoice.parser.Subtitle;
import icap.vv.de.subtitlepresenter.pojo.InfinotedProject;
import icap.vv.de.subtitlepresenter.pojo.SubtitleProject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Created by Marko Nalis on 28.10.2015.
 */
public class SubtitleProjectDao implements BasicDao<SubtitleProject>{

    private final static String FIRST_USE_KEY ="firstUse";
    private final static String HOST = "conferencebeta.verbavoice.net";//"54.200.142.239";//"192.168.1.149";//"conferencebeta.verbavoice.net";
    private SharedPreferences sharedPreferences;
    private Context context;


    public SubtitleProjectDao(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static List<Subtitle> loadSubs(InputStream in) throws IOException {
        SRTParser parser = new SRTParser();
        return parser.parse(in).subtitles();
    }

    public static List<Subtitle> loadSubs(Context context, String filePath) throws IOException {
        SRTParser parser = new SRTParser();
        InputStream in;
        in = context.getAssets().open(filePath);
        return parser.parse(in).subtitles();
    }

    private void populateDB() throws IOException {

        SubtitleProject macBeth = new SubtitleProject("Macbeth");
        macBeth.save();

        InfinotedProject mcb_en = new InfinotedProject(HOST, "stagecap-en", loadSubs("theater_teil_macbeth_angepasst.srt"));
        mcb_en.setLanguage("en");
        mcb_en.setSubtitleProject(macBeth);
        InfinotedProject mcb_de = new InfinotedProject(HOST, "stagecap-de", loadSubs("theater_teil_macbeth_deutsch_angepasst.srt"));
        mcb_de.setLanguage("de");
        mcb_de.setSubtitleProject(macBeth);
        mcb_en.save();
        mcb_de.save();
        /*SubtitleProject startWars = new SubtitleProject("starWars");
        SubtitleProject theWalkingDead = new SubtitleProject("The Walking Dead");
        startWars.save();
        theWalkingDead.save();*/


        /*InfinotedProject sw01ger = new InfinotedProject(HOST, "m.nalis-de",loadSubs("swe01ger1.srt") );
        sw01ger.setSubtitleProject(startWars);
        InfinotedProject sw01en = new InfinotedProject(HOST, "m.nalis-en",loadSubs("swe01en1.srt"));
        sw01en.setSubtitleProject(startWars);
        InfinotedProject twd = new InfinotedProject(HOST, "m.nalis-de", loadSubs("twdtest1.srt"));
        twd.setSubtitleProject(theWalkingDead);

        InfinotedProject twd = new InfinotedProject(HOST, "m.nalis-de", loadSubs("twdtest1.srt"));

        sw01ger.save();
        sw01en.save();
        twd.save();*/

        this.setFirstUse(false);

    }

    private List<Subtitle> loadSubs(String filePath) throws IOException {
        SRTParser parser = new SRTParser();
        InputStream in;
        in = context.getAssets().open(filePath);
        return parser.parse(in).subtitles();
    }

    public boolean isFirstUse() {
      return  sharedPreferences.getBoolean(FIRST_USE_KEY, true);
      //  return true;
    }

    public void setFirstUse(boolean firstUse) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(FIRST_USE_KEY,firstUse);
        edit.commit();
    }

    @Override
    public List<SubtitleProject> getAll()  {
      if(isFirstUse()) try {
            populateDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Select()
                .from(SubtitleProject.class)
                .orderBy("Title ASC")
                .execute();
    }

    @Override
    public void save(SubtitleProject element) {
        element.save();
    }


    @Override
    public void delete(SubtitleProject element) {
        element.delete();
    }

    @Override
    public void update(SubtitleProject element) {
        element.save();
    }

    @Override
    public SubtitleProject findById(String title) {
        return null;
    }

    @Override
    public void clearAll() {

    }

    public void close(){

    };


}
