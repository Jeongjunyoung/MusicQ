package musicq.apps.obg.adapter;

import android.annotation.SuppressLint;
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


public class MusicAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder>{
    private static int playPosition;
    private ArrayList<View> viewList = new ArrayList<>();
    //private AsyncBitmapLoader mAsyncBitmapLoader;
    public MusicAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        AudioItem audioItem = AudioItem.bindCursor(cursor);
        if (audioItem != null) {
            ((AudioViewHolder) viewHolder).setAudioItem(audioItem, cursor.getPosition(), viewHolder.getAdapterPosition());
            /*boolean isDup = false;
            for(int i=0;i<viewList.size();i++) {
                if (viewList.get(i).mItem.mId == ((AudioViewHolder) viewHolder).mItem.mId) {
                    //Log.d("ADDMA","onBindViewHolder() >> find");
                    isDup = true;
                }
                if (!isDup) {
                    viewList.add((AudioViewHolder) viewHolder);
                }
            }*/
            //viewList.add((AudioViewHolder) viewHolder);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        /*mImgAlbumArt = (ImageView) view.findViewById(R.id.album_image);
        mTitle = (TextView) view.findViewById(R.id.music_title);
        mArtist = (TextView) view.findViewById(R.id.music_artist);*/
        AudioViewHolder audioViewHolder = new AudioViewHolder(v);
        audioViewHolder.mImgAlbumArt = (ImageView) v.findViewById(R.id.album_image);
        audioViewHolder.mTitle = (TextView) v.findViewById(R.id.music_title);
        audioViewHolder.mArtist = (TextView) v.findViewById(R.id.music_artist);
        audioViewHolder.mIsPlaying = (ImageView) v.findViewById(R.id.is_playing);
        v.setTag(audioViewHolder);
        viewList.add(v);
        return audioViewHolder;
    }
    public static class AudioItem {
        public long mId; // 오디오 고유 ID
        public long mAlbumId; // 오디오 앨범아트 ID
        public String mTitle; // 타이틀 정보
        public String mArtist; // 아티스트 정보
        public String mAlbum; // 앨범 정보
        public long mDuration; // 재생시간
        public String mDataPath; // 실제 데이터위치

        public static AudioItem bindCursor(Cursor cursor) {
            AudioItem audioItem = new AudioItem();
            audioItem.mId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID ));
            audioItem.mAlbumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            audioItem.mTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            audioItem.mArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            audioItem.mAlbum = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
            audioItem.mDuration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
            audioItem.mDataPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            return audioItem;
        }
    }
    public ArrayList<Long> getAudioIds() {
        int count = getItemCount();
        ArrayList<Long> audioIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            audioIds.add(getItemId(i));
        }
        return audioIds;
    }
    private class AudioViewHolder extends RecyclerView.ViewHolder {
            private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
            private ImageView mImgAlbumArt;
            private TextView mTitle;
            private TextView mArtist;
            private ImageView mIsPlaying;
            //private TextView mTxtDuration;
            private AudioItem mItem;
            private int mPosition;
            private AudioViewHolder(View view) {
                super(view);
                /*mImgAlbumArt = (ImageView) view.findViewById(R.id.album_image);
                mTitle = (TextView) view.findViewById(R.id.music_title);
                mArtist = (TextView) view.findViewById(R.id.music_artist);
                mIsPlaying = (ImageView) view.findViewById(R.id.is_playing);*/
                //mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("ID", "" + mPosition);
                        MusicApplication.getInstance().getServiceInterface().setPlayList(getAudioIds(), "SongAdapter"); // 재생목록등록
                        MusicApplication.getInstance().getServiceInterface().play(mPosition); // 선택한 오디오재생
                        playPosition = mPosition;
                        /*mTitle.setTextColor(Color.parseColor("#e49292"));
                        mIsPlaying.setVisibility(View.VISIBLE);*/
                        //notifyDataSetChanged();
                        Log.d("MA", "CLICK MUSIC");
                        setNowPlaying();
                    }
                });
            }

        @SuppressLint("ResourceAsColor")
        public void setAudioItem(AudioItem item, int position, int adapterPosition) {
            mItem = item;
            mPosition = position;
            Log.d("MAdapter", "playPosition : " + position);
            Log.d("MAdapter", "adapterPosition : " + adapterPosition);
            Log.d("MAdapter", "MusicApplication : " + MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition());
            mTitle.setText(item.mTitle);
            mTitle.setTag(item.mId);
            mArtist.setText(item.mArtist);
            mArtist.setTag(position);
            mTitle.setTextColor(Color.parseColor("#464646"));
            mIsPlaying.setImageResource(R.drawable.isplaying_icon);
            mIsPlaying.setVisibility(View.GONE);
            if (item.mId == getAudioIds().get(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition())) {
                //if(MusicApplication.getInstance().getServiceInterface().isPlaying()){
                //Log.d("MAdapter", "TEXTTEXT : " + item.mId);
                //Log.d("MAdapter", "mID : " + item.mId + "getAudioIds() : " + getAudioIds().get(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition()));
                //mIsPlaying.setVisibility(View.VISIBLE);
                //mIsPlaying.setImageResource(R.drawable.isplaying_icon);
                mTitle.setTextColor(Color.parseColor("#e49292"));
                mIsPlaying.setVisibility(View.VISIBLE);
                //mTitle.setBackgroundColor(R.color.colorPrimaryDark);
                //setNowPlaying(playPosition);
                //}
            }
            //mArtist.setText(item.mArtist + "(" + item.mAlbum + ")");
            //mTxtDuration.setText(DateFormat.format("mm:ss", item.mDuration));

            Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.mAlbumId);
            //Picasso.with(itemView.getContext()).load(albumArtUri).error(R.drawable.album_default_icon).fit().into(mImgAlbumArt);
            //mImgAlbumArt.setImageResource(R.drawable.album_default_icon);
            Glide.with(itemView.getContext()).load(albumArtUri).error(R.drawable.album_default_icon).into(mImgAlbumArt);
        }
    }
    public void setNowPlaying() {
        //notifyDataSetChanged();
        //AudioViewHolder audioViewHolder;
        //Log.d("SNP", "position : " + position);
        /*for(int i=0;i<viewList.size();i++) {
            audioViewHolder = (AudioViewHolder) viewList.get(i);
            Log.d("MAdapter", "getTag() : " + audioViewHolder.mTitle.getTag());
            Log.d("MAdapter", "getAudioIds() : " + getAudioIds().get(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition()));
            if (audioViewHolder.mTitle.getTag() == getAudioIds().get(MusicApplication.getInstance().getServiceInterface().getNowPlayingPosition())) {
                Log.d("SNP", "i == position : " + position);
                audioViewHolder.mTitle.setTextColor(Color.parseColor("#e49292"));
                //audioViewHolder.mIsPlaying.setImageResource(R.drawable.isplaying_icon);
            } else {
                audioViewHolder.mTitle.setTextColor(Color.parseColor("#464646"));
            }
        }*/
        View view = null;
        for(int i=0; i<viewList.size();i++) {
            view = viewList.get(i);
            AudioViewHolder holder = (AudioViewHolder) view.getTag();
            if ((int)holder.mArtist.getTag() == playPosition){
                //Log.d("MAdatper", "viewHolder : " + holder.mTitle.getText());
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
}
