package com.frizzle.glide.fragment;

import androidx.fragment.app.Fragment;

/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 * 和FragmentActivity的生命周期关联
 */
public class FragmentActivityFragmentManager extends Fragment {
    private LifecycleCallback lifecycleCallback;

    public FragmentActivityFragmentManager(LifecycleCallback lifecycleCallback) {
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
