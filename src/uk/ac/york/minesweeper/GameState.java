package uk.ac.york.minesweeper;

/**
 * The state of the game itself
 */
public enum GameState
{
    /**
     * The first tile has not been clicked yet
     */
    NOT_STARTED,

    /**
     * The game is in progress
     */
    RUNNING,

    /**
     * The game has finished and has been won (uncovered all non-mines)
     */
    WON,

    /**
     * The game has been lost (clicked a mine)
     */
    LOST,
}
