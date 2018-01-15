package musicq.apps.obg.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import musicq.apps.obg.R;
import musicq.apps.obg.service.MusicApplication;

/**
 * Created by d1jun on 2017-12-29.
 */

public class PLMusicListAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder>{
    private static int playPosition;
    private static int count;
    private ArrayList<View> viewList = new ArrayList<>();
    private ArrayList<AudioViewHolder> holders = new ArrayList<>();
    public PLMusicListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        AudioItemList audioItem = AudioItemList.bindCursor(cursor);
        if (audioItem != null) {
            ((AudioViewHolder) viewHolder).setAudioItem(audioItem, cursor.getPosition());
            boolean isDup = false;
            for(int i=0;i<holders.size();i++) {
                if (holders.get(i).mItem.mId == ((PLMusicListAdapter.AudioViewHolder) viewHolder).mItem.mId) {
                    Log.d("PLMA","onBindViewHolder() >> find");
                    isDup = true;
                }
            }
            if (!isDup) {
                holders.add((AudioViewHolder) viewHolder);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        AudioViewHolder audioViewHolder = new AudioViewHolder(v);
        audioViewHolder.mImgAlbumArt = (ImageView) v.findViewById(R.id.album_image);
        audioViewHolder.mTitle = (TextView) v.findViewById(R.id.music_title);
        audioViewHolder.mArtist = (TextView) v.findViewById(R.id.music_artist);
        audioViewHolder.mIsPlaying = (ImageView) v.findViewById(R.id.is_playing);
        v.setTag(audioViewHolder);
        viewList.add(v);
        return audioViewHolder;
    }
    public ArrayList<Long> getAudioIds() {
        //count = getItemCount();
        View view = null;
        Log.d("PLMA", "getAudioIds() >> count : " + count);
        ArrayList<Long> audioIds = new ArrayList<>();
        /*for (int i = 0; i < holders.size(); i++) {
            //view = viewList.get(i);
            //AudioViewHolder audioViewHolder = (AudioViewHolder) view.getTag();
            //Log.d("PLMA", "getItemId() >> ID : " + getAudioIds());
            //audioIds.add(getItemId(i));
            audioIds.add(holders.get(i).mItem.mId);
            //audioIds.add(audioViewHolder.mItem.mId);
        }*/
        for(int i=0; i<viewList.size(); i++) {
            view = viewList.get(i);
            AudioViewHolder holder = (AudioViewHolder) view.getTag();
            Log.d("PLMA", "getItemId() >> ID : " + holder.mTitle.getTag());
            audioIds.add((Long)holder.mTitle.getTag());
        }
        return audioIds;
    }
    public static class AudioItemList {
        public long mId; // 오디오 고유 ID
        public long mAlbumId; // 오디오 앨범아트 ID
        public String mTitle; // 타이틀 정보
        public String mArtist; // 아티스트 정보
        public String mAlbum; // 앨범 정보
        public long mDuration; // 재생시간
        public String mDataPath; // 실제 데이터위치

        public static AudioItemList bindCursor(Cursor cursor) {
            AudioItemList audioItem = new AudioItemList();
            audioItem.mId =cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
            audioItem.mAlbumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            audioItem.mTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            audioItem.mArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            audioItem.mAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            audioItem.mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
            audioItem.mDataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            return audioItem;
        }
    }
    private class AudioViewHolder extends RecyclerView.ViewHolder {
        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        private ImageView mImgAlbumArt;
        private TextView mTitle;
        private TextView mArtist;
        private ImageView mIsPlaying;
        //private TextView mTxtDuration;
        private AudioItemList mItem;
        private int mPosition;
        private AudioViewHolder(View view) {
            super(view);
            /*mImgAlbumArt = (ImageView) view.findViewById(R.id.album_image);
            mTitle = (TextView) view.findViewById(R.id.music_title);
            mArtist = (TextView) view.findViewById(R.id.music_artist);*/
            //mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ID", "" + mPosition);
                    MusicApplication.getInstance().getServiceInterface().setPlayList(getAudioIds(), "PLMusicAdapter"); // 재생목록등록
                    MusicApplication.getInstance().getServiceInterface().play(mPosition); // 선택한 오디오재생
                    playPosition = mPosition;
                    setNowPlaying();
                }
            });
        }

        public void setAudioItem(PLMusicListAdapter.AudioItemList item, int position) {
            mItem = item;
            mPosition = position;
            if (playPosition == mPosition) {
                mTitle.setTextColor(Color.parseColor("#e49292"));
                mIsPlaying.setVisibility(View.VISIBLE);
            }
            mTitle.setText(item.mTitle);
            mTitle.setTag(item.mId);
            mArtist.setText(item.mArtist);
            mArtist.setTag(position);
            mTitle.setTextColor(Color.parseColor("#464646"));
            mIsPlaying.setImageResource(R.drawable.isplaying_icon);
            mIsPlaying.setVisibility(View.GONE);
            /*if (item.mId == getAudioIds().get(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition())) {
                mTitle.setTextColor(Color.parseColor("#e49292"));
                mIsPlaying.setVisibility(View.VISIBLE);
            }*/
            //mArtist.setText(item.mArtist + "(" + item.mAlbum + ")");
            //mTxtDuration.setText(DateFormat.format("mm:ss", item.mDuration));
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.mAlbumId);
            Glide.with(itemView.getContext()).load(albumArtUri).error(R.drawable.album_default_icon).into(mImgAlbumArt);
        }
    }
    public void setNowPlaying() {
        /*PLMusicListAdapter.AudioViewHolder audioViewHolder;
        Log.d("PLMA", "setNowPlaying() : " + position);
        for(int i=0;i<viewList.size();i++) {
            audioViewHolder = (PLMusicListAdapter.AudioViewHolder) viewList.get(i);
            if (i == position) {
                //audioViewHolder.mTitle.setTextColor(Color.parseColor("#e49292"));
            } else {
                audioViewHolder.mTitle.setTextColor(Color.parseColor("#464646"));
            }
        }*/
        View view = null;
        for(int i=0;i<viewList.size();i++) {
            view = viewList.get(i);
            AudioViewHolder holder = (AudioViewHolder) view.getTag();
            //if (holder.mTitle.getTag() == getAudioIds().get(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition())) {
            if((int)holder.mArtist.getTag() == playPosition){
                holder.mTitle.setTextColor(Color.parseColor("#e49292"));
                holder.mIsPlaying.setVisibility(View.VISIBLE);
            } else {
                holder.mTitle.setTextColor(Color.parseColor("#464646"));
                holder.mIsPlaying.setVisibility(View.GONE);
            }
        }
    }
    public void bottomUIChangeMusic(int i) {
        playPosition = i;
        setNowPlaying();
    }

    public void setPlayingAudios() {
        MusicApplication.getInstance().getServiceInterface().setPlayList(getAudioIds(), "PLMusicAdapter");
        Log.d("PLMA", "setPlayingAudios() >> position : "+playPosition);
        //playPosition = MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition();
        Log.d("PLMA", "setPlayingAudios() >> position : "+playPosition);
    }
}
