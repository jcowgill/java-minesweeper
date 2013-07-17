package uk.ac.york.minesweeper;

/**
 * The state of the game itself
 */
public enum GameState
{
    /** The first tile has not been clicked yet */
    NOT_STARTED(0),

    /** The game is in progress */
    RUNNING(1),

    /** The game has finished and has been won (uncovered all non-mines) */
    WON(2),

    /** The game has been lost (clicked a mine) */
    LOST(3);

    /** Byte associated with the game state */
    final byte NUMBER;

    private GameState(int num) { NUMBER = (byte) num; }

    /**
     * Reads a game state from a number
     */
    static GameState fromNumber(byte num)
    {
        switch (num)
        {
            case 0: return NOT_STARTED;
            case 1: return RUNNING;
            case 2: return WON;
            case 3: return LOST;
        }

        throw new IllegalArgumentException("Invalid game state code");
    }
}
