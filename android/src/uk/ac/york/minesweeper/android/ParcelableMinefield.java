package uk.ac.york.minesweeper.android;

import java.io.DataInput;
import java.io.IOException;

import uk.ac.york.minesweeper.Minefield;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A minefield which can be saved to a parcel
 */
public class ParcelableMinefield extends Minefield implements Parcelable
{
    /**
     * Creates a new parcelable minefield
     *
     * @param width width of the minefield in tiles
     * @param height height of the minefield in tiles
     * @param mines number of mines
     */
    public ParcelableMinefield(int width, int height, int mines)
    {
        super(width, height, mines);
    }

    /**
     * Creates a new parcelable minefield from a data stream
     *
     * @param in input data stream
     */
    public ParcelableMinefield(DataInput in) throws IOException
    {
        super(in);
    }

    /**
     * Creates a new parcelable minefield from a parcel
     *
     * @param in input parcel
     */
    public ParcelableMinefield(Parcel in) throws IOException
    {
        super(new DataParcelStream(in));
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        try
        {
            save(new DataParcelStream(out));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static final Parcelable.Creator<ParcelableMinefield> CREATOR =
            new Creator<ParcelableMinefield>()
            {
                @Override
                public ParcelableMinefield[] newArray(int size)
                {
                    return new ParcelableMinefield[size];
                }

                @Override
                public ParcelableMinefield createFromParcel(Parcel source)
                {
                    try
                    {
                        return new ParcelableMinefield(source);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            };
}
