package uk.ac.york.minesweeper;

/**
 * Helper class use to draw the minefield game area
 *
 * @param <TContext> the type of context used in the drawer
 * @param <TPaint> the type of cached paints
 */
public abstract class MinefieldDrawer<TContext, TPaint>
{
    /** Number to divide tile size by to get bevel width */
    private static final float BEVEL_WIDTH_DIVISOR = 16f;

    /** Number to divide tile size by to get line width */
    private static final float LINE_WIDTH_DIVISOR = 32f;

    /** Number to multiply tile size by to get image sizes */
    private static final float IMAGE_SIZE_MULTIPLIER = 0.75f;

    // All colours are RGB ints (0xRRGGBB)

    /** Default background colour */
    private static final int COLOUR_BACKGROUND = 0xC0C0C0;

    /** Light grey for bevels */
    private static final int COLOUR_LIGHT = 0xE0E0E0;

    /** Dark grey for bevels */
    private static final int COLOUR_DARK = 0x808080;

    /** Colour of question marks */
    private static final int COLOUR_QUESTION = 0xFFFFFF;

    /** The colours of the numbers (0 is unused) */
    private static final int[] COLOUR_NUMBERS = new int[]
    {
        0,          // 0 = Unused
        0x0000FF,   // 1 = Blue
        0x007F00,   // 2 = Green
        0xFF0000,   // 3 = Red
        0x2F2F9F,   // 4 = Dark Blue
        0x7F0000,   // 5 = Maroon
        0x9F9F2F,   // 6 = Turquoise
        0x000000,   // 7 = Black
        0x7F7F7F,   // 8 = Grey
    };

    // Cached colours
    private boolean doneInit;

    private TPaint paintBackground;
    private TPaint paintLight;
    private TPaint paintDark;
    private TPaint paintQuestion;
    private TPaint[] paintNumbers;

    /** The saved drawer context */
    private TContext context;

    /**
     * Gets this drawer's context
     *
     * This field is only value during the execution of the draw method
     *
     * @return the context
     */
    public TContext getDrawContext()
    {
        return context;
    }

    /**
     * Initializes the cached colours
     */
    public void init()
    {
        if (!doneInit)
        {
            paintBackground  = createPaint(COLOUR_BACKGROUND);
            paintDark        = createPaint(COLOUR_DARK);
            paintLight       = createPaint(COLOUR_LIGHT);
            paintQuestion    = createPaint(COLOUR_QUESTION);

            @SuppressWarnings("unchecked")
            TPaint[] locNumbers = (TPaint[]) new Object[COLOUR_NUMBERS.length];

            for (int i = 0; i < COLOUR_NUMBERS.length; i++)
                locNumbers[i] = createPaint(COLOUR_NUMBERS[i]);

            paintNumbers = locNumbers;

            doneInit = true;
        }
    }

    /**
     * Draws the minefield using the given drawing context
     *
     * @param drawContext context to draw to
     */
    public void draw(TContext drawContext)
    {
        // Ensure paints have been generated
        init();

        // Save context
        this.context = drawContext;

        // Clear the screen
        resetDrawArea(paintBackground);

        // Get minefield and (possibly) exit early
        Minefield minefield = getMinefield();
        if (minefield == null)
        {
            this.context = null;
            return;
        }

        // Get and calculate other parameters
        int selectedX = getSelectedX();
        int selectedY = getSelectedY();
        float paddingX = getPaddingLeft();
        float paddingY = getPaddingTop();

        float tileSize = getTileSize();
        float bevelWidth = tileSize / BEVEL_WIDTH_DIVISOR;
        float lineWidth = tileSize / LINE_WIDTH_DIVISOR;
        float imageSize = tileSize * IMAGE_SIZE_MULTIPLIER;
        float imageOffset = tileSize * ((1 - IMAGE_SIZE_MULTIPLIER) / 2f);

        // Draw all the tiles
        for (int x = 0; x < minefield.getWidth(); x++)
        {
            for (int y = 0; y < minefield.getHeight(); y++)
            {
                float graphicsX1 = x * tileSize + paddingX;
                float graphicsY1 = y * tileSize + paddingY;

                // Draw standard background
                fillRect(graphicsX1, graphicsY1, tileSize, lineWidth, paintDark);
                fillRect(graphicsX1, graphicsY1, lineWidth, tileSize, paintDark);

                // Covered or uncovered?
                if (minefield.getTileState(x, y) == TileState.UNCOVERED)
                {
                    // Draw the correct symbol
                    int tileValue = minefield.getTileValue(x, y);

                    if (tileValue < 0)
                    {
                        drawMine(graphicsX1 + imageOffset, graphicsY1 + imageOffset, imageSize);
                    }
                    else if (tileValue > 0)
                    {
                        drawCharacter(graphicsX1, graphicsY1, tileSize,
                                (char) ('0' + tileValue), paintNumbers[tileValue]);
                    }
                }
                else
                {
                    // Only draw the bevel background if this is NOT the selected tile
                    if (x != selectedX || y != selectedY)
                    {
                        float bevelX2 = graphicsX1 + tileSize - bevelWidth;
                        float bevelY2 = graphicsY1 + tileSize - bevelWidth;

                        fillRect(graphicsX1, graphicsY1, tileSize, bevelWidth, paintLight);
                        fillRect(graphicsX1, graphicsY1, bevelWidth, tileSize, paintLight);
                        fillRect(graphicsX1, bevelY2,    tileSize, bevelWidth, paintDark);
                        fillRect(bevelX2,    graphicsY1, bevelWidth, tileSize, paintDark);
                    }

                    // Draw flag or question mark if needed
                    if (minefield.getTileState(x, y) == TileState.FLAGGED)
                    {
                        drawFlag(graphicsX1 + imageOffset, graphicsY1 + imageOffset, imageSize);
                    }
                    else if (minefield.getTileState(x, y) == TileState.QUESTION)
                    {
                        drawCharacter(graphicsX1, graphicsY1, tileSize, '?', paintQuestion);
                    }
                }
            }
        }

        // Clear context
        this.context = null;
    }

    /**
     * Returns the size of tiles
     *
     * @return the tile size
     */
    protected abstract float getTileSize();

    /**
     * Returns the selected tile's x position or -1 if no tile is selected
     *
     * @return selected tile x position
     */
    protected int getSelectedX()
    {
        return -1;
    }

    /**
     * Returns the selected tile's y position or -1 if no tile is selected
     *
     * @return selected tile y position
     */
    protected int getSelectedY()
    {
        return -1;
    }

    /**
     * Returns the amount of padding above the minefield
     */
    protected float getPaddingTop()
    {
        return 0f;
    }

    /**
     * Returns the amount of padding to the left of the minefield
     */
    protected float getPaddingLeft()
    {
        return 0f;
    }

    /**
     * Returns the minefield to draw
     *
     * @return minefield to draw
     */
    protected abstract Minefield getMinefield();

    /**
     * Creates a paint object for the given RGB colour
     *
     * @param color colour to create object for
     * @param forText true if the colour will only be used for text, false for rectangles
     */
    protected abstract TPaint createPaint(int color);

    /**
     * Clears the draw area to the given colour and prepares for drawing
     *
     * @param background background colour to draw
     */
    protected abstract void resetDrawArea(TPaint background);

    /**
     * Draws a filled in rectangle at the given location using the given paint
     *
     * @param x x position of top-left corner
     * @param y y position of top-left corner
     * @param width width of the rectangle
     * @param height height of the rectangle
     * @param paint paint to draw the rectangle with
     */
    protected abstract void fillRect(float x, float y, float width, float height, TPaint paint);

    /**
     * Draws a single character at the given tile position using the given colour
     *
     * @param x x position of the top-left of the tile
     * @param y y position of the top-left of the tile
     * @param tileSize size of tiles (as returned by {@code getTileSize()})
     * @param c character to draw
     * @param paint paint to draw with
     */
    protected abstract void drawCharacter(float x, float y, float tileSize, char c, TPaint paint);

    /**
     * Draws a mine at the given location
     *
     * @param x x position of top-left corner
     * @param y y position of top-left corner
     * @param size width and height of the mine
     */
    protected abstract void drawMine(float x, float y, float size);

    /**
     * Draws a flag at the given location
     *
     * @param x x position of top-left corner
     * @param y y position of top-left corner
     * @param size width and height of the flag
     */
    protected abstract void drawFlag(float x, float y, float size);
}
