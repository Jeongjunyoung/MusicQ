package musicq.apps.obg;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import musicq.apps.obg.adapter.AddMusicListAdapter;
import musicq.apps.obg.adapter.MusicAdapter;

public class MusiclistForPlaylist extends AppCompatActivity implements View.OnClickListener{
    private static final int LOADER_ID = 0;
    private RecyclerView mRecyclerView;
    private AddMusicListAdapter mAdapter;
    private String listName;
    private int listId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musiclist_for_playlist);
        Intent intent = getIntent();
        listName = intent.getStringExtra("listName");
        mRecyclerView = (RecyclerView) findViewById(R.id.add_music_list);
        mAdapter = new AddMusicListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        Button backMusicList = (Button) findViewById(R.id.cancel_music_list);
        Button addMusic = (Button) findViewById(R.id.list_music_add);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            } else {
                // READ_EXTERNAL_STORAGE 에 대한 권한이 있음.
                getAudioListFromMediaDatabase();
            }
        }
        else{
            getAudioListFromMediaDatabase();
        }
        backMusicList.setOnClickListener(this);
        addMusic.setOnClickListener(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE 에 대한 권한 획득.
            getAudioListFromMediaDatabase();
        }
    }
    private void getAudioListFromMediaDatabase() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = new String[]{
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA
                };
                String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(getApplicationContext(), uri, projection, selection, null, sortOrder);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.swapCursor(data);
            }
            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.swapCursor(null);
            }
        });
    }
    private void insertMusicInPlayList(MusicAdapter.AudioItem item) {
        ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Playlists._ID},
                MediaStore.Audio.Playlists.NAME +"=?",
                new String[]{listName},
                null);
        c.moveToFirst();
        listId = c.getInt(0);
        c.close();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", listId);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, "desc");
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, item.mId);
        resolver.insert(uri, values);
        Log.d("PA", "SUCCESS.... : " + item.mId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_music_list:
                //playListLayout.setEnabled(true);
                //musicListBtn.setVisibility(View.VISIBLE);
                //listLayout.setVisibility(View.GONE);
                finish();
                break;
            case R.id.list_music_add:
                List<MusicAdapter.AudioItem> items = mAdapter.getCheckedIds();
                for(int i=0;i<items.size();i++) {
                    MusicAdapter.AudioItem audioItem = items.get(i);
                    insertMusicInPlayList(audioItem);
                    finish();
                    //Log.d("PA", "ID : " + audioItem.mId);
                    //Log.d("PA", "TITLE : " + audioItem.mTitle);
                    break;
                }
        }
    }
}
