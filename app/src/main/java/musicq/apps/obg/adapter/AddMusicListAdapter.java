package musicq.apps.obg.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import musicq.apps.obg.R;

/**
 * Created by d1jun on 2017-12-27.
 */

public class AddMusicListAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private ArrayList<AudioViewHolder> viewList = new ArrayList<>();
    public AddMusicListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        MusicAdapter.AudioItem audioItem = MusicAdapter.AudioItem.bindCursor(cursor);
        if (audioItem != null) {
            ((AudioViewHolder) viewHolder).setAudioItem(audioItem,cursor.getPosition());
            //viewList.clear();
            boolean isDup = false;
            for(int i=0;i<viewList.size();i++) {
                if (viewList.get(i).mItem.mId == ((AudioViewHolder) viewHolder).mItem.mId) {
                    //Log.d("ADDMA","onBindViewHolder() >> find");
                    isDup = true;
                }
            }
            if (!isDup) {
                viewList.add((AudioViewHolder) viewHolder);
            }
            //Log.d("ADDMA","onBindViewHolder() >> viewList.add() Call");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_musiclist, parent, false);
        return new AudioViewHolder(view);
    }

    private class AudioViewHolder extends RecyclerView.ViewHolder{
        private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        //private ImageView mImgAlbumArt;
        private TextView mTitle;
        private TextView mArtist;
        private CheckBox mCheck;
        //private TextView mTxtDuration;
        private MusicAdapter.AudioItem mItem;
        //private int mPosition;

        private AudioViewHolder(View view) {
            super(view);
            //mImgAlbumArt = (ImageView) view.findViewById(R.id.album_image);
            mTitle = (TextView) view.findViewById(R.id.add_title);
            mArtist = (TextView) view.findViewById(R.id.add_artist);
            mCheck = (CheckBox) view.findViewById(R.id.add_checkbox);
            //mTxtDuration = (TextView) view.findViewById(R.id.txt_duration);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MusicApplication.getInstance().getServiceInterface().setPlayList(getAudioIds()); // 재생목록등록
                    //MusicApplication.getInstance().getServiceInterface().play(mPosition); // 선택한 오디오재생
                    if (mCheck.isChecked()) {
                        mCheck.setChecked(false);
                    } else {
                        mCheck.setChecked(true);
                    }
                }
            });
        }
        public void setAudioItem(MusicAdapter.AudioItem item, int position) {
            mItem = item;
            mTitle.setText(item.mTitle);
            mArtist.setText(item.mArtist);
            mTitle.setTag(item);
            //mArtist.setText(item.mArtist + "(" + item.mAlbum + ")");
            //mTxtDuration.setText(DateFormat.format("mm:ss", item.mDuration));
            //Uri albumArtUri = ContentUris.withAppendedId(artworkUri, item.mAlbumId);
            //Picasso.with(itemView.getContext()).load(albumArtUri).error(R.drawable.album_default_icon).into(mImgAlbumArt);
        }
    }

    public List<MusicAdapter.AudioItem> getCheckedIds() {
        AudioViewHolder audioViewHolder;
        ArrayList<MusicAdapter.AudioItem> items = new ArrayList<>();
        for(int i=0; i<viewList.size(); i++) {
            audioViewHolder = (AudioViewHolder) viewList.get(i);
            if (audioViewHolder.mCheck.isChecked()) {
                items.add((MusicAdapter.AudioItem)audioViewHolder.mTitle.getTag()); //= (MusicAdapter.AudioItem) audioViewHolder.mTitle.getTag();
                //checkIds.add(items.mId);
            }
        }
        //Long[] arrLong = (Long[]) checkIds.toArdray(new Long[0]);
        return  items;
    }
}
