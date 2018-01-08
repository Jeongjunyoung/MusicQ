package musicq.apps.obg.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import musicq.apps.obg.R;
import musicq.apps.obg.domain.PlayListVO;

/**
 * Created by d1jun on 2017-12-21.
 */

public class PlayListAdapter extends BaseAdapter {
    private ArrayList<PlayListVO> items = new ArrayList<>();
    private ArrayList<View> convertViewList = new ArrayList<>();
    LayoutInflater inflater;
    public PlayListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public PlayListVO getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = null;
        PlayListViewHolder holder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_playlist, viewGroup, false);
            PlayListVO vo = items.get(i);
            holder = new PlayListViewHolder();
            holder.mLayout = (LinearLayout) view.findViewById(R.id.list_id);
            holder.mText = (TextView) view.findViewById(R.id.playlist_name);
            holder.mLayout.setId(vo.getListId());
            Log.d("name", "" + vo.getListName());
            holder.mText.setTag(vo.getListName());
            holder.mLayout.setTag(i);
            convertViewList.add(view);
            view.setTag(holder);
        } else {
            view = convertView;
        }
        LinearLayout layout = (LinearLayout) view;
        TextView list_name = (TextView) layout.findViewById(R.id.playlist_name);
        LinearLayout list_id = (LinearLayout) layout.findViewById(R.id.list_id);

        list_name.setText(items.get(i).getListName());
        //list_id.setId(items.get(i).getListId());

        return layout;
    }

    public void addItem(PlayListVO vo) {
        items.add(vo);
    }

    public void removeItem(String listName) {
        for(int i=0;i< items.size();i++) {
            if (items.get(i).getListName().equals(listName)) {
                items.remove(i);
                convertViewList.remove(i);
            }
        }
    }
    public void itemClear() {
        items.clear();
    }
    private class PlayListViewHolder{
        public LinearLayout mLayout;
        public TextView mText;
    }

    public PlayListVO getListId(int position) {
        View view;
        //int listId = 0;
        PlayListVO vo = new PlayListVO();
        for(int i=0;i<convertViewList.size();i++) {
            view = convertViewList.get(i);
            PlayListViewHolder holder = (PlayListViewHolder) view.getTag();
            if ((int)holder.mLayout.getTag() == position) {
                vo.setListId(holder.mLayout.getId());
                vo.setListName(holder.mText.getTag().toString());
            }
        }
        //Log.d("listId", "" + listId);
        return vo;
    }
}
