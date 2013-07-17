package uk.ac.york.minesweeper.android;

import uk.ac.york.minesweeper.android.ZoomView.SavedInfo;

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
    private static final String ZOOM_STATE_KEY = "ZOOM_STATE_KEY";
    private static final String MINEFIELD_KEY = "MINEFIELD_KEY";

    // View states
    private ZoomView zoomView;
    private MinefieldView minefieldView;

    // Minefield states
    private ParcelableMinefield minefield;
    private MinefieldManager minefieldManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Load views
        zoomView = ((ZoomView) findViewById(R.id.zoom_view));
        minefieldView = ((MinefieldView) findViewById(R.id.minefield_view));

        // Attempt to load minefield
        minefieldManager = new MinefieldManager(this);

        if (savedInstanceState != null)
        {
            // From bundle
            minefield = savedInstanceState.getParcelable(MINEFIELD_KEY);
            minefieldView.setMinefield(minefield);
            zoomView.restoreSavedInfo((SavedInfo) savedInstanceState.getParcelable(ZOOM_STATE_KEY));
        }
        else
        {
            // Try saved file
            minefield = minefieldManager.loadFromDisk();

            if (minefield == null)
            {
                // Restart game
                minefield = minefieldManager.loadNew();
            }

            // Reset views
            minefieldView.setMinefield(minefield);
        }
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

    @Override
    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);

        // Save zoom state and current minefield
        bundle.putParcelable(ZOOM_STATE_KEY, new ZoomView.SavedInfo(zoomView));
        bundle.putParcelable(MINEFIELD_KEY, minefield);
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
