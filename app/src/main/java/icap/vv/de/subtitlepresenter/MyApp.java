package icap.vv.de.subtitlepresenter;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.activeandroid.ActiveAndroid;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Marko on 16.11.2015.
 */
public class MyApp extends com.activeandroid.app.Application{

    public static Bus bus;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
       // MultiDex.install(this);
        bus  = new Bus(ThreadEnforcer.MAIN);
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
    public static Bus getBus() {
        return bus;
    }
}
