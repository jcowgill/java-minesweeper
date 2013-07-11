package uk.ac.york.minesweeper.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.ViewGroup;

/**
 * A view which contains another view which can be zoomed and panned
 *
 * This view does not actually zoom the child view's graphics, it instead
 * changes its layout size. This allows events to be received correctly.
 */
public class ZoomView extends ViewGroup
{
    // Minimum and maximum zoom constants
    private final static float MIN_ZOOM = 1f;
    private final static float MAX_ZOOM = 5f;

    // Current zoom state
    private float scaleFactor = 1f;

    // Current pan state
    private float translateX = 0f;
    private float translateY = 0f;

    // Last recorded X and Y positions of the mouse
    private float prevX = 0f;
    private float prevY = 0f;

    // Class which handles scaling events
    private ScaleGestureDetector detector =
            new ScaleGestureDetector(getContext(), new OnPinchListener());

    /**
     * Creates a new ZoomView using the given context
     *
     * @param context context to use
     */
    public ZoomView(Context context)
    {
        super(context);
    }

    /**
     * Creates a new ZoomView using the given context and attributes
     *
     * @param context context to use
     * @param attrs view group attributes
     */
    public ZoomView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void addView(View child, int index, LayoutParams params)
    {
        // Prevent multiple children
        if (super.getChildCount() >= 1)
            throw new UnsupportedOperationException("only one sub-view allowed in ZoomView");

        super.addView(child, index, params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // Measure ourselves with default settings
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Measure child allowing it any size it wants
        View child = getChildAt(0);

        if (child != null && child.getVisibility() != GONE)
        {
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        updateLayout();
    }

    /**
     * Called to update the layout immediately
     */
    private void updateLayout()
    {
        View child = getChildAt(0);

        // Any children?
        if (child != null && child.getVisibility() != GONE)
        {
            // Get requested child width and height (before scaling)
            int preScaleChildWidth, preScaleChildHeight;
            LayoutParams childLayout = child.getLayoutParams();

            if (childLayout.width == LayoutParams.WRAP_CONTENT)
                preScaleChildWidth = child.getMeasuredWidth();
            else if (childLayout.width == LayoutParams.MATCH_PARENT)
                throw new IllegalStateException("cannot use match_parent layout with ZoomView");
            else
                preScaleChildWidth = childLayout.width;

            if (childLayout.height == LayoutParams.WRAP_CONTENT)
                preScaleChildHeight = child.getMeasuredHeight();
            else if (childLayout.height == LayoutParams.MATCH_PARENT)
                throw new IllegalStateException("cannot use match_parent layout with ZoomView");
            else
                preScaleChildHeight = childLayout.height;

            // Scale width and height
            int childWidth = (int) (preScaleChildWidth * scaleFactor);
            int childHeight = (int) (preScaleChildHeight * scaleFactor);

            // Get and adjust translate offsets
            float x = adjustOffset(translateX, childWidth, getWidth());
            float y = adjustOffset(translateY, childHeight, getHeight());

            translateX = x;
            translateY = y;

            // Do the final layout
            int intX = (int) x;
            int intY = (int) y;

            child.layout(intX, intY, intX + childWidth, intY + childHeight);
        }
    }

    /**
     * Adjusts an x or y offset so that the child fits (if possible) within the view
     *
     * @param original original child offset
     * @param childSize size (width / height) of the child
     * @param viewSize size (width / height) of the view
     *
     * @return the new offset
     */
    private float adjustOffset(float original, int childSize, int viewSize)
    {
        // If the child is smaller, centre it and ignore the original offset
        if (childSize < viewSize)
            return (viewSize - childSize) / 2;

        // Ensure there's no gap at the top
        if (original > 0)
            return 0;

        // Ensure there's no gap at the bottom
        if ((original + childSize) < viewSize)
            return viewSize - childSize;

        return original;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int pointerCount = event.getPointerCount();
        int action = event.getActionMasked();

        float sumX = 0f;
        float sumY = 0f;
        float x, y;

        // Calculate index to skip for pointer up events
        int skipIndex = -1;

        if (action == MotionEvent.ACTION_POINTER_UP)
            skipIndex = event.getActionIndex();

        // Calculate focal point
        //  If a pointer is being lifted, do not include it in the focal point
        for (int i = 0; i < pointerCount; i++)
        {
            if (i != skipIndex)
            {
                sumX += event.getX(i);
                sumY += event.getY(i);
            }
        }

        if (skipIndex < 0)
        {
            x = sumX / pointerCount;
            y = sumY / pointerCount;
        }
        else
        {
            x = sumX / (pointerCount - 1);
            y = sumY / (pointerCount - 1);
        }

        // Handle the dragging events
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                // Reset previous x and y positions
                //  These recalculate the focal point so that adding / removing
                //  fingers does not cause the view to jump
                prevX = x;
                prevY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                // Move the current position by the delta amount
                translateX += x - prevX;
                translateY += y - prevY;
                prevX = x;
                prevY = y;

                // Trigger redraw
                updateLayout();
                break;
        }

        // Handle any zoom events
        detector.onTouchEvent(event);
        return true;
    }

    /**
     * Private class which handles scale changes
     */
    private class OnPinchListener extends SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            // Update scale factor and limit zoom
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

            // Trigger redraw
            updateLayout();
            return true;
        }
    }
}
