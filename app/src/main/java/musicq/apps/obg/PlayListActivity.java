package musicq.apps.obg;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import musicq.apps.obg.adapter.MusicAdapter;
import musicq.apps.obg.adapter.PLMusicListAdapter;
import musicq.apps.obg.service.BroadcastActions;
import musicq.apps.obg.service.MusicApplication;

public class PlayListActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int LOADER_ID = 0;
    private RecyclerView mRecyclerViewMusic;
    private PLMusicListAdapter mAdapterMusic;
    private String listName;
    private int mId;
    private int listId;
    private TextView mTitle;
    private ImageButton mPlayBtn;
    //FloatingActionButton musicListBtn;
    Button musicListBtn;
    LinearLayout listLayout;
    LinearLayout playListLayout;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        mRecyclerViewMusic = (RecyclerView) findViewById(R.id.playlist_music);
        mAdapterMusic = new PLMusicListAdapter(this, null);
        mRecyclerViewMusic.setAdapter(mAdapterMusic);
        LinearLayoutManager layoutManagerMusic = new LinearLayoutManager(this);
        layoutManagerMusic.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewMusic.setLayoutManager(layoutManagerMusic);
        TextView listTitle = (TextView) findViewById(R.id.list_title);
        //musicListBtn = (FloatingActionButton) findViewById(R.id.add_music_playlist);
        musicListBtn = (Button) findViewById(R.id.add_music_playlist);
        //listLayout = (LinearLayout) findViewById(R.id.music_list_layout);
        playListLayout = (LinearLayout) findViewById(R.id.playlist_layout);
        mTitle = (TextView) findViewById(R.id.txt_title_playlist);
        mPlayBtn = (ImageButton) findViewById(R.id.btn_play_pause_playlist);
        Intent intent = getIntent();
        listName = intent.getStringExtra("name");
        mId = intent.getIntExtra("id", 0);
        String str = listName + " : " + String.valueOf(mId);
        listTitle.setText(str);
        musicListBtn.setOnClickListener(this);
        //findViewById(R.id.bottom_player_playlist).setOnClickListener(this);
        findViewById(R.id.btn_rewind_playlist).setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        findViewById(R.id.btn_forward_playlist).setOnClickListener(this);
        //getMusicListInPlayList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                // READ_EXTERNAL_STORAGE 에 대한 권한이 있음.
                //getAudioListFromMediaDatabase();
                getMusicListInPlayList();
            }
        }
        else{
            //getAudioListFromMediaDatabase();
            getMusicListInPlayList();
        }
        registerBroadcast();
        updateUI();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
                case R.id.add_music_playlist:
                    //playListLayout.setEnabled(false);
                    //musicListBtn.setVisibility(View.GONE);
                    //listLayout.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(getApplicationContext(), MusiclistForPlaylist.class);
                    intent.putExtra("listName", listName);
                    startActivity(intent);
                    break;
                case R.id.bottom_player_playlist:
                    // 플레이어 화면으로 이동할 코드가 들어갈 예정
                    break;
                case R.id.btn_rewind_playlist:
                    // 이전곡으로 이동
                    MusicApplication.getInstance().getServiceInterface().rewind();
                    break;
                case R.id.btn_play_pause_playlist:
                    // 재생 또는 일시정지
                    MusicApplication.getInstance().getServiceInterface().togglePlay();
                    break;
                case R.id.btn_forward_playlist:
                    // 다음곡으로 이동
                    MusicApplication.getInstance().getServiceInterface().forward();
                    break;
            }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE 에 대한 권한 획득.
            //getAudioListFromMediaDatabase();
            getMusicListInPlayList();
        }
    }

    public void getMusicListInPlayList() {
        ContentResolver resolver = getContentResolver();
        Cursor c1 = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME +"=?",
                new String[]{listName},
                null);
        c1.moveToFirst();
        listId = c1.getInt(0);
        c1.close();
        getSupportLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", listId);
                String[] projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA
                };
                String sortOder = MediaStore.Audio.Playlists.Members.PLAY_ORDER +" ASC";

                return new CursorLoader(getApplicationContext(), uri, projection, null, null, sortOder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapterMusic.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapterMusic.swapCursor(null);
            }
        });
    }
    private void updateUI() {
        if (MusicApplication.getInstance().getServiceInterface().isPlaying()) {
            mPlayBtn.setImageResource(R.drawable.pause_icon);
        } else {
            mPlayBtn.setImageResource(R.drawable.play_icon);
        }
        MusicAdapter.AudioItem audioItem = MusicApplication.getInstance().getServiceInterface().getAudioItem();
        if (audioItem != null) {
            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), audioItem.mAlbumId);
            //Picasso.with(getApplicationContext()).load(albumArtUri).error(R.drawable.empty_albumart).into(mImgAlbumArt);
            mTitle.setText(audioItem.mTitle);
        } else {
            //mImgAlbumArt.setImageResource(R.drawable.empty_albumart);
            mTitle.setText("재생중인 음악이 없습니다.");
        }
    }
    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PREPARED);
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }
    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }
}
