package musicq.apps.obg;

import android.Manifest;
import android.content.BroadcastReceiver;
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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import musicq.apps.obg.adapter.MusicAdapter;
import musicq.apps.obg.database.DBHelper;
import musicq.apps.obg.fragment.MusicListFragment;
import musicq.apps.obg.fragment.PlayingMusicFragment;
import musicq.apps.obg.fragment.YoutubeFragment;
import musicq.apps.obg.service.BroadcastActions;
import musicq.apps.obg.service.MusicApplication;

public class MusicQActivity extends AppCompatActivity implements View.OnClickListener{
    YoutubeFragment youtubeFragment;
    MusicListFragment musicListFragment;
    PlayingMusicFragment playingMusicFragment;
    DBHelper dbHelper;
    private static final int LOADER_ID = 0;
    private static final String TAG = "MAIN";
    private static String FRAGMENT_TAG = "PLAYING_FRAGMENT";
    private TextView mTitle;
    private ImageButton mPlayBtn;
    private MusicAdapter mAdapter;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_q);
        youtubeFragment = new YoutubeFragment();
        musicListFragment = new MusicListFragment();
        playingMusicFragment = new PlayingMusicFragment();
        mAdapter = new MusicAdapter(this, null);
        mTitle = (TextView) findViewById(R.id.txt_title);
        mPlayBtn = (ImageButton) findViewById(R.id.btn_play_pause);
        dbHelper = new DBHelper(this);
        dbHelper.open();
        findViewById(R.id.bottom_player).setOnClickListener(this);
        findViewById(R.id.btn_rewind).setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        findViewById(R.id.btn_forward).setOnClickListener(this);
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().beginTransaction().add(R.id.container, youtubeFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, musicListFragment).commit();
        ft1.hide(musicListFragment);
        ft1.commit();
        //getSupportFragmentManager().beginTransaction().add(R.id.container,musicListFragment).attach(musicListFragment);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setIcon(R.drawable.menu_star_icon));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.menu_playlist_icon));
        //tabs.addTab(tabs.newTab().setIcon(R.drawable.menu_playing_icon2));
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                int position = tab.getPosition();
                if (position == 0) {
                    ft2.hide(musicListFragment);
                    ft2.show(youtubeFragment);
                    ft2.commit();
                } else if (position == 1) {
                    ft2.hide(youtubeFragment);
                    ft2.show(musicListFragment);
                    ft2.commit();
                } /*else if (position == 2) {
                    selected = playingMusicFragment;
                }*/
                //getSupportFragmentManager().beginTransaction().replace(R.id.container,selected).commit();


            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
            case R.id.bottom_player:
                // 플레이어 화면으로 이동할 코드가 들어갈 예정
                break;
            case R.id.btn_rewind:
                // 이전곡으로 이동
                MusicApplication.getInstance().getServiceInterface().rewind();
                break;
            case R.id.btn_play_pause:
                // 재생 또는 일시정지
                MusicApplication.getInstance().getServiceInterface().togglePlay();
                break;
            case R.id.btn_forward:
                // 다음곡으로 이동
                MusicApplication.getInstance().getServiceInterface().forward();
                break;
        }
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
