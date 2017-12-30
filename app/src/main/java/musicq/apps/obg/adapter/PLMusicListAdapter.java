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
    private ArrayList<PLMusicListAdapter.AudioViewHolder> viewList = new ArrayList<>();
    public PLMusicListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        PLMusicListAdapter.AudioItemList audioItem = PLMusicListAdapter.AudioItemList.bindCursor(cursor);
        if (audioItem != null) {
            ((PLMusicListAdapter.AudioViewHolder) viewHolder).setAudioItem(audioItem, cursor.getPosition());
            viewList.add((PLMusicListAdapter.AudioViewHolder) viewHolder);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new PLMusicListAdapter.AudioViewHolder(v);
    }
    public ArrayList<Long> getAudioIds() {
        count = getItemCount();
        ArrayList<Long> audioIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            audioIds.add(viewList.get(i).mItem.mId);
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

        public static PLMusicListAdapter.AudioItemList bindCursor(Cursor cursor) {
            PLMusicListAdapter.AudioItemList audioItem = new PLMusicListAdapter.AudioItemList();
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
        //private TextView mTxtDuration;
        private PLMusicListAdapter.AudioItemList mItem;
        private int mPosition;
        private AudioViewHolder(View view) {
            super(view);
            mImgAlbumArt = (ImageView) view.findViewById(R.id.album_image);
            mTitle = (TextView) view.findViewById(R.id.music_title);
            mArtist = (TextView) view.findViewById(R.id.music_artist);
            //mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ID", "" + mPosition);
                    MusicApplication.getInstance().getServiceInterface().setPlayList(getAudioIds(), "PLMusicAdapter"); // 재생목록등록
                    MusicApplication.getInstance().getServiceInterface().play(mPosition); // 선택한 오디오재생
                    playPosition = mPosition;
                    setNowPlaying(playPosition);
                }
            });
        }

        public void setAudioItem(PLMusicListAdapter.AudioItemList item, int position) {
            mItem = item;
            mPosition = position;
            if (playPosition == mPosition) {
                mTitle.setTextColor(Color.parseColor("#e49292"));
            }
            mTitle.setText(item.mTitle);
            mArtist.setText(item.mArtist);
            //mArtist.setText(item.mArtist + "(" + item.mAlbum + ")");
            //mTxtDuration.setText(DateFormat.format("mm:ss", item.mDuration));
            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.mAlbumId);
            Picasso.with(itemView.getContext()).load(albumArtUri).error(R.drawable.album_default_icon).into(mImgAlbumArt);
        }
    }
    public void setNowPlaying(int position) {
        PLMusicListAdapter.AudioViewHolder audioViewHolder;
        Log.d("SNP", "position : " + position);
        for(int i=0;i<viewList.size();i++) {
            audioViewHolder = (PLMusicListAdapter.AudioViewHolder) viewList.get(i);
            if (i == position) {
                audioViewHolder.mTitle.setTextColor(Color.parseColor("#e49292"));
            } else {
                audioViewHolder.mTitle.setTextColor(Color.parseColor("#464646"));
            }
        }
    }
    public void bottomUIChangeMusic(int i) {
        playPosition = i;
        setNowPlaying(i);
    }
}
