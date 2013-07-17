package uk.ac.york.minesweeper.android;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
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

    // Detector classes
    private ScaleGestureDetector scaleDetector =
            new ScaleGestureDetector(getContext(), new OnPinchListener());

    private GestureDetector scrollDetector =
            new GestureDetector(getContext(), new OnScrollListener());

    // True if a detector requested a relayout
    private boolean shouldUpdateLayout;

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

    /**
     * Restores data in a SavedInfo instance
     *
     * @param info info to restore
     */
    public void restoreSavedInfo(SavedInfo info)
    {
        scaleFactor = info.scaleFactor;
        translateX = info.translateX;
        translateY = info.translateY;
        updateLayout();
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

        // Done layout
        shouldUpdateLayout = false;
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
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        // Small hack to see all touch events as well as passing onto children

        // Handle touch event ourselves
        scrollDetector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);

        // Forward to children
        boolean result = super.dispatchTouchEvent(event);

        // Trigger relayout
        if (shouldUpdateLayout)
            updateLayout();

        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // Handle all touch events which come our way
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
            float oldScaleFactor = scaleFactor;

            // Update scale factor and limit zoom
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

            // Update translate offsets so we zoom into the focus point
            float thisScaleFactor = scaleFactor / oldScaleFactor;

            float focusPointX = detector.getFocusX() - translateX;
            float focusPointY = detector.getFocusY() - translateY;

            translateX -= focusPointX * (thisScaleFactor - 1);
            translateY -= focusPointY * (thisScaleFactor - 1);

            // Trigger redraw
            shouldUpdateLayout = true;
            return true;
        }
    }

    /**
     * Private class which handles scrolling
     */
    private class OnScrollListener extends SimpleOnGestureListener
    {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            // Update translate amount
            translateX -= distanceX;
            translateY -= distanceY;

            // Trigger redraw
            shouldUpdateLayout = true;
            return true;
        }
    }

    /**
     * Saved information about the state of the zoom view
     *
     * Information about the child is NOT saved
     */
    public static class SavedInfo implements Parcelable
    {
        final float scaleFactor, translateX, translateY;

        /**
         * Reads an instance of SavedInfo from a parcel
         *
         * @param parcel parcel to read info from
         */
        public SavedInfo(Parcel parcel)
        {
            scaleFactor = parcel.readFloat();
            translateX = parcel.readFloat();
            translateY = parcel.readFloat();
        }

        /**
         * Creates an instance of SavedInfo from the information in ZoomView
         *
         * @param zoomView view to read info from
         */
        public SavedInfo(ZoomView zoomView)
        {
            scaleFactor = zoomView.scaleFactor;
            translateX = zoomView.translateX;
            translateY = zoomView.translateY;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            out.writeFloat(scaleFactor);
            out.writeFloat(translateX);
            out.writeFloat(translateY);
        }

        public static final Parcelable.Creator<SavedInfo> CREATOR =
            new Creator<ZoomView.SavedInfo>()
            {
                @Override
                public SavedInfo[] newArray(int size)
                {
                    return new SavedInfo[size];
                }

                @Override
                public SavedInfo createFromParcel(Parcel source)
                {
                    return new SavedInfo(source);
                }
            };
    }
}
