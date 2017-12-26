package musicq.apps.obg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

public class PlayListActivity extends AppCompatActivity implements View.OnClickListener{
    FloatingActionButton musicListBtn;
    LinearLayout listLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        TextView listTitle = (TextView) findViewById(R.id.list_title);
        musicListBtn = (FloatingActionButton) findViewById(R.id.add_music_playlist);
        listLayout = (LinearLayout) findViewById(R.id.music_list_layout);
        Intent intent = getIntent();
        String listName = intent.getStringExtra("name");
        int listId = intent.getIntExtra("id", 0);
        String str = listName + " : " + String.valueOf(listId);
        listTitle.setText(str);
        musicListBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_music_playlist:
                musicListBtn.setVisibility(View.GONE);
                listLayout.setVisibility(View.VISIBLE);
                break;
        }
    }
}
