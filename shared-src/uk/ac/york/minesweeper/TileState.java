package uk.ac.york.minesweeper;

/**
 * The state of a tile in the minefield
 */
public enum TileState
{
    /** Tile is covered (with no flags / question) */
    COVERED(0),

    /** Tile is covered with a flag */
    FLAGGED(1),

    /** Tile is covered with a question */
    QUESTION(2),

    /** Tile is uncovered */
    UNCOVERED(3);

    /** Byte associated with the tile state */
    final byte NUMBER;

    private TileState(int num) { NUMBER = (byte) num; }

    /**
     * Reads a tile state from a number
     */
    static TileState fromNumber(byte num)
    {
        switch (num)
        {
            case 0: return COVERED;
            case 1: return FLAGGED;
            case 2: return QUESTION;
            case 3: return UNCOVERED;
        }

        throw new IllegalArgumentException("Invalid tile state code");
    }
}
