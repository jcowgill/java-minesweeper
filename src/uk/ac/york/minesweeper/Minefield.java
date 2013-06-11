package uk.ac.york.minesweeper;

import java.util.Arrays;
import java.util.Random;

/**
 * Class containing the game data for the minesweeper game
 */
public class Minefield
{
    // Array containing tile values (-1 = mine)
    private final byte[][] valuesArray;

    // Array containing tile states
    private final TileState[][] stateArray;

    // Number of mines
    private final int mines;

    // Number of extra tiles which need to uncovered to win
    private int tilesLeft;

    // State of the game
    private GameState gameState = GameState.NOT_STARTED;

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
        int tilesLeft = (width * height) - mines;

        // Validate arguments
        if (width < 1 || height < 1 || mines < 0)
            throw new IllegalArgumentException("invalid minefield dimensions");

        if (tilesLeft <= 0)
            throw new IllegalArgumentException("too many mines");

        // Save initial properties
        this.mines = mines;
        this.tilesLeft = tilesLeft;

        // Create arrays (empty + covered)
        TileState[][] stateArray = new TileState[width][height];

        for (int x = 0; x < width; x++)
            Arrays.fill(stateArray[x], TileState.COVERED);

        this.stateArray = stateArray;
        this.valuesArray = new byte[width][height];
    }

    /**
     * Gets the width of the minefield in tiles
     *
     * @return width of the minefield
     */
    public int getWidth()
    {
        return valuesArray.length;
    }

    /**
     * Gets the height of the minefield in tiles
     *
     * @return height of the minefield
     */
    public int getHeight()
    {
        return valuesArray[0].length;
    }

    /**
     * Gets the total number of mines in the minefield
     *
     * @return total number of mines
     */
    public int getMines()
    {
        return mines;
    }

    /**
     * Gets the current state of the game
     *
     * @return the state of the game
     */
    public GameState getGameState()
    {
        return gameState;
    }

    /**
     * Returns true if the game has finished
     *
     * @return true if the game has finished
     */
    public boolean isFinished()
    {
        return gameState != GameState.RUNNING && gameState != GameState.NOT_STARTED;
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
        if (gameState == GameState.NOT_STARTED)
            throw new IllegalStateException("you must call uncover at least once before using getTileValue");

        return valuesArray[x][y];
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
        return stateArray[x][y];
    }

    /**
     * Updates the state of the given tile
     *
     * Can be used to add flags and uncover tiles.
     * You cannot cover a tile that has already been uncovered.
     * If a tile is uncovered, other tile states and the game state may be updated.
     *
     * @param x x position of tile
     * @param y y position of tile
     * @param newState the tile's new state
     */
    public void setTileState(int x, int y, TileState newState)
    {
        if (isFinished())
            throw new IllegalStateException("the game has finished");

        switch (newState)
        {
            case COVERED:
            case FLAGGED:
            case QUESTION:
                // Set unless we're recovering a tile
                if (stateArray[x][y] == TileState.UNCOVERED)
                    throw new UnsupportedOperationException("you cannot cover a tile once uncovered");

                stateArray[x][y] = newState;
                break;

            case UNCOVERED:
                // Forward to uncover
                uncover(x, y);
                break;

            default:
                throw new IllegalArgumentException("newState is not a valid tile state");
        }
    }

    /**
     * Uncovers the tile at the given location
     *
     * This method is equivalent to calling {@code setTileState(x, y, TileState.UNCOVERED)}
     *
     * @param x x position of tile
     * @param y y position of tile
     */
    public void uncover(int x, int y)
    {
        if (isFinished())
            throw new IllegalStateException("the game has finished");

        // New game?
        if (gameState == GameState.NOT_STARTED)
        {
            initValues(x, y);
            gameState = GameState.RUNNING;
        }

        // Perform any uncovering
        uncoverRecursive(x, y);

        // Check for game state updates
        if (valuesArray[x][y] < 0)
        {
            // Hit a mine
            gameState = GameState.LOST;
        }
        else if (tilesLeft <= 0)
        {
            // Uncovered all the non-mines!
            gameState = GameState.WON;
        }
    }

    /**
     * Uncovers the given tile and surrounding tiles without performing state checks
     *
     * @param x x position of tile
     * @param y y position of tile
     */
    private void uncoverRecursive(int x, int y)
    {
        int width = getWidth();
        int height = getHeight();

        // Ignore if the tile does not exist / is already uncovered
        if (x < 0 || y < 0 || x >= width || y >= height)
            return;

        if (stateArray[x][y] == TileState.UNCOVERED)
            return;

        // Uncover this tile and surrounding tiles if it was a zero
        stateArray[x][y] = TileState.UNCOVERED;
        tilesLeft--;

        if (valuesArray[x][y] == 0)
        {
            if (y > 0)
            {
                if (x > 0)          uncoverRecursive(x - 1, y - 1);
                                    uncoverRecursive(x    , y - 1);
                if (x < width - 1)  uncoverRecursive(x + 1, y - 1);
            }

            if (x > 0)              uncoverRecursive(x - 1, y    );
            if (x < width - 1)      uncoverRecursive(x + 1, y    );

            if (y < height - 1)
            {
                if (x > 0)          uncoverRecursive(x - 1, y + 1);
                                    uncoverRecursive(x    , y + 1);
                if (x < width - 1)  uncoverRecursive(x + 1, y + 1);
            }
        }
    }

    /**
     * Initializes the values grid for a new game
     *
     * startX and startY are used to prevent mines from appearing at the start location
     *
     * @param startX x position to prevent mines for
     * @param startY y position to prevent mines for
     */
    private void initValues(int startX, int startY)
    {
        int width = getWidth();
        int height = getHeight();

        // Randomly place all the mines
        Random rnd = new Random();

        for (int i = 0; i < mines; i++)
        {
            int x, y;

            // Keep trying random positions until we've found an acceptable one
            do
            {
                x = rnd.nextInt(width);
                y = rnd.nextInt(height);
            }
            while(valuesArray[x][y] < 0 || (x == startX && y == startY));

            // Set as a mine
            valuesArray[x][y] = -1;

            // Increment number of mines in all surrounding tiles
            if (y > 0)
            {
                if (x > 0)          incrementNonMine(x - 1, y - 1);
                                    incrementNonMine(x    , y - 1);
                if (x < width - 1)  incrementNonMine(x + 1, y - 1);
            }

            if (x > 0)              incrementNonMine(x - 1, y    );
            if (x < width - 1)      incrementNonMine(x + 1, y    );

            if (y < height - 1)
            {
                if (x > 0)          incrementNonMine(x - 1, y + 1);
                                    incrementNonMine(x    , y + 1);
                if (x < width - 1)  incrementNonMine(x + 1, y + 1);
            }
        }
    }

    /**
     * Increments the value of a tile if it is not a mine
     *
     * @param x x position of tile
     * @param y y position of tile
     */
    private void incrementNonMine(int x, int y)
    {
        if (valuesArray[x][y] >= 0)
            valuesArray[x][y]++;
    }

    /**
     * Gets a string representing the minefield's current visible state
     */
    @Override
    public String toString()
    {
        int width = getWidth();
        int height = getHeight();

        StringBuilder builder = new StringBuilder();

        // Write top line
        builder.append('+');
        for (int x = 0; x < width; x++)
            builder.append('-');
        builder.append("+\n");

        // Write each line of the minefield
        for (int y = 0; y < height; y++)
        {
            builder.append('|');

            for (int x = 0; x < width; x++)
            {
                char c;

                // Handle each tile state
                switch (getTileState(x, y))
                {
                    case COVERED:
                        c = '#';
                        break;

                    case FLAGGED:
                        c = 'f';
                        break;

                    case QUESTION:
                        c = '?';
                        break;

                    default:
                        // Show tile's value
                        int tileValue = getTileValue(x, y);

                        if (tileValue < 0)
                            c = '!';
                        else if(tileValue == 0)
                            c = ' ';
                        else
                            c = (char) ('0' + tileValue);
                }

                builder.append(c);
            }

            builder.append("|\n");
        }

        // Write bottom line
        builder.append('+');
        for (int x = 0; x < width; x++)
            builder.append('-');
        builder.append("+\n");

        return builder.toString();
    }
}
