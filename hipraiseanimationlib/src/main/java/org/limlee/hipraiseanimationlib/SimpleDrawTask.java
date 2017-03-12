package org.limlee.hipraiseanimationlib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import org.limlee.hipraiseanimationlib.base.IDrawTask;
import org.limlee.hipraiseanimationlib.base.IDrawable;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class SimpleDrawTask implements IDrawTask {
    private static final String TAG = SimpleDrawTask.class.getSimpleName();
    private static final int MAX_DRAWABLES = 128; //最多显示绘制对象
    private static RectF RECT = new RectF();
    private static Paint PAINT = new Paint();
    private int mDrawables = MAX_DRAWABLES;
    private BlockingQueue<IDrawable> mDrawableQueue;
    private Handler mCallbackHandler;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private boolean mIsStarted;

    static {
        PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        PAINT.setColor(Color.TRANSPARENT);
    }

    public SimpleDrawTask(Handler callbackHandler) {
        mCallbackHandler = callbackHandler;
        mDrawableQueue = new ArrayBlockingQueue<>(mDrawables);
    }

    @Override
    public void start() {
        if (mIsStarted) return;
        if (null == mHandlerThread) {
            mHandlerThread = new HandlerThread("DrawTask HandlerThread");
            mHandlerThread.start();
        }
        if (null == mHandler) {
            mHandler = new Handler(mHandlerThread.getLooper());
        }
        mIsStarted = true;
    }

    @Override
    public void stop() {
        mIsStarted = false;
        if (null != mCallbackHandler) {
            mCallbackHandler.removeCallbacksAndMessages(null);
        }
        mHandler.removeCallbacksAndMessages(null);
        HandlerThread handlerThread = mHandlerThread;
        mHandlerThread = null;
        handlerThread.quit();
        try {
            handlerThread.join(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handlerThread.interrupt();
    }

    @Override
    public void draw(Canvas canvas) {
        clearCanvas(canvas);
        consumeDrawableQueue(canvas);
    }

    private void clearCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT,
                PorterDuff.Mode.CLEAR);
        RECT.set(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawRect(RECT, PAINT);
    }

    @Override
    public void addDrawable(final IDrawable drawable) {
        if (mIsStarted
                && null != mHandler) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        mDrawableQueue.offer(drawable);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void clearDrawable() {
        mDrawableQueue.clear();
    }

    private void consumeDrawableQueue(Canvas canvas) {
        Iterator<IDrawable> drawableIterator = mDrawableQueue.iterator();
        while (drawableIterator.hasNext()) {
            final IDrawable drawable = drawableIterator.next();
            if (null != drawable) {
                long currentTime = SystemClock.uptimeMillis();
                drawable.draw(canvas, currentTime);
                if (drawable.isFinished()) {
                    if (null != mCallbackHandler
                            && drawable instanceof OnDrawCallback) {
                        mCallbackHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ((OnDrawCallback) drawable).onFinish();
                            }
                        });
                    }
                    drawableIterator.remove();
                }
            }
        }
    }
}
