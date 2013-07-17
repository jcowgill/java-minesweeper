package uk.ac.york.minesweeper.android;

import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

/**
 * The game settings activity
 */
public class SettingsActivity extends Activity
{
    // Preference strings
    public static final String PREF_DIFFICULTY = "pref_difficulty";
    public static final String PREF_WIDTH = "pref_width";
    public static final String PREF_HEIGHT = "pref_height";
    public static final String PREF_MINES = "pref_mines";
    public static final String PREF_ENABLE_QUESTIONS = "pref_enable_questions";
    public static final String PREF_TAP_FLAG = "pref_tap_flag";

    // Difficulty levels
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;
    public static final int DIFFICULTY_CUSTOM = 3;

    // Preference objects
    private ListPreference prefDifficulty;
    private NumberPickerPreference prefWidth;
    private NumberPickerPreference prefHeight;
    private NumberPickerPreference prefMines;

    private SettingsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Enable up button on action bar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the settings fragment
        fragment = new SettingsFragment();

        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, fragment)
            .commit();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Find preferences
        prefDifficulty = (ListPreference) fragment.findPreference(PREF_DIFFICULTY);
        prefWidth = (NumberPickerPreference) fragment.findPreference(PREF_WIDTH);
        prefHeight = (NumberPickerPreference) fragment.findPreference(PREF_HEIGHT);
        prefMines = (NumberPickerPreference) fragment.findPreference(PREF_MINES);

        // Register on change listeners
        prefDifficulty.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                updateDifficulty((String) newValue);
                return true;
            }
        });

        prefWidth.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                updateDimensions((Integer) newValue, prefHeight.getValue());
                return true;
            }
        });

        prefHeight.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                updateDimensions(prefWidth.getValue(), (Integer) newValue);
                return true;
            }
        });

        prefMines.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
        {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                updateMines((Integer) newValue);
                return true;
            }
        });

        // Update everything now
        updateDifficulty(prefDifficulty.getValue());
        updateDimensions(prefWidth.getValue(), prefHeight.getValue());
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Unregister preference change listeners
        prefDifficulty.setOnPreferenceChangeListener(null);
        prefWidth.setOnPreferenceChangeListener(null);
        prefHeight.setOnPreferenceChangeListener(null);
        prefMines.setOnPreferenceChangeListener(null);
    }

    /**
     * Updates the difficulty preference
     */
    private void updateDifficulty(String valueStr)
    {
        int valueCode = Integer.parseInt(valueStr);

        // Update summary first
        prefDifficulty.setSummary(prefDifficulty.getEntries()[valueCode]);

        // Unlock size prefs
        prefWidth.setEnabled(true);
        prefHeight.setEnabled(true);
        prefMines.setEnabled(true);

        // Calculate new dimensions
        int width, height, mines;

        switch (valueCode)
        {
            case DIFFICULTY_EASY:
                width = 8;
                height = 8;
                mines = 10;
                break;

            case DIFFICULTY_MEDIUM:
                width = 16;
                height = 16;
                mines = 40;
                break;

            case DIFFICULTY_HARD:
                width = 30;
                height = 16;
                mines = 99;
                break;

            default:
                // Custom - return here leaving preferences unlocked
                return;
        }

        // Update dimensions
        prefWidth.setValue(width);
        prefHeight.setValue(height);
        prefMines.setValue(mines);

        // Update summaries
        updateDimensions(width, height);

        // Lock size prefs
        prefWidth.setEnabled(false);
        prefHeight.setEnabled(false);
        prefMines.setEnabled(false);
    }

    /**
     * Updates the dimension (width + height) preferences
     */
    private void updateDimensions(int width, int height)
    {
        // Update summaries
        prefWidth.setSummary(Integer.toString(width));
        prefHeight.setSummary(Integer.toString(height));

        // Update max of the mines preference
        prefMines.setMax(width * height - 10);
        updateMines(prefMines.getValue());
    }

    /**
     * Updates the summary for the mines preference
     *
     * @param mines new number of mines
     */
    private void updateMines(int mines)
    {
        prefMines.setSummary(Integer.toString(mines));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                // Go back to game activity
                GameActivity.navigateUp(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * The settings fragment displaying all the preferences
     */
    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            // Load preferences into fragment
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
