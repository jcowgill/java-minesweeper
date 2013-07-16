package uk.ac.york.minesweeper.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import uk.ac.york.minesweeper.GameState;
import uk.ac.york.minesweeper.Minefield;
import uk.ac.york.minesweeper.MinefieldDrawer;
import uk.ac.york.minesweeper.TileState;

/**
 * A component which can display a minefield graphically and handle various events
 */
public class MinefieldPanel extends JComponent
{
    private static final long serialVersionUID = 1L;

    /** Size of all the tiles */
    private static final int TILE_SIZE = 32;

    /** Font vertical offset (from top to BASELINE) */
    private static final int FONT_VOFFSET = 24;

    /** The font to draw numbers with */
    private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 24);

    /** Current minefield */
    private Minefield minefield;

    /** Currently selected tile (null most of the time) */
    private Point selectedTile;

    /** List of state change listeners */
    private ArrayList<MinefieldStateChangeListener> listeners = new ArrayList<MinefieldStateChangeListener>();

    /** Class which draws the game area */
    private SwingDrawer drawer = new SwingDrawer();

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
     * Adds a listener to which received game state change events
     *
     * @param listener listener to add
     */
    public void addStateChangeListener(MinefieldStateChangeListener listener)
    {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /**
     * Removes a listener which received game state change events
     *
     * @param listener listener to remove
     */
    public void removeStateChangeListener(MinefieldStateChangeListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Fires the state changed event
     */
    private void fireStateChangeEvent()
    {
        MinefieldStateChangeEvent event = new MinefieldStateChangeEvent(this);

        for (MinefieldStateChangeListener listener : listeners)
            listener.stateChanged(event);
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
        this.setSize(getPreferredSize());
        this.repaint();

        // Fire event
        this.fireStateChangeEvent();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        drawer.draw((Graphics2D) g);
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
            // Clear selected tile
            if (selectedTile != null)
            {
                selectedTile = null;
                repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            // Ignore if finished
            if (minefield.isFinished())
                return;

            // Get tile position
            Point tile = getTileFromEvent(e);

            // Right or left click?
            if (SwingUtilities.isLeftMouseButton(e))
            {
                // Do not select tiles with flags on
                if (minefield.getTileState(tile.x, tile.y) == TileState.FLAGGED)
                    return;

                // Set new selected tile
                selectedTile = tile;
            }
            else if (SwingUtilities.isRightMouseButton(e))
            {
                TileState newState;

                // Change flagged state
                switch(minefield.getTileState(tile.x, tile.y))
                {
                    case COVERED:   newState = TileState.FLAGGED;   break;
                    case FLAGGED:   newState = TileState.QUESTION;  break;
                    default:        newState = TileState.COVERED;   break;

                    case UNCOVERED: newState = TileState.UNCOVERED; break;
                }

                minefield.setTileState(tile.x, tile.y, newState);
            }

            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            // Ignore if finished
            if (minefield.isFinished())
                return;

            // Ensure there was a tile selected
            if (selectedTile != null)
            {
                // Ensure the tile was the same as the one clicked on
                if (selectedTile.equals(getTileFromEvent(e)))
                {
                    // Either chord or uncover depending on the number of clicks
                    GameState state = minefield.getGameState();

                    if (e.getClickCount() == 2)
                        minefield.chord(selectedTile.x, selectedTile.y);
                    else if (e.getClickCount() == 1)
                        minefield.uncover(selectedTile.x, selectedTile.y);

                    // Fire state changed event if needed
                    if (minefield.getGameState() != state)
                        fireStateChangeEvent();
                }

                // Clear selected tile
                selectedTile = null;
                repaint();
            }
        }
    }

    /**
     * The class which actually draws the game
     */
    private class SwingDrawer extends MinefieldDrawer<Graphics2D, Color>
    {
        @Override
        protected float getTileSize()
        {
            return TILE_SIZE;
        }

        @Override
        protected int getSelectedX()
        {
            return (selectedTile == null ? -1 : selectedTile.x);
        }

        @Override
        protected int getSelectedY()
        {
            return (selectedTile == null ? -1 : selectedTile.y);
        }

        @Override
        protected Minefield getMinefield()
        {
            return minefield;
        }

        @Override
        protected Color createPaint(int color)
        {
            return new Color(color);
        }

        @Override
        protected void resetDrawArea(Color background)
        {
            Graphics2D g = getDrawContext();

            // Make the numbers look a little nicer
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw background
            if (isOpaque())
            {
                g.setColor(background);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }

        @Override
        protected void fillRect(float x, float y, float width, float height, Color paint)
        {
            Graphics2D g = getDrawContext();

            g.setColor(paint);
            g.fillRect((int) x, (int) y, (int) width, (int) height);
        }

        @Override
        protected void drawCharacter(float x, float y, float tileSize, char c, Color paint)
        {
            Graphics2D g = getDrawContext();

            // Get coordinates to draw at
            int drawX = (int) (x + (TILE_SIZE - g.getFontMetrics().charWidth(c)) / 2);
            int drawY = (int) (y + FONT_VOFFSET);

            // Draw the character
            g.setColor(paint);
            g.drawChars(new char[] { c }, 0, 1, drawX, drawY);
        }

        @Override
        protected void drawMine(float x, float y, float size)
        {
            drawImage(x, y, size, Images.MINE);
        }

        @Override
        protected void drawFlag(float x, float y, float size)
        {
            drawImage(x, y, size, Images.FLAG);
        }

        private void drawImage(float x, float y, float size, Image img)
        {
            getDrawContext().drawImage(img, (int) x, (int) y, (int) size, (int) size, null);
        }
    }
}
