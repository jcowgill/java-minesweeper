package uk.ac.york.minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * A component which can display a minefield graphically and handle various events
 */
public class MinefieldPanel extends JComponent
{
    private static final long serialVersionUID = 1L;

    /** Default (preferred) tile size */
    private static final int DEFAULT_TILE_SIZE = 24;

    /** Amount to reduce line size by compared to tile size */
    private static final int LINE_SCALE = 16;

    /** Amount of space to leave around characters (this value is for the default tile size) */
    private static final int FONT_MARGIN = 4;

    /** Normal grey background */
    private static final Color COLOUR_NORMAL = new Color(0xC0, 0xC0, 0xC0);

    /** Light grey for bevels */
    private static final Color COLOUR_LIGHT = new Color(0xE0, 0xE0, 0xE0);

    /** Dark grey for bevels */
    private static final Color COLOUR_DARK = new Color(0x80, 0x80, 0x80);

    /** Colour of question marks */
    private static final Color COLOUR_QUESTION = Color.WHITE;

    /** The colours of the numbers */
    private static final Color[] COLOUR_NUMBERS = new Color[]
    {
        COLOUR_NORMAL,                  // 0 = Blend with background
        new Color(0x00, 0x00, 0xFF),    // 1 = Blue
        new Color(0x00, 0x7F, 0x00),    // 2 = Green
        new Color(0xFF, 0x00, 0x00),    // 3 = Red
        new Color(0x2F, 0x2F, 0x9F),    // 4 = Dark Blue
        new Color(0xFF, 0x00, 0x00),    // 5 = Maroon
        new Color(0x9F, 0x9F, 0x2F),    // 6 = Turquoise
        new Color(0x00, 0x00, 0x00),    // 7 = Black
        new Color(0x7F, 0x7F, 0x7F),    // 8 = Grey
    };

    /** Current minefield */
    private Minefield minefield;

    /** Currently selected tile (null most of the time) */
    private Point selectedTile;

    /**
     * Initializes a new MinefieldPanel with the given Minefield
     *
     * There must always be a minefield to display (you cannot pass null)
     *
     * @param minefield minefield to display
     */
    public MinefieldPanel(Minefield minefield)
    {
        this.addMouseListener(new MouseEventListener());
        this.setOpaque(true);
        this.setMinefield(minefield);
    }

    /**
     * Gets the current minefield
     *
     * @return current minefield
     */
    public Minefield getMinefield()
    {
        return minefield;
    }

    /**
     * Sets a new minefield for the component
     *
     * @param newMinefield the new minefield
     */
    public void setMinefield(Minefield newMinefield)
    {
        if (newMinefield == null)
            throw new IllegalArgumentException("newMinefield cannot be null");

        this.minefield = newMinefield;

        // Reset selected tile
        this.selectedTile = null;

        // Update all visuals
        this.setBounds(this.getBounds());
        this.repaint();
    }

    /**
     * Rescales the current font for another tile size
     */
    private static void rescaleFont(Graphics g, int tileSize)
    {
        // Calculate height to work with
        float availableSpace = tileSize - 2 * FONT_MARGIN;

        // Rescale font height
        Font currFont = g.getFont();
        FontMetrics fMetrics = g.getFontMetrics();

        float newSize = availableSpace / (fMetrics.getAscent() + fMetrics.getDescent()) * currFont.getSize();

        // Set new font
        g.setFont(currFont.deriveFont(newSize));
    }

    /**
     * Draws a character on a tile
     *
     * @param g graphics object
     * @param tileSize tile size
     * @param x x position of top-left of tile
     * @param y y position of top-left of tile
     * @param c character to draw
     */
    private static void drawCharacter(Graphics g, int x, int y, char c)
    {
        // Get currect coordinates to draw at
        int drawX = x + FONT_MARGIN;
        int drawY = y + FONT_MARGIN + g.getFontMetrics().getAscent();

        // Draw the character
        g.drawChars(new char[] { c }, 0, 1, drawX, drawY);
    }

    @Override
    public void paintComponent(Graphics gOld)
    {
        Graphics2D g = (Graphics2D) gOld;

        // Get selected tile position
        int selectedX = (selectedTile == null ? -1 : selectedTile.x);
        int selectedY = (selectedTile == null ? -1 : selectedTile.y);

        // Calculate tile size and line size
        int tileSize = getTileSize();
        int lineSize = (tileSize + LINE_SCALE - 1) / LINE_SCALE;

        // Rescale font to correct size
        rescaleFont(g, tileSize);

        // Draw background
        g.setColor(COLOUR_NORMAL);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw all the tiles
        for (int x = 0; x < minefield.getWidth(); x++)
        {
            for (int y = 0; y < minefield.getHeight(); y++)
            {
                int graphicsX1 = x * tileSize;
                int graphicsY1 = y * tileSize;

                // Draw standard background
                g.setColor(COLOUR_DARK);
                g.drawLine(graphicsX1, graphicsY1, graphicsX1 + tileSize, graphicsY1);
                g.drawLine(graphicsX1, graphicsY1, graphicsX1, graphicsY1 + tileSize);

                // Covered or uncovered?
                if (minefield.getTileState(x, y) == TileState.UNCOVERED)
                {
                    // Draw the correct symbol
                    int tileValue = minefield.getTileValue(x, y);

                    if (tileValue < 0)
                    {
                        // TODO Draw Mine
                    }
                    else
                    {
                        g.setColor(COLOUR_NUMBERS[tileValue]);
                        drawCharacter(g, graphicsX1, graphicsY1, (char) ('0' + tileValue));
                    }
                }
                else
                {
                    // Only draw the bevel background if this is NOT the selected tile
                    if (x != selectedX || y != selectedY)
                    {
                        int bevelX2 = graphicsX1 + tileSize - lineSize;
                        int bevelY2 = graphicsY1 + tileSize - lineSize;

                        g.setColor(COLOUR_LIGHT);
                        g.fillRect(graphicsX1, graphicsY1, tileSize, lineSize);
                        g.fillRect(graphicsX1, graphicsY1, lineSize, tileSize);
                        g.setColor(COLOUR_DARK);
                        g.fillRect(graphicsX1, bevelY2,    tileSize, lineSize);
                        g.fillRect(bevelX2,    graphicsY1, lineSize, tileSize);
                    }

                    // Draw flag or question mark if needed
                    if (minefield.getTileState(x, y) == TileState.FLAGGED)
                    {
                        // TODO flags
                    }
                    else if (minefield.getTileState(x, y) == TileState.QUESTION)
                    {
                        g.setColor(COLOUR_QUESTION);
                        drawCharacter(g, graphicsX1, graphicsY1, '?');
                    }
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(DEFAULT_TILE_SIZE * minefield.getWidth(),
                             DEFAULT_TILE_SIZE * minefield.getHeight());
    }

    @Override
    public void setBounds(int x, int y, int width, int height)
    {
        // This method forces the component to the correct aspect ratio

        // Calculate tile size from width and height
        int tileSizeWidth = width / minefield.getWidth();
        int tileSizeHeight = height / minefield.getHeight();

        // Use the smallest to recalculate width and height
        int tileSize = Math.min(tileSizeWidth, tileSizeHeight);

        width = tileSize * minefield.getWidth();
        height = tileSize * minefield.getHeight();

        // Tell superclass about the update
        super.setBounds(x, y, width, height);
    }

    /**
     * Gets the size of the tiles in the minefield
     */
    private int getTileSize()
    {
        return getWidth() / minefield.getWidth();
    }

    /**
     * Handles all mouse events within the game area
     */
    private class MouseEventListener extends MouseAdapter
    {
        /**
         * Calculates the selected tile from a mouse event
         */
        private Point getTileFromEvent(MouseEvent e)
        {
            int tileSize = getTileSize();

            return new Point(e.getX() / tileSize, e.getY() / tileSize);
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            // Ignore if finished
            if (minefield.isFinished())
                return;

            // Clear selected tile
            selectedTile = null;
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            // Ignore if finished
            if (minefield.isFinished())
                return;

            // Update selected tile
            selectedTile = getTileFromEvent(e);
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            // Ignore if finished
            if (minefield.isFinished())
                return;

            // If the tile is the same as before, uncover it
            if (selectedTile != null && selectedTile.equals(getTileFromEvent(e)))
            {
                minefield.uncover(selectedTile.x, selectedTile.y);
            }

            // Clear selected tile
            selectedTile = null;
            repaint();
        }
    }

    public static void main(String[] args)
    {
        final MinefieldPanel comp = new MinefieldPanel(new Minefield(8, 8, 10));

        JFrame mainWindow = new JFrame("My Own Component");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        comp.setFocusable(true);
        mainWindow.getContentPane().add(comp);
        mainWindow.pack();
        mainWindow.setVisible(true);
    }
}
