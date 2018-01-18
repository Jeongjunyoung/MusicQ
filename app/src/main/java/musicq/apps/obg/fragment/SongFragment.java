package musicq.apps.obg.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import musicq.apps.obg.PlayingActivity;
import musicq.apps.obg.R;
import musicq.apps.obg.adapter.MusicAdapter;
import musicq.apps.obg.domain.MusicVO;
import musicq.apps.obg.service.BroadcastActions;
import musicq.apps.obg.service.MusicApplication;

public class SongFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private MusicAdapter mAdapter;
    private static ArrayList<MusicVO> list;
    private static String FRAGMENT_TAG = "PLAYING_FRAGMENT";
    private static final int LOADER_ID = 0;
    private static final String TAG = "SONGF";
    Long playingId;
    public static SongFragment newInstance() {
        Bundle args = new Bundle();
        SongFragment fragment = new SongFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("setIds") != null) {
                Log.d("SET", "setIds");
                setAudioList();
                /*getMusicListInPlayList();*/
                //mAdapterMusic.notifyDataSetChanged();
                //mAdapterMusic.setPlayingAudios();
                //recreate();
            }
            int position = intent.getIntExtra("position",0);
            changeMusic(position);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_song, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.song_list);
        mAdapter = new MusicAdapter(getActivity(), null);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                // READ_EXTERNAL_STORAGE 에 대한 권한이 있음.
                getAudioListFromMediaDatabase();

            }
        }
        // OS가 Marshmallow 이전일 경우 권한체크를 하지 않는다.
        else{
            getAudioListFromMediaDatabase();
        }

        registerBroadcast();
        return rootView;
    }

    private void getAudioListFromMediaDatabase() {
        getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(getActivity().getApplication(), uri, projection, selection, null, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE 에 대한 권한 획득.
            getAudioListFromMediaDatabase();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterBroadcast();
    }
    public void changeMusic(int position) {
        Log.d("change", "MUSIC : SONGF");
        mAdapter.bottomUIChangeMusic(position);
    }
    private void setAudioList() {
        MusicApplication.getInstance().getServiceInterface().setPlayList(mAdapter.getAudioIds(),null,"SongAdapter");
        MusicApplication.getInstance().getServiceInterface().forward();
    }
    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PREPARED);
        filter.addAction(BroadcastActions.CHANGE_MUSIC_SONGA);
        filter.addAction(BroadcastActions.SET_AUDIO_IDS);
        getActivity().registerReceiver(mBroadcastReceiver, filter);
    }
    public void unregisterBroadcast(){
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

}
