package org.limlee.hipraiseanimationlib.base;

import android.graphics.Canvas;

public interface IDrawable {

    /**
     * 是否已经绘制完成
     *
     * @return
     */
    boolean isFinished();

    /**
     * 如果绘制当前动画
     *
     * @param canvas
     * @param current 当前绘制的时间
     */
    void draw(Canvas canvas, long current);

}
