package musicq.apps.obg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PlayListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        TextView listTitle = (TextView) findViewById(R.id.list_title);
        Intent intent = getIntent();
        String listName = intent.getStringExtra("name");
        int listId = intent.getIntExtra("id", 0);
        String str = listName + " : " + String.valueOf(listId);
        listTitle.setText(str);
    }
}
