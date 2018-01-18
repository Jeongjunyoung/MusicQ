package musicq.apps.obg;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import musicq.apps.obg.adapter.AlbumPagerAdapter;
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
    AlbumPagerAdapter pagerAdapter;
    private static final int LOADER_ID = 0;
    private static final String TAG = "MAIN";
    private static String FRAGMENT_TAG = "PLAYING_FRAGMENT";
    private TextView mTitle, title, mDuration, pDuration;
    private SeekBar seekBar;
    private ImageView mAlbumArt, album;
    private ImageButton mPlayBtn, play;
    private MusicAdapter mAdapter;
    private LinearLayout playingLayout;
    private ProgressUpdate progressUpdate;
    private ViewPager viewPager;
    private boolean isNext = false;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("ppBtn") != null) {
                updatePPBtn();
            } else if (intent.getStringExtra("setIds") != null) {
                setAudioList();
            } else {
                Log.d("MAIN","po" + MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition());
                pagerAdapter.notifyDataSetChanged();
                pagerAdapter.setAudioIds();
                viewPager.setCurrentItem(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition());
                updateUI();
            }

            /*if (!(progressUpdate.isAlive())) {
                Log.d("MAIN","progressUpdate().start() 호출");
                progressUpdate.start();
            }*/
            new ProgressUpdate().start();
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
        title = (TextView) findViewById(R.id.title);
        mDuration = (TextView) findViewById(R.id.music_duration);
        pDuration = (TextView) findViewById(R.id.music_playing_duration);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        mAlbumArt = (ImageView) findViewById(R.id.album_image);
        album = (ImageView) findViewById(R.id.album);
        mPlayBtn = (ImageButton) findViewById(R.id.btn_play_pause);
        play = (ImageButton) findViewById(R.id.play);
        playingLayout = (LinearLayout) findViewById(R.id.playing_layout);
        viewPager = (ViewPager) findViewById(R.id.music_pager);
        pagerAdapter = new AlbumPagerAdapter(getLayoutInflater());
        dbHelper = new DBHelper(this);
        viewPager.setAdapter(pagerAdapter);
        dbHelper.open();
        findViewById(R.id.bottom_player).setOnClickListener(this);
        findViewById(R.id.btn_rewind).setOnClickListener(this);
        findViewById(R.id.pre).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        play.setOnClickListener(this);
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


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    MusicApplication.getInstance().getServiceInterface().seekTo(i);
                }
                pDuration.setText(setDuration(i/60000,(i%60000)/1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //MusicApplication.getInstance().getServiceInterface().pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /*MusicApplication.getInstance().getServiceInterface().seekTo(seekBar.getProgress());
                if (seekBar.getProgress() > 0 && MusicApplication.getInstance().getServiceInterface().isPlaying()) {
                    MusicApplication.getInstance().getServiceInterface().play();
                }*/
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int mState = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int mPosition =  MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition();
                Log.d("MAIN", "PagerListener() >> position : "+position + ", mPosition : "+ mPosition + ", mState : "+mState);
                /*if (position > mPosition && mState == 2) {
                    Log.d("MAIN", "UP");
                    MusicApplication.getInstance().getServiceInterface().forward();
                } else if(position < mPosition && mState == 2){
                    Log.d("MAIN", "DOWN");
                    MusicApplication.getInstance().getServiceInterface().rewind();
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                mState = state;
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
                playingLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_rewind:
                // 이전곡으로 이동
                MusicApplication.getInstance().getServiceInterface().rewind();
               //viewPager.setCurrentItem(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition());
                break;
            case R.id.btn_play_pause:
                // 재생 또는 일시정지
                MusicApplication.getInstance().getServiceInterface().togglePlay();
                break;
            case R.id.btn_forward:
                // 다음곡으로 이동
                MusicApplication.getInstance().getServiceInterface().forward();
                //viewPager.setCurrentItem(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition());
                break;
            case R.id.play:
                MusicApplication.getInstance().getServiceInterface().togglePlay();
                break;
            case R.id.pre:
                //pagerAdapter.setAudioIds();
                //pagerAdapter.notifyDataSetChanged();
                isNext = false;
                MusicApplication.getInstance().getServiceInterface().rewind();
                viewPager.setCurrentItem(viewPager.getCurrentItem()-1,true);
                //viewPager.setCurrentItem(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition());
                break;
            case R.id.next:
                //pagerAdapter.setAudioIds();
                //pagerAdapter.notifyDataSetChanged();
                isNext = true;
                MusicApplication.getInstance().getServiceInterface().forward();
                //pagerAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1,true);
                //viewPager.setCurrentItem(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition());
                break;
        }
    }

    private void updateUI() {
        seekBar.setMax(MusicApplication.getInstance().getServiceInterface().getDuration());
        seekBar.setProgress(0);
        pagerAdapter.setAudioIds();
        pagerAdapter.notifyDataSetChanged();
        /*if (isNext) {
            Log.d("MA","viewPager.getCurrentItem() : "+viewPager.getCurrentItem());
            viewPager.setCurrentItem(viewPager.getCurrentItem() +2, true);
        } else if(!isNext) {
            Log.d("MA","viewPager.getCurrentItem() : "+viewPager.getCurrentItem());
            viewPager.setCurrentItem(viewPager.getCurrentItem()+2, true);
        }*/


        if (MusicApplication.getInstance().getServiceInterface().isPlaying()) {
            mPlayBtn.setImageResource(R.drawable.pause_icon);
            play.setImageResource(R.drawable.pause_icon);
        } else {
            mPlayBtn.setImageResource(R.drawable.play_icon);
            play.setImageResource(R.drawable.play_icon);
        }
        MusicAdapter.AudioItem audioItem = MusicApplication.getInstance().getServiceInterface().getAudioItem();
        if (audioItem != null) {
            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), audioItem.mAlbumId);
            //Picasso.with(getApplicationContext()).load(albumArtUri).error(R.drawable.album_default_icon).into(mAlbumArt);
            //Picasso.with(getApplicationContext()).load(albumArtUri).error(R.drawable.album_default_icon).into(album);
            mTitle.setText(audioItem.mTitle);
            title.setText(audioItem.mTitle);
            int sec_duration = (int)audioItem.mDuration / 1000;
            String maxDuration = setDuration(sec_duration/60, sec_duration%60);
            mDuration.setText(maxDuration);
        } else {
            //mImgAlbumArt.setImageResource(R.drawable.empty_albumart);
            mTitle.setText("재생중인 음악이 없습니다.");
            title.setText("재생중인 음악이 없습니다.");
        }
    }

    private void updatePPBtn() {
        if (MusicApplication.getInstance().getServiceInterface().isPlaying()) {
            mPlayBtn.setImageResource(R.drawable.pause_icon);
            play.setImageResource(R.drawable.pause_icon);
        } else {
            mPlayBtn.setImageResource(R.drawable.play_icon);
            play.setImageResource(R.drawable.play_icon);
        }
    }
    public void registerBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastActions.PREPARED);
        filter.addAction(BroadcastActions.PLAY_STATE_CHANGED);
        filter.addAction(BroadcastActions.CHANGE_MUSIC_SONGA);
        filter.addAction(BroadcastActions.CHANGE_MUSIC_PLMA);
        filter.addAction(BroadcastActions.SET_AUDIO_IDS_SONG);
        registerReceiver(mBroadcastReceiver, filter);
    }
    public void unregisterBroadcast(){
        unregisterReceiver(mBroadcastReceiver);
    }

    class ProgressUpdate extends Thread{
        @Override
        public void run() {
            while(MusicApplication.getInstance().getServiceInterface().isPlaying()){
                try {
                    Thread.sleep(1000);
                    if (MusicApplication.getInstance().getServiceInterface().isPlaying()) {
                        //Log.d("MAIN","Current : " + MusicApplication.getInstance().getServiceInterface().getCurrentPosition());
                        seekBar.setProgress(MusicApplication.getInstance().getServiceInterface().getCurrentPosition());
                    }
                } catch (Exception e) {
                    Log.d("ProgressUpdate",e.getMessage());
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (playingLayout.getVisibility() == View.VISIBLE) {
            playingLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    public String setDuration(int minutes, int seconds) {
        String minutesStr = String.valueOf(minutes);
        String secondsStr = "";
        if (seconds < 10) {
            secondsStr = "0" + String.valueOf(seconds);
        } else {
            secondsStr = String.valueOf(seconds);
        }
        return minutesStr + ":" + secondsStr;
    }
    private void setAudioList() {
        //MusicApplication.getInstance().getServiceInterface().setPlayList(mAdapter.getAudioIds(),"SongAdapter");
        //MusicApplication.getInstance().getServiceInterface().forward();
    }
    /*Playing Layout 에서 목록으로
    * Playing Layout 에서 Control 기능
    * */
}
