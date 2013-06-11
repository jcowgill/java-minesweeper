package uk.ac.york.minesweeper;

/**
 * The state of a tile in the minefield
 */
public enum TileState
{
    /**
     * Tile is covered (with no flags / question)
     */
    COVERED,

    /**
     * Tile is covered with a flag
     */
    FLAGGED,

    /**
     * Tile is covered with a question
     */
    QUESTION,

    /**
     * Tile is uncovered
     */
    UNCOVERED,
}
