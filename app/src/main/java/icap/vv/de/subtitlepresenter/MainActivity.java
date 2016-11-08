package icap.vv.de.subtitlepresenter;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import icap.vv.de.subtitlepresenter.fragments.MenuFragment;
import icap.vv.de.subtitlepresenter.fragments.PrefsFragments;
import vv.de.subtitlepresenter.R;

public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awsome_toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        }else getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MenuFragment()).commit();



    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag(fragmentTag);
        getSupportFragmentManager().putFragment(outState,"mContent",currentFragment);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, new PrefsFragments());
            transaction.addToBackStack("openSub");
            transaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
