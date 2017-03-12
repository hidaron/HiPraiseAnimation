# HiPraiseAnimation
一个可以在异步线程绘制点赞动画（不仅是）的库，可以实现映客直播直播间的点赞效果，它支持使用SurfaceView或者TextureView来绘制，当然，也可以是用View在UI线程绘制动画 。动画的绘制和绘制的过程分离，可以支持任意的动画实现。这里还附带点小福利，这里包含了一个点赞动画的实现，使用了三阶赛贝尔曲线来描述动画的运行
轨迹。

![image](https://github.com/hidaron/HiPraiseAnimation/blob/master/demo.gif) 

## 怎么使用（超级简单）

###1.首先把View添加到布局中
````
  <org.limlee.hipraiseanimationlib.HiPraiseAnimationView
        android:id="@+id/praise_animation"
        android:layout_width="200dp"
        android:layout_height="400dp"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"/>
````
###2.要在适当的时机启动动画的绘制，例如在Activity的OnResume()和OnStop()中开启和关闭动画的绘制
````
    @Override
    protected void onResume() {
        super.onResume();
        mHiPraiseAnimationView.start(); //添加点赞动画之前要先开始启动绘制，如果没有，是添加不了任何的动画对象
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHiPraiseAnimationView.stop(); //停止绘制点赞动画，停止后会clear掉整个画布和清空掉所有绘制的对象
    }
````
###3.最后，可以添加动画（点赞）对象了，支持下面两个方式

####1.普通的添加方法
````
    /**
     * 添加点赞动画，没有绘制结果的回调
     */
    private void addPraise() {
        final IPraise hiPraise = new HiPraise(getHeartBitmap());
        mHiPraiseAnimationView.addPraise(hiPraise);
    }
````
####2.带有动画绘制结束回调的添加方法
````
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
````
