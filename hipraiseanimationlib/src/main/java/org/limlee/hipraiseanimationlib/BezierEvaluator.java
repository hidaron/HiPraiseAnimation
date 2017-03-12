package org.limlee.hipraiseanimationlib;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * @author lim lee
 *         <p/>
 *         三阶贝塞尔曲线
 */
public class BezierEvaluator implements TypeEvaluator<PointF> {

    PointF pointF1;
    PointF pointF2;

    public BezierEvaluator(PointF pointF1, PointF pointF2) {
        this.pointF1 = pointF1;
        this.pointF2 = pointF2;
    }

    @Override
    public PointF evaluate(float t, PointF pointF0, PointF pointF3) {
        PointF pointF = new PointF();
        pointF.x = pointF0.x * (1 - t) * (1 - t) * (1 - t) + 3 * pointF1.x * t
                * (1 - t) * (1 - t) + 3 * pointF2.x * t * t * (1 - t)
                + pointF3.x * t * t * t;
        pointF.y = pointF0.y * (1 - t) * (1 - t) * (1 - t) + 3 * pointF1.y * t
                * (1 - t) * (1 - t) + 3 * pointF2.y * t * t * (1 - t)
                + pointF3.y * t * t * t;
        return pointF;
    }
}
