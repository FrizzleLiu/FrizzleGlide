package com.frizzle.glide.fragment;

/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 */
public interface LifecycleCallback {
    //生命周期初始化
    void glideInitAction();

    //生命周期停止
    void glideStopAction();

    //生命周期释放
    void glideRecycleAction();
}
