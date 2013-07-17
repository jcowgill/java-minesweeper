package uk.ac.york.minesweeper.android;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.ac.york.minesweeper.Minefield;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Class containing methods to manage minefield loading + saving
 */
public class MinefieldManager
{
    private static final String MINEFIELD_FILE = "saved_minefield";

    private Context context;

    /**
     * Creates a new minefield manager using the given context
     *
     * @param context context to use
     */
    public MinefieldManager(Context context)
    {
        this.context = context;
    }

    /**
     * Loads a new minefield based on the current settings
     *
     * @return new minefield
     */
    public ParcelableMinefield loadNew()
    {
        // Obtain settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int width = prefs.getInt(SettingsActivity.PREF_WIDTH, 8);
        int height = prefs.getInt(SettingsActivity.PREF_HEIGHT, 8);
        int mines = prefs.getInt(SettingsActivity.PREF_MINES, 10);

        // Create minefield
        return new ParcelableMinefield(width, height, mines);
    }

    /**
     * Loads a saved minefied from disk
     *
     * @return the minefield or null if none exists
     */
    public ParcelableMinefield loadFromDisk()
    {
        try
        {
            // Open minefield file
            DataInputStream input = new DataInputStream(context.openFileInput(MINEFIELD_FILE));

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
    public boolean saveToDisk(Minefield minefield)
    {
        try
        {
            // Open minefield file
            DataOutputStream output = new DataOutputStream(
                    context.openFileOutput(MINEFIELD_FILE, Context.MODE_PRIVATE));

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
