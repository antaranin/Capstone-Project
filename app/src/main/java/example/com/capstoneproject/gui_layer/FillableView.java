package example.com.capstoneproject.gui_layer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import example.com.capstoneproject.R;
import hugo.weaving.DebugLog;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Arin on 09/05/16.
 */
public class FillableView extends View
{
    private static final int FILL_PERC_MOVE = 3;
    public static final int NO_IMAGE = -1;
    private final static int HUNDRED_PERC = 100;
    private
    @DrawableRes
    int mainImageId = NO_IMAGE;
    private
    @DrawableRes
    int fillerImageId = NO_IMAGE;

    private Bitmap mainImage;
    private Bitmap fillerImage;
    private Rect fillerRect;
    private Rect fillerOutRect;
    private Pair<Integer, Integer> imageCoords;

    @Setter
    private OnFillChangedListener onFillChangedListener;
    @Getter
    @Setter
    private boolean animateTouch;

    private boolean isTouched;

    @Getter
    private int maxFill = 1;

    @Getter
    private int currentFill = 0;

    private int fillPerc = 0;

    public FillableView(Context context)
    {
        super(context);
    }

    public FillableView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initAttrs(attrs);
    }

    public FillableView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs)
    {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FillableView,
                0, 0);
        try
        {
            maxFill = a.getInt(R.styleable.FillableView_max_fill, maxFill);
            currentFill = a.getInt(R.styleable.FillableView_current_fill, currentFill);
            animateTouch = a.getBoolean(R.styleable.FillableView_animate_touch, false);
            int mainImageId = a.getResourceId(R.styleable.FillableView_main_image, 0);
            setMainImage(mainImageId);
            int fillerImageId = a.getResourceId(R.styleable.FillableView_filler_image, 0);
            setFillerImage(fillerImageId);
        }
        finally
        {
            a.recycle();
        }
    }

    @DebugLog
    private void initResources()
    {
        setMainImage(mainImageId);
        setFillerImage(fillerImageId);

        processFill(fillPerc, false);
    }

    @DebugLog
    public void setMainImage(@DrawableRes int mainImageId)
    {
        this.mainImageId = mainImageId;
        if (mainImageId == 0)
            mainImage = null;
        else if (getPaddedWidth() > 0 && getPaddedHeight() > 0)
            mainImage = createScaledImage(mainImageId, getPaddedWidth(), getPaddedHeight());

        if (mainImage != null)
            imageCoords = calculateImageCoords(mainImage);
        //log("Main image res => " + this.mainImageId);

        invalidate();
    }

    @DebugLog
    public void setFillerImage(@DrawableRes int fillerImageRes)
    {
        this.fillerImageId = fillerImageRes;
        if (fillerImageRes == 0)
            fillerImage = null;
        else if (getPaddedWidth() > 0 && getPaddedHeight() > 0)
            fillerImage = createScaledImage(fillerImageRes, getPaddedWidth(), getPaddedHeight());

        if (fillerImage != null)
        {
            fillerOutRect = calculateImageRect(fillerImage);
            fillerRect = new Rect(0, 0, fillerImage.getWidth(), fillerImage.getHeight());
        }

        //log("Filler image id => " + this.fillerImageId);

        invalidate();
    }

    @Override
    @DebugLog
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        if (getPaddedHeight() > 0 && getPaddedWidth() > 0)
            initResources();
    }

    @DebugLog
    private Bitmap createScaledImage(@DrawableRes int imageId, int maxWidth, int maxHeight)
    {
        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(), imageId);
        float iconRatio = (float) icon.getWidth() / (float) icon.getHeight();
        float canvasRatio = (float) maxWidth / (float) maxHeight;
        boolean useMaxWidth = canvasRatio < iconRatio;
        int finalWidth = useMaxWidth ? maxWidth : (int) (maxHeight * iconRatio);
        int finalHeight = useMaxWidth ? (int) (maxWidth / iconRatio) : maxHeight;

        return Bitmap.createScaledBitmap(icon, finalWidth, finalHeight, true);
    }

    private Pair<Integer, Integer> calculateImageCoords(Bitmap image)
    {
        return new Pair<>((getPaddedWidth() - image.getWidth()) / 2, (getPaddedHeight() - image.getHeight()) / 2);
    }

    private Rect calculateImageRect(Bitmap image)
    {
        Pair<Integer, Integer> left_top = calculateImageCoords(image);
        return new Rect(left_top.first, left_top.second, left_top.first + image.getWidth(), left_top.second + image.getHeight());
    }

    @DebugLog
    private void processFill(int fillPercent, boolean userInteraction)
    {
        this.fillPerc = fillPercent;
        currentFill = Math.round(maxFill * (float) fillPercent / HUNDRED_PERC);
        if (onFillChangedListener != null)
            onFillChangedListener.onFillChanged(currentFill, userInteraction);
        if (fillerRect != null && fillerOutRect != null)
        {
            fillerRect.top = fillerRect.bottom - fillerImage.getHeight() * fillPercent / HUNDRED_PERC;
            fillerOutRect.top = fillerOutRect.bottom - fillerImage.getHeight() * fillPercent / HUNDRED_PERC;
            invalidate();
        }
    }

    @DebugLog
    public void setMaxFill(int maxFill)
    {
        this.maxFill = maxFill;
        processFill(fillPerc, false);
    }

    @DebugLog
    public void setCurrentFill(int currentFill)
    {
        processFill(currentFill * HUNDRED_PERC / maxFill, false);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (fillerImage != null)
            canvas.drawBitmap(fillerImage, fillerRect, fillerOutRect, null);

        if (mainImage != null)
            canvas.drawBitmap(mainImage, imageCoords.first, imageCoords.second, null);

        if (!isTouched)
        {
            int currentFillPerc = currentFill * HUNDRED_PERC / maxFill;
            int percDiff = fillPerc - currentFillPerc;
            if (percDiff == 0)
                return;
            else if (percDiff > FILL_PERC_MOVE)
                processFill(fillPerc - FILL_PERC_MOVE, false);
            else if (percDiff < -FILL_PERC_MOVE)
                processFill(fillPerc + FILL_PERC_MOVE, false);
            else
                processFill(currentFillPerc, false);
        }
    }

    private int getPaddedWidth()
    {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getPaddedHeight()
    {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private void log(String message)
    {
        Log.d(FillableView.class.getSimpleName(), message);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!animateTouch)
            return super.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            isTouched = true;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            isTouched = false;
            if (currentFill * HUNDRED_PERC / maxFill != fillPerc)
                invalidate();
        }

        log("Is touched => " + isTouched);
        if (event.getAction() != MotionEvent.ACTION_MOVE)
        {
            super.onTouchEvent(event);
            return true;
        }

        int height = fillerImage.getHeight();
        int translatedY = (int) (event.getY() - (fillerOutRect.bottom - height));
        int perc;
        if (translatedY < 0)
            perc = 100;
        else if (translatedY > height)
            perc = 0;
        else
            perc = ((height - translatedY) * 100 / height);
        log(String.format("Event y => %s, translated y => %s bottom => %s height => %s",
                event.getY(), translatedY, fillerOutRect.bottom, height));

        if (perc != fillPerc)
            processFill(perc, true);
        return super.onTouchEvent(event);
    }

    public interface OnFillChangedListener
    {
        void onFillChanged(int currentFill, boolean userInteraction);
    }
}
