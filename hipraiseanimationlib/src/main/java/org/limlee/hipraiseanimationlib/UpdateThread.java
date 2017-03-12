package org.limlee.hipraiseanimationlib;

/**
 * @author lim lee 2017/3/10
 */
public class UpdateThread extends Thread {
    volatile boolean mIsQuited;

    public UpdateThread(String name) {
        super(name);
    }

    public void quit() {
        mIsQuited = true;
    }

    public boolean isQuited() {
        return mIsQuited;
    }

    @Override
    public void run() {
        if (mIsQuited) return;
    }

}
