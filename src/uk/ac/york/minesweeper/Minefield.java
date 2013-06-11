package uk.ac.york.minesweeper;

/**
 * Class containing the game data for the minesweeper game
 */
public class Minefield
{
    // Array containing mine locations and number of surrounding mines (-1 = mine)
    private byte[][] minesArray;

    // Minefield dimensions
    private final int width, height, mines;

    /**
     * Initializes a new Minefield class with the given properties
     *
     * The mine locations are not allocated until the first click is made
     *
     * @param width width of the minefield in tiles
     * @param height height of the minefield in tiles
     * @param mines number of mines
     */
    public Minefield(int width, int height, int mines)
    {
        // Validate arguments
        if (width < 1 || height < 1)
            throw new IllegalArgumentException("invalid minefield dimensions");

        if (width * height >= mines)
            throw new IllegalArgumentException("too many mines");

        // Save dimensions
        this.width = width;
        this.height = height;
        this.mines = mines;
    }

    public GameState getGameState()
    {
        // TODO Unimplemented
        return GameState.NOT_STARTED;
    }

    /**
     * Gets the state of the given tile
     *
     * @param x x position of tile
     * @param y y position of tile
     * @return state of that tile
     */
    public TileState getTileState(int x, int y)
    {
        // TODO Unimplemented
        return TileState.COVERED;
    }

    /**
     * Gets the value of the given tile (mine / surrounding mines)
     *
     * This should only be called AFTER the first tile is clicked (or when a tile is uncovered)
     *
     * @param x x position of tile
     * @param y y position of tile
     * @return value of that tile (-1 = mine)
     */
    public int getTileValue(int x, int y)
    {
        // TODO Unimplemented
        return 0;
    }

    public void click(int x, int y)
    {
        //
    }
}
