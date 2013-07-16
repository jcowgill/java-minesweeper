package uk.ac.york.minesweeper.android;

import uk.ac.york.minesweeper.Minefield;
import uk.ac.york.minesweeper.MinefieldDrawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
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

    /** The drawing class */
    private AndroidDrawer drawer = new AndroidDrawer();

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
    protected void onSizeChanged(int l, int t, int oldl, int oldt)
    {
        invalidate();
    }

    /**
     * The class which actually draws the game
     */
    private class AndroidDrawer extends MinefieldDrawer<Canvas, Paint>
    {
        @Override
        protected float getTileSize()
        {
            Canvas canvas = getDrawContext();

            // Find largest possible tile size
            return Math.min((float) canvas.getWidth() / minefield.getWidth(),
                            (float) canvas.getHeight() / minefield.getHeight());
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
            // TODO fix this
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
}
