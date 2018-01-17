package musicq.apps.obg.adapter;

import android.content.ContentUris;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import musicq.apps.obg.R;
import musicq.apps.obg.service.MusicApplication;

/**
 * Created by d1jun on 2018-01-11.
 */

public class AlbumPagerAdapter extends PagerAdapter {
    LayoutInflater inflater;
    private ArrayList<Long> mAudioIds;
    //private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
    public AlbumPagerAdapter(LayoutInflater inflater) {
        this.inflater  = inflater;
    }
    @Override
    public int getCount() {
        if (mAudioIds != null) {
            return mAudioIds.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        Log.d("PAGER", "instantiateItem() 호출");
        view = inflater.inflate(R.layout.item_album_pager, null);
        ImageView album = (ImageView) view.findViewById(R.id.album_pager);
        Log.d("PAGER", "AudioIDs : " + mAudioIds.get(position));
        MusicAdapter.AudioItem audioItem = MusicApplication.getInstance().getServiceInterface().getAudioItem();
        if (audioItem != null) {
            Uri albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), audioItem.mAlbumId);
            Glide.with(view.getContext()).load(albumArtUri).error(R.drawable.album_default_icon).into(album);
        }
        //Picasso.with(view.getContext()).load(albumArtUri).error(R.drawable.album_default_icon).fit().into(album);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public void setAudioIds() {
        mAudioIds = MusicApplication.getInstance().getServiceInterface().getAudioIds();
    }
}
