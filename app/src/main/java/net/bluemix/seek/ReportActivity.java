package net.bluemix.seek;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.facebook.drawee.backends.pipeline.Fresco;

import net.bluemix.seek.fragments.CameraFragment;
import net.bluemix.seek.fragments.ReportFragment;

/**
 * Created by john on 8/2/2015.
 */
public class ReportActivity extends SingleFragmentActivity{

    private Bundle bundle;

    @Override
    protected Fragment createFragment() {

        ReportFragment reportFragment=new ReportFragment();
        reportFragment.setArguments(bundle);

        return reportFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bundle=getIntent().getExtras();
        super.onCreate(savedInstanceState);
    }
}
