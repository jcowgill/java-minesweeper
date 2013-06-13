package uk.ac.york.minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

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

    /** Normal grey background */
    private static final Color COLOUR_NORMAL = new Color(0xC0, 0xC0, 0xC0);

    /** Light grey for bevels */
    private static final Color COLOUR_LIGHT = new Color(0xE0, 0xE0, 0xE0);

    /** Dark grey for bevels */
    private static final Color COLOUR_DARK = new Color(0x80, 0x80, 0x80);

    /** Current minefield */
    private Minefield minefield;

    /**
     * Initializes a new MinefieldPanel with the given Minefield
     *
     * There must always be a minefield to display (you cannot pass null)
     *
     * @param minefield minefield to display
     */
    public MinefieldPanel(Minefield minefield)
    {
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

        // Update all visuals
        this.setBounds(this.getBounds());
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics gOld)
    {
        Graphics2D g = (Graphics2D) gOld;

        // Calculate tile size and line size
        int tileSize = getWidth() / minefield.getWidth();
        int lineSize = (tileSize + LINE_SCALE - 1) / LINE_SCALE;

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

                // Covered or uncovered?
                if (minefield.getTileState(x, y) == TileState.UNCOVERED)
                {
                    // TODO numbers
                }
                else
                {
                    // Draw bevel
                    int bevelX2 = graphicsX1 + tileSize - lineSize;
                    int bevelY2 = graphicsY1 + tileSize - lineSize;

                    g.setColor(COLOUR_LIGHT);
                    g.fillRect(graphicsX1, graphicsY1, tileSize, lineSize);
                    g.fillRect(graphicsX1, graphicsY1, lineSize, tileSize);
                    g.setColor(COLOUR_DARK);
                    g.fillRect(graphicsX1, bevelY2,    tileSize, lineSize);
                    g.fillRect(bevelX2,    graphicsY1, lineSize, tileSize);

                    // Draw flag or question mark if needed
                    if (minefield.getTileState(x, y) == TileState.FLAGGED)
                    {
                        // TODO flags
                    }
                    else if (minefield.getTileState(x, y) == TileState.QUESTION)
                    {
                        // TODO questions
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
