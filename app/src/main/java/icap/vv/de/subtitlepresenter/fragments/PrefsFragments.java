package icap.vv.de.subtitlepresenter.fragments;

import android.os.Bundle;

import android.support.v7.preference.PreferenceFragmentCompat;
import vv.de.subtitlepresenter.R;

/**
 * Created by crocop on 11.12.15.
 */
public class PrefsFragments extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }
}
