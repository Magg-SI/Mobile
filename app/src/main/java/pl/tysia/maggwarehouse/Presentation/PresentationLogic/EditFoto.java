package pl.tysia.maggwarehouse.Presentation.PresentationLogic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class EditFoto extends View {

    public static final int MODE_CROP = 2;
    public static final int MODE_SCALE = 1;

    private static final int CROP_RECT_OFFSET = 7;

    private boolean isMultiTouch=false;
    private boolean isTestMode=false;
    private int mode =1;
    private int typMoving=0;
    private Rect mainRect, movedRect, maxRect, wynikRect;
    private Rect bmpRectSrc, bmpRectSel, bmpRectStart, bmpRectView;
    private Rect rectLeft, rectTop, rectRight, rectBottom;
    private Rect rectLeftT, rectTopT, rectRightT, rectBottomT;
    private Paint mPaintR, mPaintM, mPaintDark;
    private Point moveStart, moveEnd, minSize, centerBmp, movedBmp;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetectorCompat mDetector;
    private float mScaleBmp, mScaleRect;
    private Bitmap bitmapIn, bitmapOut;

    public EditFoto(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        mDetector = new GestureDetectorCompat(context, new MyGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onTouchEventSized(event);
        mScaleGestureDetector.onTouchEvent(event);
        mDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mode==0) canvas.drawBitmap(bitmapOut, wynikRect, new Rect(0,0,300,300), null);
        if(mode==1) canvas.drawBitmap(bitmapIn, bmpRectSrc, bmpRectView, null);
        if(mode==2) {
            canvas.drawBitmap(bitmapIn, bmpRectSrc, bmpRectView, mPaintDark);
            canvas.drawBitmap(bitmapIn, bmpRectSel, mainRect, null);
            canvas.drawRect(mainRect, mPaintR);
            canvas.drawRect(rectLeft, mPaintM);
            canvas.drawRect(rectTop, mPaintM);
            canvas.drawRect(rectRight, mPaintM);
            canvas.drawRect(rectBottom, mPaintM);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        maxRect = new Rect(0, 0, w, h);
        resizeRect();
        resizeBmp();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setMode(int amod) {
        mode=amod;
        if(mode==2) {
            setMainRect();
            countSmallRects();
            countZoom();
        }
        invalidate();
    }
    public void setBitmap(Bitmap bitmap) {
        bitmapIn = bitmap;
        resizeRect();
        resizeBmp();
        invalidate();
    }
    public Bitmap getBitmap() {
        if(mode==1) {
            mainRect.left = maxRect.left;
            mainRect.top  = maxRect.top;
            mainRect.right = maxRect.right;
            mainRect.bottom   = maxRect.bottom;
            setMainRect();
            countZoom();
        }
        wynikRect = new Rect(0,0, bmpRectSel.width(), bmpRectSel.height());
        bitmapOut = Bitmap.createBitmap(bmpRectSel.width(), bmpRectSel.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapOut);
        canvas.drawBitmap(bitmapIn, bmpRectSel, wynikRect, null);

        if(isTestMode) {
            mode = 0;
            invalidate();
        }

        return bitmapOut;
    }

    private void moving() {
        if(mode==1) movingBmp();
        else if(typMoving==1) movingLeft();
        else if(typMoving==2) movingTop();
        else if(typMoving==3) movingRight();
        else if(typMoving==4) movingBottom();
        else movingRect();

        countSmallRects();
        invalidate();
    }
    private void movingBmp() {
        centerBmp.x = movedBmp.x + moveEnd.x - moveStart.x;
        centerBmp.y  = movedBmp.y + moveEnd.y - moveStart.y;
        scaleBmpRect();
    }
    private void movingLeft() {
        mainRect.left  = movedRect.left + moveEnd.x - moveStart.x;
        if(mainRect.width()<minSize.x) mainRect.left = mainRect.right-minSize.x;
        if (mainRect.left < maxRect.left) mainRect.left = maxRect.left;
        if (mainRect.left < bmpRectView.left) mainRect.left = bmpRectView.left;
        countZoom();
    }
    private void movingTop() {
        mainRect.top = movedRect.top + moveEnd.y - moveStart.y;
        if(mainRect.height()<minSize.y) mainRect.top = mainRect.bottom-minSize.y;
        if (mainRect.top < maxRect.top) mainRect.top = maxRect.top;
        if (mainRect.top  < bmpRectView.top) mainRect.top  = bmpRectView.top;
        countZoom();
    }
    private void movingRight() {
        mainRect.right = movedRect.right + moveEnd.x - moveStart.x;
        if(mainRect.width()<minSize.x) mainRect.right = mainRect.left+minSize.x;
        if (mainRect.right > maxRect.right) mainRect.right = maxRect.right;
        if (mainRect.right > bmpRectView.right) mainRect.right = bmpRectView.right;
        countZoom();
    }
    private void movingBottom() {
        mainRect.bottom = movedRect.bottom + moveEnd.y - moveStart.y;
        if(mainRect.height()<minSize.y) mainRect.bottom = mainRect.top+minSize.y;
        if (mainRect.bottom > maxRect.bottom) mainRect.bottom = maxRect.bottom;
        if (mainRect.bottom > bmpRectView.bottom) mainRect.bottom = bmpRectView.bottom;
        countZoom();
    }
    private void movingRect() {
        mainRect.left = movedRect.left + moveEnd.x - moveStart.x;
        mainRect.top  = movedRect.top + moveEnd.y - moveStart.y;
        if (mainRect.left < maxRect.left) mainRect.left = maxRect.left;
        if (mainRect.top  < maxRect.top) mainRect.top  = maxRect.top;
        if (mainRect.left < bmpRectView.left) mainRect.left = bmpRectView.left;
        if (mainRect.top  < bmpRectView.top) mainRect.top  = bmpRectView.top;

        if ((mainRect.left + movedRect.width()) > maxRect.right) mainRect.left = maxRect.right - movedRect.width();
        if ((mainRect.top  + movedRect.height()) > maxRect.bottom) mainRect.top  = maxRect.bottom - movedRect.height();
        if ((mainRect.left + movedRect.width()) > bmpRectView.right) mainRect.left = bmpRectView.right - movedRect.width();
        if ((mainRect.top  + movedRect.height()) > bmpRectView.bottom) mainRect.top  = bmpRectView.bottom - movedRect.height();

        mainRect.right = mainRect.left + movedRect.width();
        mainRect.bottom = mainRect.top  + movedRect.height();

        countZoom();
    }
    private void scaleBmpRect() {
        int szer  = Math.round(mScaleBmp*bmpRectStart.width());
        int wys = Math.round(mScaleBmp*bmpRectStart.height());
        int srodekL=maxRect.left  + 2 * maxRect.width() / 3;
        int srodekT=maxRect.top  + 2 *maxRect.height() / 3;
        int srodekR=maxRect.left  + maxRect.width() / 3;
        int srodekB=maxRect.top  + maxRect.height() / 3;

        if(centerBmp.x - (szer/2) > srodekL) centerBmp.x=srodekL+szer/2;
        if(centerBmp.y - (wys/2) > srodekT) centerBmp.y=srodekT+wys/2;
        if(centerBmp.x + (szer/2) < srodekR) centerBmp.x=srodekR-szer/2;
        if(centerBmp.y + (wys/2) < srodekB) centerBmp.y=srodekB-wys/2;

        bmpRectView.left = centerBmp.x - (szer/2);
        bmpRectView.top  = centerBmp.y - (wys/2);
        bmpRectView.right = bmpRectView.left+szer;
        bmpRectView.bottom = bmpRectView.top+wys;
    }

    private void scaleMainRect() {
        int centerX = (movedRect.right+movedRect.left)/2;
        int centerY = (movedRect.bottom+movedRect.top)/2;

        int szer  = (int)(mScaleRect*movedRect.width());
        int wys = (int)(mScaleRect*movedRect.height());

        mainRect.left = centerX - (szer/2);
        mainRect.top  = centerY - (wys/2);

        mainRect.right = mainRect.left+szer;
        mainRect.bottom = mainRect.top+wys;

        if (mainRect.left < maxRect.left) mainRect.left = maxRect.left;
        if (mainRect.top  < maxRect.top) mainRect.top  = maxRect.top;
        if (mainRect.left < bmpRectView.left) mainRect.left = bmpRectView.left;
        if (mainRect.top  < bmpRectView.top) mainRect.top  = bmpRectView.top;

        if (mainRect.right > maxRect.right) mainRect.right = maxRect.right;
        if (mainRect.bottom  > maxRect.bottom) mainRect.bottom  = maxRect.bottom;
        if (mainRect.right > bmpRectView.right) mainRect.right = bmpRectView.right;
        if (mainRect.bottom  > bmpRectView.bottom) mainRect.bottom  = bmpRectView.bottom;

        countSmallRects();
        countZoom();
    }


    private void init() {
        mScaleBmp = 1.0f;
        mScaleRect = 1.0f;

        mPaintR = new Paint();
        mPaintR.setColor(Color.GRAY);
        mPaintR.setStrokeWidth(3);
        mPaintR.setStyle(Paint.Style.STROKE);

        mPaintM = new Paint();
        mPaintM.setColor(Color.BLACK);

        mPaintDark = new Paint(Color.RED);
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
        ColorFilter filter = new LightingColorFilter(0xFF3F3F3F, 0x00000000);    // darken
        mPaintDark.setColorFilter(filter);

        moveStart = new Point();
        moveEnd = new Point();
        minSize = new Point(150, 150);

        movedRect = new Rect();
        rectLeft = new Rect();
        rectTop = new Rect();
        rectRight = new Rect();
        rectBottom = new Rect();
        rectLeftT = new Rect();
        rectTopT = new Rect();
        rectRightT = new Rect();
        rectBottomT = new Rect();
        maxRect = new Rect(0, 0, 200, 200);
        resizeRect();

        if(isTestMode) bitmapIn = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/100ANDRO/DSC_0017.jpg");
        else bitmapIn = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        //getBitmap();
        resizeBmp();
    }

    private void resizeRect() {
        int margX = 0;
        int margY = 0;
        mainRect = new Rect(margX, margY, maxRect.right - margX, maxRect.bottom - margY);
        minSize = new Point(maxRect.width()/4, maxRect.width()/4);
        movedBmp = new Point();
        centerBmp = new Point((maxRect.right + maxRect.left) / 2, (maxRect.bottom + maxRect.top) / 2);
        countSmallRects();
    }

    private void resizeBmp() {
        int maxX = maxRect.right - maxRect.left;
        int maxY = maxRect.bottom - maxRect.top;

        bmpRectSrc = new Rect();
        bmpRectSrc = new Rect(0,0,bitmapIn.getWidth(),bitmapIn.getHeight());
        bmpRectStart = new Rect(0, 0, maxX, maxY);
        double scaleB =(1.0*bmpRectSrc.width())/(1.0*bmpRectSrc.height());
        double scaleV =(1.0*bmpRectStart.width())/(1.0*bmpRectStart.height());

        if(scaleB>scaleV) {
            int x = (int)(bmpRectStart.right / scaleB);
            bmpRectStart.top =(maxY -x)/2;
            bmpRectStart.bottom =bmpRectStart.top+x;
        }
        else {
            int x= (int)(bmpRectStart.bottom * scaleB);
            bmpRectStart.left  =(maxX  -x)/2;
            bmpRectStart.right =bmpRectStart.left+x;
        }

        bmpRectView = new Rect(bmpRectStart);
        bmpRectSel = new Rect(bmpRectSrc);
    }

    private void countSmallRects() {
        Point middle = new Point(mainRect.left+(mainRect.width()/2), mainRect.top+(mainRect.height()/2));
        countSmallRect(rectLeft,mainRect.left, middle.y, 1);
        countSmallRect(rectTop,middle.x ,mainRect.top, 2);
        countSmallRect(rectRight,mainRect.right, middle.y, 3);
        countSmallRect(rectBottom,middle.x ,mainRect.bottom, 4);
    }

    private void countSmallRect(Rect rectM, int x, int y, int type) {
        int size=(int)(maxRect.width()/50);
        int mnozLine=11;
        int mnozOut=5;
        int mnozIn=4;

        rectM.left = x-size;
        rectM.top = y-size;
        rectM.right = x+size;
        rectM.bottom = y+size;

        if(type==1) {
            rectLeftT.left = x - mnozOut * size;
            rectLeftT.top = y - mnozLine * size;
            rectLeftT.right = x + mnozIn * size;
            rectLeftT.bottom = y + mnozLine * size;
        }
        else if(type==2) {
            rectTopT.left = x - mnozLine * size;
            rectTopT.top = y - mnozOut * size;
            rectTopT.right = x + mnozLine * size;
            rectTopT.bottom = y + mnozIn * size;
        }
        else if(type==3) {
            rectRightT.left = x - mnozIn * size;
            rectRightT.top = y - mnozLine * size;
            rectRightT.right = x + mnozOut * size;
            rectRightT.bottom = y + mnozLine * size;
        }
        else if(type==4) {
            rectBottomT.left = x - mnozLine * size;
            rectBottomT.top = y - mnozIn * size;
            rectBottomT.right = x + mnozLine * size;
            rectBottomT.bottom = y + mnozOut * size;
        }
    }

    private void countZoom() {

        double left = (1.0 * (mainRect.left - bmpRectView.left)) / (1.0 * bmpRectView.width());
        double right = (1.0 * (mainRect.right - bmpRectView.left)) / (1.0 * bmpRectView.width());
        double top = (1.0 * (mainRect.top  - bmpRectView.top)) / (1.0 * bmpRectView.height());
        double bottom = (1.0 * (mainRect.bottom  - bmpRectView.top)) / (1.0 * bmpRectView.height());

        bmpRectSel.left = (int)Math.round(left * (1.0 * bmpRectSrc.width()));
        bmpRectSel.right = (int)Math.round(right * (1.0 * bmpRectSrc.width()));
        bmpRectSel.top = (int)Math.round(top * (1.0 * bmpRectSrc.height()));
        bmpRectSel.bottom = (int)Math.round(bottom * (1.0 * bmpRectSrc.height()));
    }

    private void setMainRect() {
        if (mainRect.left < bmpRectView.left) mainRect.left = bmpRectView.left;
        if (mainRect.top  < bmpRectView.top) mainRect.top  = bmpRectView.top;
        if (mainRect.right > bmpRectView.right) mainRect.right = bmpRectView.right;
        if (mainRect.bottom  > bmpRectView.bottom) mainRect.bottom   = bmpRectView.bottom;
        countSmallRects();
    }

    private void testMoveType() {
        if(mode==1) typMoving=0;
        else if(rectLeftT.contains(moveStart.x,moveStart.y)) typMoving=1;
        else if(rectTopT.contains(moveStart.x,moveStart.y)) typMoving=2;
        else if(rectRightT.contains(moveStart.x,moveStart.y)) typMoving=3;
        else if(rectBottomT.contains(moveStart.x,moveStart.y)) typMoving=4;
        else typMoving=0;
    }

    private void onTouchEventSized(MotionEvent event) {

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                movedBmp.x = centerBmp.x;
                movedBmp.y = centerBmp.y;

                mScaleRect=1;
                movedRect.left = mainRect.left;
                movedRect.top = mainRect.top;
                movedRect.right = mainRect.right;
                movedRect.bottom = mainRect.bottom;

                moveStart.x = (int) event.getX();
                moveStart.y = (int) event.getY();
                testMoveType();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                isMultiTouch=true;
                break;

            case MotionEvent.ACTION_MOVE:

                moveEnd.x = (int) event.getX();
                moveEnd.y = (int) event.getY();
                if(!isMultiTouch) moving();
                break;

            case MotionEvent.ACTION_UP:

                moveEnd.x = (int) event.getX();
                moveEnd.y = (int) event.getY();
                if(!isMultiTouch) moving();
                isMultiTouch=false;
                break;

            default:

                super.onTouchEvent(event);
                break;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            if(mode==1) {
                mScaleBmp *= scaleGestureDetector.getScaleFactor();
                mScaleBmp = Math.max(1.0f, Math.min(mScaleBmp, 5.0f));
                scaleBmpRect();
            }
            else
            {
                mScaleRect *= scaleGestureDetector.getScaleFactor();
                mScaleRect  = Math.max(0.3f, Math.min(mScaleRect, 3.0f));
                scaleMainRect();
            }
            invalidate();
            return true;
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            if(mode==1) mode=2;
            else if(mode==2) mode = 1;
            if(mode==2) setMainRect();
            return true;
        }
    }
}