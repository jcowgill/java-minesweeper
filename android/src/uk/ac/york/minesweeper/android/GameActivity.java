package uk.ac.york.minesweeper.android;

import uk.ac.york.minesweeper.Minefield;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The main game activity
 */
public class GameActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // TODO DEBUG CODE
        ((MinefieldView) findViewById(R.id.minefield_view)).setMinefield(new Minefield(8, 8, 10));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                // Go to settings activity
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Navigate up to the main game activity
     *
     * @param oldActivity old activity
     */
    static void navigateUp(Activity oldActivity)
    {
        // Do this manually - i'm not pulling in the whole support library for this!

        // Create intent
        Intent intent = new Intent(oldActivity, GameActivity.class);

        // Navigate up to that intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            oldActivity.navigateUpTo(intent);
        }
        else
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            oldActivity.startActivity(intent);
            oldActivity.finish();
        }
    }
}
