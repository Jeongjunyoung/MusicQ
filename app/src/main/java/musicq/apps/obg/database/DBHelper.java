package musicq.apps.obg.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import musicq.apps.obg.adapter.MusicAdapter;
import musicq.apps.obg.domain.PlayListVO;

/**
 * Created by d1jun on 2017-12-20.
 */

public class DBHelper {
    private static final String DATABASE_NAME = "playlist.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase db;
    private DatabaseHelper mDBHelper;
    private Context mCtx;
    private static DBHelper instance = null;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }
    public DBHelper(){}
    private class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            //sqLiteDatabase.execSQL("create table oneline_memo(_id integer primary key AUTOINCREMENT, content text, date text, res_id integer)");
            sqLiteDatabase.execSQL("create table playlist(_id integer primary key AUTOINCREMENT, list_name text)");
            sqLiteDatabase.execSQL("create table list_music(music_id long, " +
                    "list_id integer, " +
                    "music_title text," +
                    "music_artist text, music_albumid long,music_album text, music_duration long, music_datapath text," +
                    "constraint list_id_fk foreign key(list_id) references playlist(_id))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
    public DBHelper(Context context) {
        this.mCtx = context;
    }
    public DBHelper open() throws SQLException {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        db = mDBHelper.getWritableDatabase();
        return this;
    }

    public SQLiteDatabase getDataBaseHelper() {
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        return mDBHelper.getWritableDatabase();
    }
    public void insert(String listName) {
        String sql = "insert into playlist(list_name) values(?)";
        Object[] params = {listName};
        db.execSQL(sql, params);
        //Log.d("OBG", "INSERT SUCCESS");
    }

    public void remove(String listName) {
        String sql = "delete from playlist where list_name = ?";
        Object[] params = {listName};
        db.execSQL(sql, params);
    }
    public void playListMusicInsert(int listId, MusicAdapter.AudioItem items) {
        /*"music_title text, music_id long," +
                "music_artist text, music_albumid long,music_album text, music_duration long, music_datapath text,*/
        String sql = "insert into list_music(list_id, music_title, music_id, music_artist, " +
                "music_albumid, music_album, music_duration, music_datapath) values(?,?,?,?,?,?,?,?)";
        Object[] params = {listId, items.mTitle, items.mId, items.mArtist, items.mAlbumId, items.mAlbum, items.mDuration, items.mDataPath};
        db.execSQL(sql, params);
    }
    public void delete(int id) {
        //String sql = "delete from oneline_memo where _id="+id;
        //db.execSQL(sql);
    }

    public ArrayList<PlayListVO> selectData() {
        ArrayList<PlayListVO> list = new ArrayList<>();
        PlayListVO vo = null;
        String sql = "select _id, list_name from playlist";
        Cursor cursor =  db.rawQuery(sql, null);
        for(int i = 0;i<cursor.getCount(); i++) {
            cursor.moveToNext();
            vo = new PlayListVO();
            vo.setListId(cursor.getInt(0));
            vo.setListName(cursor.getString(1));
            list.add(vo);
        }
        cursor.close();
        return list;
    }
}
