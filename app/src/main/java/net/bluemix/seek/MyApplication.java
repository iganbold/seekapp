package net.bluemix.seek;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by john on 7/29/2015.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance=this;
    }

    public static MyApplication getInstance()
    {
        return sInstance;
    }

    public static Context getAppCeontext()
    {
        return sInstance.getApplicationContext();
    }
}
