package com.frizzle.glide.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;

/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 * 关联Activity的生命周期 包名要对应
 */
@SuppressLint("ValidFragment")
public class ActivityFragmentManager extends Fragment {

    private LifecycleCallback lifecycleCallback;

    @SuppressLint("ValidFragment")
    public ActivityFragmentManager(LifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideRecycleAction();
        }
    }
}
