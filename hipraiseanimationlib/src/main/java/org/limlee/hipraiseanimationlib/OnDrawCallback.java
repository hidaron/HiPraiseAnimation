package org.limlee.hipraiseanimationlib;

/**
 * Created by apple on 16/10/20.
 */
public interface OnDrawCallback {

    /**
     * 主线程回调，不能在这里处理耗时的操作
     */
    void onFinish();

}
