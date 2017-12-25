package musicq.apps.obg.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import musicq.apps.obg.PlayListActivity;
import musicq.apps.obg.R;
import musicq.apps.obg.adapter.PlayListAdapter;
import musicq.apps.obg.database.DBHelper;
import musicq.apps.obg.domain.PlayListVO;

public class PlayListFragment extends Fragment implements View.OnClickListener{
    private DBHelper dbHelper;
    EditText editText;
    LinearLayout layout;
    PlayListAdapter listAdapter;
    public static PlayListFragment newInstance() {
        Bundle args = new Bundle();
        PlayListFragment fragment = new PlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_play_list, container, false);
        FloatingActionButton addList = (FloatingActionButton) rootView.findViewById(R.id.add_playlist);
        Button cancelBtn = (Button) rootView.findViewById(R.id.list_cancel_btn);
        Button listAddBtn = (Button) rootView.findViewById(R.id.list_add_btn);
        layout = (LinearLayout) rootView.findViewById(R.id.playlist_add_layout);
        ListView listView = (ListView) rootView.findViewById(R.id.playlist);
        editText = (EditText) rootView.findViewById(R.id.list_add_name);
        listAdapter = new PlayListAdapter(getActivity());
        dbHelper = new DBHelper(getActivity());
        dbHelper.open();
        ArrayList<PlayListVO> listItems = dbHelper.selectData();
        for(int i=0;i<listItems.size();i++) {
            listAdapter.addItem(listItems.get(i));
        }
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new ListViewItemClickListener());
        addList.setOnClickListener(this);
        listAddBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_playlist:
                layout.setVisibility(View.VISIBLE);
                break;
            case R.id.list_cancel_btn:
                layout.setVisibility(View.GONE);
                break;
            case R.id.list_add_btn:
                dbHelper.insert(editText.getText().toString());
                listAdapter.itemClear();
                ArrayList<PlayListVO> listItems = dbHelper.selectData();
                for(int i=0;i<listItems.size();i++) {
                    listAdapter.addItem(listItems.get(i));
                }
                layout.setVisibility(View.GONE);
                listAdapter.notifyDataSetChanged();
                break;
        }
    }
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            PlayListVO vo = listAdapter.getListId(i);
            Intent intent = new Intent(getActivity(), PlayListActivity.class);
            intent.putExtra("id", vo.getListId());
            intent.putExtra("name", vo.getListName());
            startActivity(intent);
            //Log.d("PLF", "" + vo.getListId());
            //Log.d("PLF", "" + vo.getListName());
        }
    }
}
