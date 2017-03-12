package org.limlee.hipraiseanimation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import org.limlee.hipraiseanimationlib.HiPraise;
import org.limlee.hipraiseanimationlib.HiPraiseAnimationView;
import org.limlee.hipraiseanimationlib.HiPraiseWithCallback;
import org.limlee.hipraiseanimationlib.OnDrawCallback;
import org.limlee.hipraiseanimationlib.base.IPraise;

import java.lang.ref.SoftReference;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int HEARDS[] = new int[]{
            R.mipmap.heart_1,
            R.mipmap.heart_2,
            R.mipmap.heart_3,
            R.mipmap.heart_4,
            R.mipmap.heart_5,
            R.mipmap.heart_6
    };
    private SparseArray<SoftReference<Bitmap>> mBitmapCacheArray = new SparseArray<>();
    private HiPraiseAnimationView mHiPraiseAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHiPraiseAnimationView = (HiPraiseAnimationView) findViewById(R.id.praise_animation);
        mHiPraiseAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPraiseWithCallback();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHiPraiseAnimationView.start(); //添加点赞动画之前要先开始启动绘制
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHiPraiseAnimationView.stop(); //停止绘制点赞动画
    }

    /**
     * 添加点赞动画
     */
    private void addPraise() {
        final IPraise hiPraise = new HiPraise(getHeartBitmap());
        mHiPraiseAnimationView.addPraise(hiPraise);
    }

    /**
     * 添加具有回调的点赞动画
     */
    private void addPraiseWithCallback() {
        final IPraise hiPraiseWithCallback = new HiPraiseWithCallback(getHeartBitmap(),
                new OnDrawCallback() {
                    @Override
                    public void onFinish() {
                        Log.d(TAG, "绘制完成了！");
                    }
                });
        mHiPraiseAnimationView.addPraise(hiPraiseWithCallback);
    }

    private Bitmap getHeartBitmap() {
        final int id = HEARDS[new Random().nextInt(HEARDS.length)];
        SoftReference<Bitmap> bitmapRef = mBitmapCacheArray.get(id);
        Bitmap retBitmap = null;
        if (null != bitmapRef) {
            retBitmap = bitmapRef.get();
        }
        if (null == retBitmap) {
            retBitmap = BitmapFactory.decodeResource(getResources(),
                    id);
            mBitmapCacheArray.put(id, new SoftReference<>(retBitmap));
        }
        return retBitmap;
    }
}
