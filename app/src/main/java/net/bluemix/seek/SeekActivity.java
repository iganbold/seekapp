package net.bluemix.seek;

import android.support.v4.app.Fragment;

import net.bluemix.seek.fragments.FindFragment;

/**
 * Created by john on 8/3/2015.
 */
public class SeekActivity extends SingleFragmentActivity implements FindFragment.OnFragmentInteractionListener{
    @Override
    protected Fragment createFragment() {
        return new FindFragment();
    }

    @Override
    public void onFragmentInteraction(String id) {
    }
}
