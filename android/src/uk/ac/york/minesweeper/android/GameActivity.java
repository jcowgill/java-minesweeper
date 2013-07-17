package uk.ac.york.minesweeper.android;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.york.minesweeper.Minefield;
import uk.ac.york.minesweeper.android.ZoomView.SavedInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The main game activity
 */
public class GameActivity extends Activity
{
    private static final String MINEFIELD_FILE = "saved_minefield";

    private static final String ZOOM_STATE_KEY = "ZOOM_STATE_KEY";
    private static final String MINEFIELD_KEY = "MINEFIELD_KEY";

    // Cached preferences
    private int width, height, mines;
    private boolean enableQuestions, tapFlag;

    // View states
    private ZoomView zoomView;
    private MinefieldView minefieldView;

    // Current minefield
    private ParcelableMinefield minefield;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Load layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Load views
        zoomView = ((ZoomView) findViewById(R.id.zoom_view));
        minefieldView = ((MinefieldView) findViewById(R.id.minefield_view));

        // Load any saved minefields
        if (savedInstanceState != null)
        {
            // From bundle
            minefield = savedInstanceState.getParcelable(MINEFIELD_KEY);
            minefieldView.setMinefield(minefield);
            zoomView.restoreSavedInfo((SavedInfo) savedInstanceState.getParcelable(ZOOM_STATE_KEY));
        }
        else
        {
            // Try from disk
            minefieldView.setMinefield(loadMinefieldFromDisk());
        }

        // Reload prefs
        reloadPreferences();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Reload prefs (after displaying settings activity)
        reloadPreferences();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        // Save current minefield to disk IF RUNNING
        saveMinefieldToDisk(minefield);
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
            case R.id.action_new_game:
                // Start new game
                minefield = null;
                reloadPreferences();
                return true;

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
     * Reloads the preferences and (possibly) loads a new minefield
     */
    private void reloadPreferences()
    {
        // Load new preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        width = prefs.getInt(SettingsActivity.PREF_WIDTH, 8);
        height = prefs.getInt(SettingsActivity.PREF_HEIGHT, 8);
        mines = prefs.getInt(SettingsActivity.PREF_MINES, 10);
        enableQuestions = prefs.getBoolean(SettingsActivity.PREF_ENABLE_QUESTIONS, false);
        tapFlag = prefs.getBoolean(SettingsActivity.PREF_TAP_FLAG, false);

        // Load a new minefield if there is none or if the settings were changed
        if (minefield == null ||
                minefield.getWidth() != width ||
                minefield.getHeight() != height ||
                minefield.getMines() != mines)
        {
            // Change stored minefield
            minefield = new ParcelableMinefield(width, height, mines);
            minefieldView.setMinefield(minefield);
        }

        // Set extra options
        minefieldView.setQuestionsEnabled(enableQuestions);
        minefieldView.setSingleTapFlag(tapFlag);
    }

    /**
     * Loads a saved minefied from disk
     *
     * @return the minefield or null if none exists
     */
    private ParcelableMinefield loadMinefieldFromDisk()
    {
        try
        {
            // Open minefield file
            DataInputStream input = new DataInputStream(openFileInput(MINEFIELD_FILE));

            try
            {
                // Read data into new minefield
                return new ParcelableMinefield(input);
            }
            finally
            {
                input.close();
            }
        }
        catch (FileNotFoundException e)
        {
            // Standard error - do not log
            return null;
        }
        catch (IOException e)
        {
            // Log exception and return failure
            Log.e("MinefieldManager", "Error reading minefield", e);
            return null;
        }
    }

    /**
     * Saves a minefield to disk, overwriting any existing minefield
     *
     * @param minefield minefield to save
     * @return false if there was an error saving to disk
     */
    private boolean saveMinefieldToDisk(Minefield minefield)
    {
        try
        {
            // Open minefield file
            DataOutputStream output = new DataOutputStream(
                    openFileOutput(MINEFIELD_FILE, Context.MODE_PRIVATE));

            try
            {
                // Write data to file
                minefield.save(output);
                return true;
            }
            finally
            {
                output.close();
            }
        }
        catch (IOException e)
        {
            // Log exception and return failure
            Log.e("MinefieldManager", "Error writing minefield", e);
            return false;
        }
    }
}
