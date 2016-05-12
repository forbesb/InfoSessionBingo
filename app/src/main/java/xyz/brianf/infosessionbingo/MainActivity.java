package xyz.brianf.infosessionbingo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "xyz.brianf.infosessionbingo.MESSAGE";
    public final static String BINGO_NUMBER_MESSAGE = "xyz.brianf.infosessionbingo.BINGOSIZE";
    public final static String WIDTH_EXTRA = "xyz.brianf.infosessionbingo.WIDTH";
    public final static String HEIGHT_EXTRA = "xyz.brianf.infosessionbingo.HEIGHT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Open your Bingo Board?", Snackbar.LENGTH_LONG)
                        .setAction("Play", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openBingo(view);
                                }}).show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void openBingo(View view){
        Intent intent = new Intent(this, BingoActivity.class);
        int message = 5;
        intent.putExtra(BINGO_NUMBER_MESSAGE, message);
        int width = view.getWidth();
        int height = view.getHeight();
        intent.putExtra(HEIGHT_EXTRA, height);
        intent.putExtra(WIDTH_EXTRA, width);
        startActivity(intent);
    }
}
