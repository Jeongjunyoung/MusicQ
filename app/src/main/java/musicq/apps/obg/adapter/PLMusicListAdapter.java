package musicq.apps.obg.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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
    private ArrayList<PLMusicListAdapter.AudioViewHolder> viewList = new ArrayList<>();
    public PLMusicListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        MusicAdapter.AudioItem audioItem = MusicAdapter.AudioItem.bindCursor(cursor);
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
        //private TextView mTxtDuration;
        private MusicAdapter.AudioItem mItem;
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
                    MusicApplication.getInstance().getServiceInterface().setPlayList(getAudioIds()); // 재생목록등록
                    MusicApplication.getInstance().getServiceInterface().play(mPosition); // 선택한 오디오재생
                    playPosition = mPosition;
                }
            });
        }

        public void setAudioItem(MusicAdapter.AudioItem item, int position) {
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
        for(int i=0;i<viewList.size();i++) {
            audioViewHolder = (PLMusicListAdapter.AudioViewHolder) viewList.get(i);
            if (i == position) {
                audioViewHolder.mTitle.setTextColor(Color.parseColor("#e49292"));
            } else {
                audioViewHolder.mTitle.setTextColor(Color.parseColor("#464646"));
            }
        }
    }
}
