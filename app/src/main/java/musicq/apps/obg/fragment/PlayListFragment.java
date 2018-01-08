package musicq.apps.obg.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import musicq.apps.obg.MusiclistForPlaylist;
import musicq.apps.obg.PlayListActivity;
import musicq.apps.obg.R;
import musicq.apps.obg.adapter.PLMusicListAdapter;
import musicq.apps.obg.adapter.PlayListAdapter;
import musicq.apps.obg.database.DBHelper;
import musicq.apps.obg.domain.PlayListVO;

public class PlayListFragment extends Fragment implements View.OnClickListener{
    private static final String PLAYLIST_DELETE = "PLAYLIST_DELETE";
    private static final String PLAYLIST_UPDATE = "PLAYLIST_UPDATE";
    private DBHelper dbHelper;
    EditText editText;
    LinearLayout layout;
    PlayListAdapter listAdapter;
    private RecyclerView mRecyclerViewMusic;
    private PLMusicListAdapter mAdapterMusic;
    FloatingActionButton musicListBtn;
    FloatingActionButton addList;
    LinearLayout listLayout;
    LinearLayout playListLayout;
    private String listName;
    private int longPosition;
    public static PlayListFragment newInstance() {
        Bundle args = new Bundle();
        PlayListFragment fragment = new PlayListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_play_list, container, false);
        addList = (FloatingActionButton) rootView.findViewById(R.id.add_playlist);
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
        listView.setOnItemLongClickListener(new ListViewItemLongClickListener());
        //String str = listName + " : " + String.valueOf(mId);
        //listTitle.setText(str);
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
                testListNames();
                break;
            case R.id.list_cancel_btn:
                layout.setVisibility(View.GONE);
                break;
            case R.id.list_add_btn:
                String name = editText.getText().toString();
                dbHelper.insert(name);
                listAdapter.itemClear();
                ArrayList<PlayListVO> listItems = dbHelper.selectData();
                for(int i=0;i<listItems.size();i++) {
                    listAdapter.addItem(listItems.get(i));
                }
                insertPlaylist(name);
                layout.setVisibility(View.GONE);
                listAdapter.notifyDataSetChanged();
                break;
        }
    }
    private class ListViewItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //PlayListVO vo = listAdapter.getListId(i);
            String name = listAdapter.getItem(i).getListName();
            Log.d("PLF", "onItemClick() >> listName : " + name);
            Intent intent = new Intent(getActivity(), PlayListActivity.class);
            //intent.putExtra("id", vo.getListId());
            intent.putExtra("name", name);
            startActivity(intent);
            //Log.d("PLF", "" + vo.getListId());
            //Log.d("PLF", "" + vo.getListName());
        }
    }
    private class ListViewItemLongClickListener implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            PopupMenu popup = new PopupMenu(getActivity(), view);
            listName = listAdapter.getItem(i).getListName();
            getActivity().getMenuInflater().inflate(R.menu.menu_playlist_popup, popup.getMenu());
            popup.setOnMenuItemClickListener(listener);
            popup.show();
            return true;
        }
    }
    public void insertPlaylist(String name) {
        Uri uri;
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor c = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{name},
                null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            c.close();
            return;
        }
        c.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, name);
        resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
    }

    PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener(){

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.playlist_delete:
                    //Toast.makeText(getActivity(),"delete",Toast.LENGTH_SHORT).show();
                    ListManagementUD(PLAYLIST_DELETE);
                    listAdapter.removeItem(listName);
                    listAdapter.notifyDataSetChanged();
                    break;
                case R.id.playlist_update:
                    Toast.makeText(getActivity(),"update",Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    };

    private void ListManagementUD(String type) {
        ContentResolver resolver = getActivity().getContentResolver();

        //ListName 중복 체크
        /*Cursor c = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[]{listName},
                null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            c.close();
            return;
        }
        c.close();*/

        Cursor c1 = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME +"=?",
                new String[]{listName},
                null);
        c1.moveToFirst();
        int listId = c1.getInt(0);
        c1.close();
        if (type.equals(PLAYLIST_DELETE)) {
            resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    MediaStore.Audio.Playlists._ID + " = " + listId, null);
            dbHelper.remove(listName);
        }
    }

    public void testListNames() {
        Log.d("NAMES", "testListNames()");
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor c = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists.NAME},
                null,
                null,
                null);
        //c.moveToFirst();
        Log.d("NAMES", "Cursor().getCount() : " + c.getCount());
        if (!c.isAfterLast()) {
            Toast.makeText(getActivity().getApplicationContext(),"없음",Toast.LENGTH_SHORT).show();
            c.close();
            return;
        }
        ArrayList<String> names = new ArrayList<>();
        for(int i = 0;i<c.getCount(); i++) {
            c.moveToNext();
            names.add(c.getString(i));
            Log.d("NAMES", "ADD");
        }
        c.close();
        for(int i=0;i<names.size();i++) {
            Log.d("NAMES", "listName : " + names.get(i));
        }
    }
}
