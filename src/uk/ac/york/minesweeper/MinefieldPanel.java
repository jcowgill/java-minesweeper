package uk.ac.york.minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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

    /** Size of all the tiles */
    private static final int TILE_SIZE = 32;

    /** Width of the bevel */
    private static final int BEVEL_WIDTH = 2;

    /** Font vertical offset (from top to BASELINE) */
    private static final int FONT_VOFFSET = 24;

    /** The font to draw numbers with */
    private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);


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
        this.setFont(FONT);
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
     * Draws a character on a tile
     *
     * @param g graphics object
     * @param x x position of top-left of tile
     * @param y y position of top-left of tile
     * @param c character to draw
     */
    private static void drawCharacter(Graphics g, int x, int y, char c)
    {
        // Get coordinates to draw at
        int drawX = x + (TILE_SIZE - g.getFontMetrics().charWidth(c)) / 2;
        int drawY = y + FONT_VOFFSET;

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

        // Draw background
        g.setColor(COLOUR_NORMAL);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw all the tiles
        for (int x = 0; x < minefield.getWidth(); x++)
        {
            for (int y = 0; y < minefield.getHeight(); y++)
            {
                int graphicsX1 = x * TILE_SIZE;
                int graphicsY1 = y * TILE_SIZE;

                // Draw standard background
                g.setColor(COLOUR_DARK);
                g.drawLine(graphicsX1, graphicsY1, graphicsX1 + TILE_SIZE, graphicsY1);
                g.drawLine(graphicsX1, graphicsY1, graphicsX1, graphicsY1 + TILE_SIZE);

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
                        int bevelX2 = graphicsX1 + TILE_SIZE - BEVEL_WIDTH;
                        int bevelY2 = graphicsY1 + TILE_SIZE - BEVEL_WIDTH;

                        g.setColor(COLOUR_LIGHT);
                        g.fillRect(graphicsX1, graphicsY1, TILE_SIZE, BEVEL_WIDTH);
                        g.fillRect(graphicsX1, graphicsY1, BEVEL_WIDTH, TILE_SIZE);
                        g.setColor(COLOUR_DARK);
                        g.fillRect(graphicsX1, bevelY2,    TILE_SIZE, BEVEL_WIDTH);
                        g.fillRect(bevelX2,    graphicsY1, BEVEL_WIDTH, TILE_SIZE);
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
        return new Dimension(TILE_SIZE * minefield.getWidth(),
                             TILE_SIZE * minefield.getHeight());
    }

    @Override
    public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize()
    {
        return getPreferredSize();
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
            return new Point(e.getX() / TILE_SIZE, e.getY() / TILE_SIZE);
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
