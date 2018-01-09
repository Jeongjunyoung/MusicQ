package musicq.apps.obg.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import musicq.apps.obg.R;
import musicq.apps.obg.domain.Top100VO;

/**
 * Created by d1jun on 2018-01-05.
 */

public class Top100Adapter extends BaseAdapter {
    private ArrayList<Top100VO> items = new ArrayList<>();
    private ArrayList<View> convertViewList = new ArrayList<>();
    LayoutInflater inflater;

    public Top100Adapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addItem(Top100VO vo) {
        items.add(vo);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = null;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            Top100VO vo = items.get(i);
            view = inflater.inflate(R.layout.item_top100, viewGroup, false);
            holder.mRank = (TextView) view.findViewById(R.id.top100_rank);
            holder.mTitle = (TextView) view.findViewById(R.id.top100_title);
            holder.mTitle.setTag(i);
            convertViewList.add(view);
            view.setTag(holder);
            //Top100VO vo = items.get(i);

        } else {
            view = convertView;
        }
        LinearLayout layout = (LinearLayout) view;
        TextView rank = (TextView) layout.findViewById(R.id.top100_rank);
        TextView title = (TextView) layout.findViewById(R.id.top100_title);

        rank.setText(String.valueOf(items.get(i).getRank()));
        //rank.setTag(items.get(i).getVideoId());
        title.setText(items.get(i).getTitle());
        return layout;
    }

    public String getVideoId(int position) {
        return items.get(position).getVideoId();
    }

    public ArrayList<Top100VO> getLists() {
        return items;
    }

    private class ViewHolder{
        public TextView mRank;
        public TextView mTitle;
    }

    public void setPlayingTitle(int position) {
        View view;
        Top100VO vo = new Top100VO();
        for(int i=0;i<convertViewList.size();i++) {
            view = convertViewList.get(i);
            ViewHolder holder = (ViewHolder) view.getTag();
            if ((int)holder.mTitle.getTag() == position) {
                holder.mTitle.setTextColor(Color.parseColor("#e62c73"));
            } else
                holder.mTitle.setTextColor(Color.parseColor("#000000"));
        }
    }
}
