package org.limlee.hipraiseanimationlib;

import android.animation.FloatEvaluator;
import android.animation.TimeInterpolator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import org.limlee.hipraiseanimationlib.base.IDrawable;

import java.util.Random;

public class PraiseDrawable implements IDrawable {
    private static final String TAG = PraiseDrawable.class.getSimpleName();
    private static final long MIN_END_POINTY = 16;
    private Bitmap bitmap;
    private Matrix mMatrix;
    private Paint mPaint;
    private PointF curPoint;
    private PointF startPoint;
    private PointF endPoint;

    private int bitmapWidth;
    private int bitmapHeight;
    private int canvasWidth;
    private int canvasHeight;

    private float scale;
    private float alpha;
    private long duration;
    private long startDelay;
    private long startFrameTime;
    private long startAplhaAnimTime;
    private long endFrameTime;
    private long delayAphaAnimTime;
    private float endYPercentage;

    private BezierEvaluator xFrameEvaluator;
    private FloatEvaluator mScaleEvaluator;
    private FloatEvaluator mAlphaEvaluator;
    private TimeInterpolator mFrameTimeInterpolator;
    private TimeInterpolator mScaleTimeInterpolator;
    private TimeInterpolator mAlphaTimeInterpolator;
    private boolean isFinished; //是否绘制完成了
    private boolean isStarted; //是否开始绘制了

    public PraiseDrawable(@NonNull Bitmap bitmap,
                          float scale, float alpha, long duration, long delay,
                          long delayAphaAnimTime, float endYPercentage) {
        mMatrix = new Matrix();
        mPaint = new Paint();
        this.bitmap = bitmap;
        this.endYPercentage = Math.min(Math.max(0, endYPercentage), 1);

        /**
         * params
         *
         */
        this.scale = scale;
        this.alpha = alpha;
        this.duration = duration;
        this.startDelay = delay;
        this.delayAphaAnimTime = delayAphaAnimTime;
        bitmapWidth = (int) (bitmap.getWidth() * scale);
        bitmapHeight = (int) (bitmap.getHeight() * scale);


        /**
         * evaluator and interpolator
         */

        mScaleEvaluator = new FloatEvaluator();
        mAlphaEvaluator = new FloatEvaluator();
        mScaleTimeInterpolator = new LinearInterpolator();
        mFrameTimeInterpolator = new AccelerateInterpolator(0.8f);
        mAlphaTimeInterpolator = new DecelerateInterpolator(0.5f);
    }

    /**
     * 绘制起点
     *
     * @return
     */
    private PointF genStartPoint() {
        final float pointX = canvasWidth / 2;
        float pointY;
        if (bitmapHeight > canvasHeight) {
            pointY = canvasHeight;
        } else {
            pointY = canvasHeight - bitmapHeight;
        }
        return new PointF(pointX, pointY);
    }

    /**
     * 绘制终点
     *
     * @param point1
     * @param point2
     * @return
     */
    private PointF genEndPoint(PointF point1, PointF point2) {
        long tempEndY = getEndPointY();
        final float endX = (point1.x + point2.x) / 2;
        final float endY = tempEndY;
        return new PointF(endX, endY);
    }

    /**
     * 绘制最后的Y点，获取startPoint后调用
     *
     * @return
     */
    private long getEndPointY() {
        long tempEndY = (long) (startPoint.y * endYPercentage);
        if (tempEndY < MIN_END_POINTY) {
            tempEndY = MIN_END_POINTY;
        }
        return tempEndY;
    }

    private PointF genRandomPoint2() {
        long tempEndY = getEndPointY();
        final float middel = startPoint.x;
        final float minX = bitmapWidth / 2;
        final int maxX = canvasWidth + bitmapWidth / 2;
        float minY = (startPoint.y - tempEndY) / 3 + tempEndY;
        final int maxY = (int) ((startPoint.y - tempEndY) / 3 * 2 + tempEndY);
        float pointX;
        while ((pointX = new Random().nextInt(maxX) % (maxX - minX + 1) + minX) == middel) {
        }
        final float pointY = new Random().nextInt(maxY) % (maxY - minY + 1)
                + minY;
        return new PointF(pointX, pointY);
    }

    private PointF genRandomPoint1(PointF point2) {
        long tempEndY = getEndPointY();
        final float middel = startPoint.x;
        final float minX = bitmapWidth / 2;
        final int maxX = canvasWidth + bitmapWidth / 2;
        final float minY = tempEndY;
        final int maxY = (int) ((startPoint.y - tempEndY) / 3 + tempEndY);
        float pointX;
        if (point2.x > middel) {
            while ((pointX = new Random().nextInt(maxX) % (maxX - minX + 1)
                    + minX) >= middel) {
            }
        } else {
            while ((pointX = new Random().nextInt(maxX) % (maxX - minX + 1)
                    + minX) <= middel) {
            }
        }
        final float pointY = new Random().nextInt(maxY) % (maxY - minY + 1)
                + minY;
        return new PointF(pointX, pointY);
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void draw(Canvas canvas, long current) {
        if (null == bitmap
                || duration == 0
                || alpha == 0
                || scale == 0
                || startDelay >= duration) {
            isFinished = true;
        }
        if (!isStarted) {
            isStarted = true;
            canvasWidth = canvas.getWidth();
            canvasHeight = canvas.getHeight();
            startFrameTime = current + startDelay;
            startAplhaAnimTime = startFrameTime + delayAphaAnimTime;
            endFrameTime = startFrameTime + duration;
            startPoint = genStartPoint(); //动画开始位置
            curPoint = new PointF(startPoint.x, startPoint.y);
            PointF point2 = genRandomPoint2();
            PointF point1 = genRandomPoint1(point2);
            endPoint = genEndPoint(point1, point2);
            xFrameEvaluator = new BezierEvaluator(point2, point1);
        }
        if (current < startFrameTime) return;
        if (current >= endFrameTime) {
            isFinished = true;
        }
        if (!isFinished
                && isStarted) {
            float fraction = (current - startFrameTime) / (float) duration;
            PointF point = drawFrame(fraction, startPoint, endPoint);
            mMatrix.setTranslate(curPoint.x = (point.x - bitmapWidth / 2),
                    curPoint.y = point.y);
            float scale = drawScale(fraction);
            mMatrix.preScale(scale, scale, bitmapWidth / 2, bitmapHeight);
            mPaint.setAlpha((int) (alpha * 255));
            if (current >= startAplhaAnimTime) {
                float alphaFraction = (current - startAplhaAnimTime)
                        / (float) (endFrameTime - startAplhaAnimTime);
                mPaint.setAlpha((int) (drawAlpah(alphaFraction) * 255));
            }
            canvas.drawBitmap(bitmap, mMatrix, mPaint);
        }
    }

    /**
     * 绘制drawable的缩放
     *
     * @param fraction
     * @return
     */
    private float drawScale(float fraction) {
        float newFraction = 4.50f * fraction;
        fraction = newFraction > 1 ? 1 : newFraction;
        fraction = mScaleTimeInterpolator.getInterpolation(fraction);
        return mScaleEvaluator.evaluate(fraction, 0.0f, this.scale);
    }

    /**
     * 绘制drawable的alpha值
     *
     * @param fraction
     * @return
     */
    private float drawAlpah(float fraction) {
        fraction = mAlphaTimeInterpolator.getInterpolation(fraction);
        return mAlphaEvaluator.evaluate(fraction, this.alpha, 0.0f);
    }

    /**
     * 绘制drawable每一帧的移动位置
     *
     * @param fraction
     * @param start
     * @param end
     * @return
     */
    private PointF drawFrame(float fraction, PointF start, PointF end) {
        fraction = mFrameTimeInterpolator.getInterpolation(fraction);
        return xFrameEvaluator.evaluate(fraction, start, end);
    }

}
