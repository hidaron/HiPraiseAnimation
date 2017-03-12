package org.limlee.hipraiseanimationlib;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;


public class PraiseWithCallbackDrawable extends PraiseDrawable implements OnDrawCallback {

    private OnDrawCallback mOnDrawCallback;

    public PraiseWithCallbackDrawable(@NonNull Bitmap bitmap, float scale, float alpha, long duration,
                                      long delay, long delayAphaAnimTime, float endYPercentage, OnDrawCallback onDrawCallback) {
        super(bitmap, scale, alpha, duration, delay, delayAphaAnimTime, endYPercentage);
        mOnDrawCallback = onDrawCallback;
    }

    @Override
    public void onFinish() {
        if (null != mOnDrawCallback) {
            mOnDrawCallback.onFinish();
        }
    }
}
