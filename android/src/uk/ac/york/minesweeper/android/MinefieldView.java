package uk.ac.york.minesweeper.android;

import uk.ac.york.minesweeper.Minefield;
import uk.ac.york.minesweeper.MinefieldDrawer;
import uk.ac.york.minesweeper.TileState;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * A view which displays and handles events for a minefield
 */
public class MinefieldView extends View
{
    /** Default tile size (zoom factor of 1) */
    private static final int DEFAULT_TILE_SIZE = 32;

    /** The current minefield */
    private Minefield minefield;

    /** If true, tap to flag and long press to uncover */
    private boolean singleTapFlag;

    /** If true, questions are enabled as well as flags */
    private boolean enableQuestions;

    /** The drawing class */
    private AndroidDrawer drawer = new AndroidDrawer();

    /** Detector class */
    private GestureDetector tapDetector =
            new GestureDetector(getContext(), new OnTapListener());

    /**
     * Creates a new MinefieldView
     */
    public MinefieldView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Gets the minefield being shown
     *
     * @return the minefield or null if nothing is showing
     */
    public Minefield getMinefield()
    {
        return minefield;
    }

    /**
     * Sets the minefield being shown
     *
     * @param minefield new minefield
     */
    public void setMinefield(Minefield minefield)
    {
        this.minefield = minefield;
        requestLayout();
        invalidate();
    }

    /**
     * True if single tapping flags mines instead of uncovering them
     */
    public boolean getSingleTapFlag()
    {
        return singleTapFlag;
    }

    /**
     * Set to true to make single tapping flag tiles instead of uncovering them
     *
     * @param value new value
     */
    public void setSingleTapFlag(boolean value)
    {
        singleTapFlag = value;
    }

    /**
     * True if questions are enabled
     */
    public boolean getQuestionsEnabled()
    {
        return enableQuestions;
    }

    /**
     * Set to true to enable questions
     *
     * If setting to false, all existing questions in the minefield
     * are converted to covered tiles.
     *
     * @param value new value
     */
    public void setQuestionsEnabled(boolean value)
    {
        if (enableQuestions && !value && minefield != null && !minefield.isFinished())
        {
            // Convert all qestion tiles to covered tiles
            int width = minefield.getWidth();
            int height = minefield.getHeight();

            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    if (minefield.getTileState(x, y) == TileState.QUESTION)
                        minefield.setTileState(x, y, TileState.COVERED);
                }
            }
        }

        enableQuestions = value;
    }

    @Override
    protected int getSuggestedMinimumWidth()
    {
        // Base on default and minefield size
        if (minefield == null)
            return super.getSuggestedMinimumWidth();
        else
            return DEFAULT_TILE_SIZE * minefield.getWidth();
    }

    @Override
    protected int getSuggestedMinimumHeight()
    {
        // Base on default and minefield size
        if (minefield == null)
            return super.getSuggestedMinimumWidth();
        else
            return DEFAULT_TILE_SIZE * minefield.getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        drawer.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        tapDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onSizeChanged(int l, int t, int oldl, int oldt)
    {
        invalidate();
    }

    /**
     * Calculates the tile size for the view
     *
     * Requires valid minefield
     *
     * @return the tile size
     */
    private float calcTileSize()
    {
        // Find largest possible tile size
        return Math.min((float) getWidth() / minefield.getWidth(),
                        (float) getHeight() / minefield.getHeight());
    }

    /**
     * The class which actually draws the game
     */
    private class AndroidDrawer extends MinefieldDrawer<Canvas, Paint>
    {
        @Override
        protected float getTileSize()
        {
            return calcTileSize();
        }

        @Override
        protected int getSelectedX()
        {
            return -1;
        }

        @Override
        protected int getSelectedY()
        {
            return -1;
        }

        @Override
        protected Minefield getMinefield()
        {
            return minefield;
        }

        @Override
        protected Paint createPaint(int color)
        {
            Paint paint = new Paint();

            // Set colour and properties
            paint.setColor(color | 0xFF000000);
            paint.setStyle(Paint.Style.FILL);
            paint.setTypeface(Typeface.MONOSPACE);
            paint.setTextAlign(Paint.Align.CENTER);

            return paint;
        }

        @Override
        protected void resetDrawArea(Paint background)
        {
            Canvas canvas = getDrawContext();

            // Clear background
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), background);
        }

        @Override
        protected void fillRect(float x, float y, float width, float height, Paint paint)
        {
            getDrawContext().drawRect(x, y, x + width, y + height, paint);
        }

        @Override
        protected void drawCharacter(float x, float y, float tileSize, char c, Paint paint)
        {
            float size = tileSize * 0.75f;

            // Update font size
            paint.setTextSize(tileSize * 0.75f);

            // Reposition
            x += tileSize / 2;
            y += size;

            // Draw text
            getDrawContext().drawText(new char[] { c }, 0, 1, x, y, paint);
        }

        @Override
        protected void drawMine(float x, float y, float size)
        {
            // TODO draw proper image
            drawCharacter(x, y, getTileSize(), '!', createPaint(0));
        }

        @Override
        protected void drawFlag(float x, float y, float size)
        {
            // TODO draw proper image
            drawCharacter(x, y, getTileSize(), 'F', createPaint(0));
        }
    }

    /**
     * Private class which handles tapping
     */
    private class OnTapListener extends SimpleOnGestureListener
    {
        /** Length of vibration (ms) for long presses */
        private static final int VIBLEN_LONG_PRESS = 50;

        /** Length of vibration (ms) for end of game */
        private static final int VIBLEN_GAME_END = 200;

        private Vibrator vibrator =
                (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        /**
         * Handles a tap event
         */
        private void handleEvent(MotionEvent event, boolean isLongPress)
        {
            // Only handle for valid minefields
            if (minefield != null && !minefield.isFinished())
            {
                // Calculate tile to process
                float tileSize = calcTileSize();
                int x = (int) (event.getX() / tileSize);
                int y = (int) (event.getY() / tileSize);

                // Uncover, flag or chord
                TileState state = minefield.getTileState(x, y);

                if (state == TileState.UNCOVERED)
                {
                    // Chord for single tap, ignore long presses
                    if (!isLongPress)
                        minefield.chord(x, y);
                }
                else if (!isLongPress == singleTapFlag)
                {
                    // Flag
                    if (state == TileState.COVERED)
                        state = TileState.FLAGGED;
                    else if (state == TileState.FLAGGED && enableQuestions)
                        state = TileState.QUESTION;
                    else
                        state = TileState.COVERED;

                    minefield.setTileState(x, y, state);
                }
                else if (state != TileState.FLAGGED)
                {
                    // Uncover
                    minefield.uncover(x, y);
                }

                // Vibrate at end of game and on long presses
                if (minefield.isFinished())
                    vibrator.vibrate(VIBLEN_GAME_END);
                else if (isLongPress && state != minefield.getTileState(x, y))
                    vibrator.vibrate(VIBLEN_LONG_PRESS);

                // Redraw
                invalidate();
            }
        }

        @Override
        public void onLongPress(MotionEvent event)
        {
            handleEvent(event, true);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event)
        {
            handleEvent(event, false);
            return true;
        }
    }
}
