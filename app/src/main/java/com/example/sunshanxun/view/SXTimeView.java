package com.example.sunshanxun.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import com.example.sunshanxun.utils.DeviceInfoUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SunZJ on 2016/10/22.
 */

public class SXTimeView extends View {
    private static final String TAG = "SunPickerView";

    private Context mContext;
    private Paint mPaint;
    private List<String> mDatas;
    private int mSelectedIndex;
    private float mMaxTextSize;
    private float mMinTextSize;
    private float mTextSpaceSize;
    private float mOffsetUp, mOffsetDown, mOffsetLeft, mOffsetRight;
    private float mMoveOffset;
    private Scroller mScroller;
    private int mMaxTextAlpha = 255;
    private int mMinTextAlpha = 120;
    /**
     * text之间间距和minTextSize之比
     */
    public float MARGIN_ALPHA = 2.8f;
    /**
     * 自动回滚到中间的速度
     */
    public float SPEED = 4;
    private Timer timer;
    private MyTimerTask mTask;

    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (Math.abs(mMoveOffset) < SPEED) {
                mMoveOffset = 0;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                }
            } else
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveOffset = mMoveOffset - mMoveOffset / Math.abs(mMoveOffset) * SPEED;
            invalidate();
        }

    };


    public SXTimeView(Context context) {
        super(context);
        init(context);
    }

    public SXTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SXTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        timer = new Timer();
        mScroller = new Scroller(mContext);
        mPaint = new Paint();
        mPaint.setTextAlign(Paint.Align.CENTER);
//      mPaint.setColor(Color.BLACK);
        mOffsetUp = DeviceInfoUtil.getPixelFromDip(0);
        mOffsetDown = DeviceInfoUtil.getPixelFromDip(0);
        mOffsetLeft = DeviceInfoUtil.getPixelFromDip(0);
        mOffsetRight = DeviceInfoUtil.getPixelFromDip(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mMaxTextSize = getMeasuredHeight() / 4.0f;
        mMinTextSize = mMaxTextSize / 2.0f;
        mTextSpaceSize = mMinTextSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawText(canvas, mPaint);
    }

    private void drawText(Canvas canvas, Paint paint) {
        if (mDatas == null || mSelectedIndex < 0 || mSelectedIndex > mDatas.size() - 1) {
            return;
        }


        /**
         * 比例 1~0
         */
        float scale = parabola(getHeight() / 4, mMoveOffset);
        /**
         * 中间的Text
         */
        paint.setTextSize((mMaxTextSize - mMinTextSize) * scale + mMinTextSize);
        paint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float x = getWidth() / 2;
        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
        float y = getHeight() / 2 + mMoveOffset - (fmi.top / 2.0f + fmi.bottom / 2.0f);
        canvas.drawText(mDatas.get(mSelectedIndex), x, y, paint);


        // 绘制上方data
        for (int i = 1; (mSelectedIndex - i) >= 0; i++) {
            drawOtherText(canvas, paint, i, -1);
        }
        // 绘制下方data
        for (int i = 1; (mSelectedIndex + i) < mDatas.size(); i++) {
            drawOtherText(canvas, paint, i, 1);
        }

    }


    private void drawOtherText(Canvas canvas, Paint paint, int position, int type) {
        float d = (float) (MARGIN_ALPHA * mMinTextSize * position + type
                * mMoveOffset);
        float scale = parabola(getHeight() / 4.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        paint.setTextSize(size);
        paint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float y = (float) (getHeight() / 2.0 + type * d);
        Paint.FontMetricsInt fmi = paint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDatas.get(mSelectedIndex + type * position),
                (float) (getWidth() / 2.0), baseline, paint);
    }


    float startY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveOffset += event.getY() - startY;
                startY = event.getY();

                if (mMoveOffset > MARGIN_ALPHA * mMinTextSize / 2) {
                    // 往下滑超过离开距离
                    moveTailToHead();
                    mMoveOffset = mMoveOffset - MARGIN_ALPHA * mMinTextSize;
                } else if (mMoveOffset < -MARGIN_ALPHA * mMinTextSize / 2) {
                    // 往上滑超过离开距离
                    moveHeadToTail();
                    mMoveOffset = mMoveOffset + MARGIN_ALPHA * mMinTextSize;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
//                if (mMoveOffset > 0) {
//                    smoothScroll(0, Math.abs(MARGIN_ALPHA * mMinTextSize - (mMoveOffset % (MARGIN_ALPHA * mMinTextSize))));
//                } else if (mMoveOffset < 0) {
//                    smoothScroll(0, -Math.abs(MARGIN_ALPHA * mMinTextSize - (mMoveOffset % (MARGIN_ALPHA * mMinTextSize))));
//                }

                // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
                if (Math.abs(mMoveOffset) < 0.0001) {
                    mMoveOffset = 0;
                    break;
                }
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                }
                mTask = new MyTimerTask(updateHandler);
                timer.schedule(mTask, 0, 10);

                break;
            default:
                break;
        }

        return true;
    }


    private void moveHeadToTail() {
        String head = mDatas.get(0);
        mDatas.remove(0);
        mDatas.add(head);
    }

    private void moveTailToHead() {
        String tail = mDatas.get(mDatas.size() - 1);
        mDatas.remove(mDatas.size() - 1);
        mDatas.add(0, tail);
    }

    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    /**
     * 弹性滑动
     *
     * @param destX
     * @param destY
     */
    private void smoothScroll(float destX, float destY) {
        float scrollY = getScrollY();
        int deltaY = (int) (scrollY - destY);
        mScroller.startScroll(getScrollX(), getScrollY(), 0, deltaY);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }


    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }

    }

    private float getContentWidth() {
        return getWidth() - mOffsetLeft - mOffsetRight;
    }

    private float getContentHeight() {
        return getHeight() - mOffsetUp - mOffsetDown;
    }

    public void setData(List<String> datas) {
        mDatas = datas;

        invalidate();
    }

    public void setData(List<String> datas, int selectedIndex) {
        mDatas = datas;
        this.mSelectedIndex = selectedIndex;
        invalidate();
    }

    public String getSelectValue() {
        if (mDatas == null) {
            return null;
        }
        return mDatas.get(mSelectedIndex);
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(int mSelectedIndex) {
        this.mSelectedIndex = mSelectedIndex;
    }
}
