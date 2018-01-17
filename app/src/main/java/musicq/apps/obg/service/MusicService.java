package musicq.apps.obg.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import musicq.apps.obg.adapter.MusicAdapter;
import musicq.apps.obg.adapter.PLMusicListAdapter;

public class MusicService extends Service {
    private final IBinder mBinder = new AudioServiceBinder();
    private MediaPlayer mMediaPlayer;
    private boolean isPrepared;
    private ArrayList<Long> mAudioIds = new ArrayList<>();
    private int mCurrentPosition;
    private String mListAdapter;
    private MusicAdapter.AudioItem mAudioItem;
    private MusicAdapter musicAdapter = new MusicAdapter(this, null);
    private PLMusicListAdapter playMusicAdapter = new PLMusicListAdapter(this, null);
    public class AudioServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                mp.start();
                Intent intent = null;
                        //new Intent(BroadcastActions.PREPARED);
                if (mListAdapter.equals("PLMusicAdapter")) {
                    intent = new Intent(BroadcastActions.CHANGE_MUSIC_PLMA);
                } else if(mListAdapter.equals("SongAdapter")){
                    Log.d("SPL","CHECK");
                    intent = new Intent(BroadcastActions.CHANGE_MUSIC_SONGA);
                }
                intent.putExtra("position", mCurrentPosition);
                sendBroadcast(intent);
                //sendBroadcast(new Intent(BroadcastActions.PREPARED)); //prepared 전송
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                isPrepared = false;
                sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
                return false;
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("SER","Complete");
                //MusicApplication.getInstance().getServiceInterface().forward();
                Intent intent = null;
                if (mListAdapter.equals("PLMusicAdapter")) {
                    intent = new Intent(BroadcastActions.SET_AUDIO_IDS);
                } else if(mListAdapter.equals("SongAdapter")){
                    intent = new Intent(BroadcastActions.SET_AUDIO_IDS_SONG);
                }
                intent.putExtra("setIds", "setIds");
                sendBroadcast(intent);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setPlayList(ArrayList<Long> audioIds, String setListAdapter) {
        if (mAudioIds.size() != audioIds.size()) {
            if (!mAudioIds.equals(audioIds)) {
                mAudioIds.clear();
                mAudioIds.addAll(audioIds);
                mListAdapter = setListAdapter;
            }
        }
    }
    private void queryAudioItem(int position) {
        mCurrentPosition = position;
        long audioId = mAudioIds.get(position);
        Log.d("AID", "" + audioId);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };
        String selection = MediaStore.Audio.Media._ID + " = ?";
        String[] selectionArgs = {String.valueOf(audioId)};
        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mAudioItem = MusicAdapter.AudioItem.bindCursor(cursor);
            }
            cursor.close();
        }
    }
    private void prepare() {
        try {
            mMediaPlayer.setDataSource(mAudioItem.mDataPath);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void stop() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
    }
    public void play(int position) {
        queryAudioItem(position);
        stop();
        prepare();
    }
    public void play() {
        if (isPrepared) {
            mMediaPlayer.start();
            sendPlayPauseState();
            //sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
        }
    }
    public void pause() {
        if (isPrepared) {
            mMediaPlayer.pause();
            sendPlayPauseState();
            //sendBroadcast(new Intent(BroadcastActions.PLAY_STATE_CHANGED)); // 재생상태 변경 전송
        }
    }
    public void forward() {
        if (mAudioIds.size() - 1 > mCurrentPosition) {
            Log.d("MS", "forward() >> Next");
            mCurrentPosition++; // 다음 포지션으로 이동.
        } else {
            Log.d("MS", "forward() >> First");
            mCurrentPosition = 0; // 처음 포지션으로 이동.
        }
        //pass(mCurrentPosition);
        play(mCurrentPosition);
    }

    public void rewind() {
        if (mCurrentPosition > 0) {
            mCurrentPosition--; // 이전 포지션으로 이동.
        } else {
            mCurrentPosition = mAudioIds.size() - 1; // 마지막 포지션으로 이동.
        }
        //pass(mCurrentPosition);
        play(mCurrentPosition);
    }
    public MusicAdapter.AudioItem getAudioItem() {
        return mAudioItem;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void pass(int position) {
        /*Intent intent;
        if (mListAdapter.equals("PLMusicAdapter")) {
            intent = new Intent(BroadcastActions.CHANGE_MUSIC_PLMA);
        } else {
            intent = new Intent(BroadcastActions.CHANGE_MUSIC_SONGA);
        }
        intent.putExtra("position", position);
        sendBroadcast(intent);*/
    }

    public int getPosition() {
        return mCurrentPosition;
    }

    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public void seekTo(int position){
        mMediaPlayer.seekTo(position);
    }

    public ArrayList<Long> getAudioIds() {
        return mAudioIds;
    }
    public Boolean isMediaAlive() {
        if (mMediaPlayer == null) {
            return false;
        }
        return true;
    }
    public void sendPlayPauseState() {
        Intent intent = new Intent(BroadcastActions.PLAY_STATE_CHANGED);
        intent.putExtra("ppBtn", "ppBtn");
        sendBroadcast(intent);
    }
}
