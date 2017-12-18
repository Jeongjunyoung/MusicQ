package musicq.apps.obg.service;

import android.app.Application;

import musicq.apps.obg.AudioServiceInterface;


public class MusicApplication extends Application {
    private static MusicApplication mInstance;
    private AudioServiceInterface mInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mInterface = new AudioServiceInterface(getApplicationContext());
    }

    public static MusicApplication getInstance() {
        return mInstance;
    }

    public AudioServiceInterface getServiceInterface() {
        return mInterface;
    }
}
