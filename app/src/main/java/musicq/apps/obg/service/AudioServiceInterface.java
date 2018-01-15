package musicq.apps.obg.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;

import musicq.apps.obg.adapter.MusicAdapter;
import musicq.apps.obg.service.MusicService;

/**
 * Created by d1jun on 2017-12-18.
 */

public class AudioServiceInterface {
    private ServiceConnection mServiceConnection;
    private MusicService mService;
    public AudioServiceInterface(Context context) {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = ((MusicService.AudioServiceBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceConnection = null;
                mService = null;
            }
        };
        context.bindService(new Intent(context, MusicService.class)
                .setPackage(context.getPackageName()), mServiceConnection, Context.BIND_AUTO_CREATE);
    }
    public MusicAdapter.AudioItem getAudioItem() {
        if (mService != null) {
            return mService.getAudioItem();
        }
        return null;
    }
    public void setPlayList(ArrayList<Long> audioIds, String setListAdapter) {
        if (mService != null) {
            mService.setPlayList(audioIds, setListAdapter);
        }
    }

    public void play(int position) {
        if (mService != null) {
            mService.play(position);
        }
    }

    public void play() {
        if (mService != null) {
            mService.play();
        }
    }

    public void pause() {
        if (mService != null) {
            mService.play();
        }
    }

    public void forward() {
        if (mService != null) {
            mService.forward();
        }
    }

    public void rewind() {
        if (mService != null) {
            mService.rewind();
        }
    }
    public void togglePlay() {
        if (isPlaying()) {
            mService.pause();
        } else {
            mService.play();
        }
    }
    public boolean isPlaying() {
        if (mService != null) {
            return mService.isPlaying();
        }
        return false;
    }

    public int getNowPlayingPosition() {
        if (mService != null) {
            return mService.getPosition();
        }
        return 0;
    }

    public int getCurrentPosition() {
        if (mService != null) {
            return mService.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mService != null) {
            return mService.getDuration();
        }
        return 0;
    }

    public void seekTo(int position) {
        if (mService != null) {
            mService.seekTo(position);
        }
    }

    public boolean isMediaAlive() {
        if (mService != null) {
            return mService.isMediaAlive();
        }
        return false;
    }

    public ArrayList<Long> getAudioIds() {
        if (mService !=null) {
            return mService.getAudioIds();
        }
        return null;
    }
}
