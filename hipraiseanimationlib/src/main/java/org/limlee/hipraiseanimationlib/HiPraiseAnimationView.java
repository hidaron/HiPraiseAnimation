package org.limlee.hipraiseanimationlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.limlee.hipraiseanimationlib.base.IDrawTask;
import org.limlee.hipraiseanimationlib.base.IDrawable;
import org.limlee.hipraiseanimationlib.base.IPraise;
import org.limlee.hipraiseanimationlib.base.IPraiseView;

public class HiPraiseAnimationView extends SurfaceView implements IPraiseView, SurfaceHolder.Callback {
    private static final String TAG = HiPraiseAnimationView.class.getSimpleName();
    private static final int MAX_UPDATE_RATE = 25; //刷新频率
    private boolean mIsUpdateThreadStarted;
    private volatile boolean mIsSurfaceCreated;
    private IDrawTask mDrawTask;
    private UpdateThread mUpdateThread;
    private int mUpdateRate = MAX_UPDATE_RATE;
    private boolean mIsAttached;
    private int mSurfaceWidth;
    private int mSurfaceHeight;

    public HiPraiseAnimationView(Context context) {
        this(context, null);
    }

    public HiPraiseAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            setZOrderMediaOverlay(true);
            setZOrderOnTop(true);
        }
        setWillNotCacheDrawing(true);
        setDrawingCacheEnabled(false);
        setWillNotDraw(true);
        getHolder().setFormat(PixelFormat.TRANSPARENT);//透明背景
        getHolder().addCallback(this);
        mDrawTask = new SimpleDrawTask(new Handler(Looper.getMainLooper()));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttached = true;
        mDrawTask.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttached = false;
        mDrawTask.stop();
        stop();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsSurfaceCreated = true;
        clearSurface();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsSurfaceCreated = false;
    }

    /**
     * @return 绘制耗时
     */
    private long drawSurface() {
        if (!mIsSurfaceCreated) {
            return 0;
        }
        if (mSurfaceWidth == 0
                || mSurfaceHeight == 0) {
            return 0;
        }
        if (!isShown()) {
            mDrawTask.clearDrawable(); //清除绘制对象
            clearSurface();
            return 0;
        }
        final long startTime = SystemClock.uptimeMillis();
        Canvas canvas = getHolder().lockCanvas();
        if (null != canvas) {
            mDrawTask.draw(canvas); //绘制点赞动画
            if (mIsSurfaceCreated) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
        return SystemClock.uptimeMillis() - startTime;
    }

    private void clearSurface() {
        if (mIsSurfaceCreated) {
            Canvas canvas = getHolder().lockCanvas();
            if (null != canvas) {
                canvas.drawColor(Color.TRANSPARENT,
                        android.graphics.PorterDuff.Mode.CLEAR);
                if (mIsSurfaceCreated) {
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public synchronized void start() {
        if (mIsUpdateThreadStarted) return;
        if (null == mUpdateThread) {
            mUpdateThread = new UpdateThread("Update Thread") {

                @Override
                public void run() {
                    try {
                        while (!isQuited()
                                && !Thread.currentThread().isInterrupted()) {
                            final long cost = mUpdateRate - drawSurface();
                            if (isQuited()) {
                                break;
                            }
                            if (cost > 0) {
                                SystemClock.sleep(cost);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        HiPraiseAnimationView.this.stop();
                    }
                }
            };
        }
        mIsUpdateThreadStarted = true;
        mUpdateThread.start();
    }

    public synchronized void stop() {
        mIsUpdateThreadStarted = false;
        mDrawTask.clearDrawable();
        if (null != mUpdateThread) {
            UpdateThread thread = mUpdateThread;
            mUpdateThread = null;
            thread.quit();
            thread.interrupt();
        }
    }

    @Override
    public void addPraise(IPraise praise) {
        if (!mIsAttached
                || !mIsUpdateThreadStarted) return;
        final IDrawable drawable = praise.toDrawable();
        if (null != drawable) {
            mDrawTask.addDrawable(drawable);
        }
    }
}
