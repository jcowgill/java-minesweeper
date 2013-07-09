package uk.ac.york.minesweeper.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * A preference presenting a number picker
 */
public class NumberPickerPreference extends DialogPreference
{
    // Default picker value
    private static final int DEFAULT_VALUE = 1;

    // The current value of the preference
    private int currentValue;

    // Minimum and maximum picker parameters
    private int minValue, maxValue;

    // The visible number picker object
    private NumberPicker picker;

    /**
     * Creates a new number picker preference
     *
     * @param context context to create the preference in
     * @param attrs settings affecting the preference
     */
    public NumberPickerPreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Extract message from attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.NumberPickerPreference, 0, 0);

        try
        {
            minValue = a.getInteger(R.styleable.NumberPickerPreference_min, 1);
            maxValue = a.getInteger(R.styleable.NumberPickerPreference_max, 10);
        }
        finally
        {
            a.recycle();
        }
    }

    /**
     * Gets the value of the preference
     *
     * @return the current preference value
     */
    public int getValue()
    {
        return currentValue;
    }

    /**
     * Sets the value of the preference
     *
     * This method has no affect on a visible picker dialog
     *
     * @param value new value
     */
    public void setValue(int value)
    {
        this.currentValue = value;
        persistInt(value);
    }

    /**
     * Gets the minimum picker value
     *
     * @return the minimum picker value
     */
    public int getMin()
    {
        return minValue;
    }

    /**
     * Sets the minimum picker value
     *
     * This method has no affect on a visible picker dialog
     *
     * @param minValue minimum value
     */
    public void setMin(int minValue)
    {
        this.minValue = minValue;
    }

    /**
     * Gets the maximum picker value
     *
     * @return the maximum picker value
     */
    public int getMax()
    {
        return maxValue;
    }

    /**
     * Sets the maximum picker value
     *
     * This method has no affect on a visible picker dialog
     *
     * @param maxValue maximum value
     */
    public void setMax(int maxValue)
    {
        this.maxValue = maxValue;
    }

    @Override
    protected View onCreateDialogView()
    {
        Context context = getContext();
        LayoutParams wrapWidth = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                                  LayoutParams.WRAP_CONTENT);

        // Create layout
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(wrapWidth);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Add message under the title
        TextView messageView = new TextView(context);
        messageView.setId(android.R.id.message);
        messageView.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(messageView);

        // Add the main number picker
        NumberPicker locPicker = new NumberPicker(context);
        locPicker.setMinValue(minValue);
        locPicker.setMaxValue(maxValue);
        locPicker.setValue(currentValue);
        locPicker.setWrapSelectorWheel(false);
        locPicker.setLayoutParams(wrapWidth);
        layout.addView(locPicker);

        picker = locPicker;
        return layout;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        // Save result
        if (positiveResult)
        {
            int newValue = picker.getValue();

            if (callChangeListener(newValue))
                setValue(newValue);
        }

        // Destroy number picker
        picker = null;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue)
    {
        int newValue;

        // Obtain previous value (persisted or default)
        if (restorePersistedValue)
            newValue = getPersistedInt(DEFAULT_VALUE);
        else
            newValue = (Integer) defaultValue;

        // Set it
        setValue(newValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        final Parcelable superState = super.onSaveInstanceState();

        // Don't bother saving if it's persistent
        if (isPersistent())
            return superState;

        // Save the current state
        final SavedState myState = new SavedState(superState);
        myState.currentValue = currentValue;
        myState.minValue = minValue;
        myState.maxValue = maxValue;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (state == null || !state.getClass().equals(SavedState.class))
        {
            super.onRestoreInstanceState(state);
        }
        else
        {
            // Restore my state
            SavedState myState = (SavedState) state;
            super.onRestoreInstanceState(myState.getSuperState());

            currentValue = myState.currentValue;
            minValue = myState.minValue;
            maxValue = myState.maxValue;
        }
    }

    /**
     * Parcelable where the data is stored
     */
    private static class SavedState extends BaseSavedState
    {
        public int currentValue, minValue, maxValue;

        public SavedState(Parcel source)
        {
            super(source);
            currentValue = source.readInt();
            minValue = source.readInt();
            maxValue = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentValue);
            dest.writeInt(minValue);
            dest.writeInt(maxValue);
        }

        public SavedState(Parcelable superState)
        {
            super(superState);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
