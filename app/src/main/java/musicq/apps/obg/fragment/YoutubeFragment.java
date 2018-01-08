package musicq.apps.obg.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

import musicq.apps.obg.R;
import musicq.apps.obg.adapter.Top100Adapter;
import musicq.apps.obg.domain.Top100VO;

public class YoutubeFragment extends Fragment implements YouTubePlayer.OnInitializedListener, View.OnClickListener{
    Top100Adapter listAdapter;
    //YouTubePlayerView youTubeView;
    YouTubePlayerSupportFragment youTubeFragment;
    private Button playBtn;
    private Button prevBtn;
    private Button nextBtn;
    private TextView playingTitle;
    //YouTubePlayer.OnInitializedListener listener;
    private static final String API_KEY = "AIzaSyDqn5t2was3NcLmN1YihOteVxlkajlmEec";
    private static int mPlayingPosition;
    private String videoKey = "";
    private YouTubePlayer mPlayer;
    private static ArrayList<Top100VO> playLists;
    private boolean isPlaying = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_youtube, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.top100_list);
        playBtn = (Button) rootView.findViewById(R.id.youtube_play);
        prevBtn = (Button) rootView.findViewById(R.id.youtube_prev);
        nextBtn = (Button) rootView.findViewById(R.id.youtube_next);
        playingTitle = (TextView) rootView.findViewById(R.id.youtube_playing);

        listAdapter = new Top100Adapter(getActivity());
        listAdapter.addItem(new Top100VO(1,"박효신 - 겨울 소리", "U9XJPcJDAN8"));
        listAdapter.addItem(new Top100VO(2,"나얼 - 기억의 빈자리", "0wlXaHmmOVc"));
        listAdapter.addItem(new Top100VO(3,"지드래곤 - 무제", "9kaCAbIXuyg"));
        listView.setAdapter(listAdapter);

        playLists = listAdapter.getLists();

        youTubeFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_layout, youTubeFragment).commit();

        videoKey = "";
        listView.setOnItemClickListener(new ListViewItemClickListener());
        youTubeFragment.initialize(API_KEY, this);

        playBtn.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        //youTubeView = (YouTubePlayerView) rootView.findViewById(R.id.youtube_view);

        /*listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
                if (!b) {
                    mPlayer = player;
                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    Log.d("YOU", "player Success");
                    //player.loadVideo(videoKey);
                    //player.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                String errorMessage = error.toString();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                Log.d("errorMessage:", errorMessage);
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        return rootView;
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
        if (!b) {
            this.mPlayer = player;
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            Log.d("YOU", "player Success");
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
        String errorMessage = error.toString();
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        Log.d("errorMessage:", errorMessage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.youtube_play:
                if (isPlaying) {
                    isPlaying = false;
                    playBtn.setText("PLAY");
                    mPlayer.pause();
                } else {
                    isPlaying = true;
                    playBtn.setText("PAUSE");
                    mPlayer.play();
                }
                break;
            case R.id.youtube_prev:
                if (mPlayingPosition == 0) {
                    mPlayingPosition = listAdapter.getCount() - 1;
                } else {
                    mPlayingPosition -= 1;
                }
                setPlayingVideo();
                break;
            case R.id.youtube_next:
                if (mPlayingPosition == listAdapter.getCount()-1) {
                    mPlayingPosition = 0;
                } else {
                    mPlayingPosition += 1;
                }
                setPlayingVideo();
                break;
        }
    }

    private class ListViewItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mPlayingPosition = i;
            isPlaying = true;
            playBtn.setText("PAUSE");
            Log.d("YOU", "KEY : " + videoKey);
            Log.d("YOU", "POSITION : " + mPlayingPosition);
            setPlayingVideo();
        }
    }

    public void setPlayingVideo() {
        Log.d("YOU", "setPlayingVideo()  : 호출, position : " + mPlayingPosition);
        Top100VO vo = (Top100VO) listAdapter.getItem(mPlayingPosition);
        //videoKey = listAdapter.getVideoId(mPlayingPosition);
        playingTitle.setText(vo.getTitle().toString());
        mPlayer.loadVideo(vo.getVideoId());
        mPlayer.play();
    }
}
