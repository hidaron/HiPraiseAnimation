package org.limlee.hipraiseanimationlib;

import android.graphics.Bitmap;

import org.limlee.hipraiseanimationlib.base.IDrawable;


public class HiPraiseWithCallback extends HiPraise {

    private OnDrawCallback mOnDrawCallback;

    public HiPraiseWithCallback(Bitmap bitmap, OnDrawCallback onDrawCallback) {
        super(bitmap);
        mOnDrawCallback = onDrawCallback;
    }

    @Override
    public IDrawable toDrawable() {
        return new PraiseWithCallbackDrawable(bitmap, scale,
                alpha, duration, startDelay, delayAplhaTime, 0.45f, mOnDrawCallback);
    }
}
