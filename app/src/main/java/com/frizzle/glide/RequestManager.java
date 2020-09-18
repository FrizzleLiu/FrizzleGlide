package com.frizzle.glide;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.frizzle.glide.fragment.ActivityFragmentManager;
import com.frizzle.glide.fragment.FragmentActivityFragmentManager;


/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 */
public class RequestManager {
    private final String FRAGMENT_ACTIVITY_NAME = "fragment_activity_name";
    private final String ACTIVITY_NAME = "activity_name";
    private final int NEXT_HAND_MSG = 99545664;
    private static RequestTargetEngine requestTargetEngine;
    private Context requestManagerContext;

    //构造代码块
    {
        if (null == requestTargetEngine){
            requestTargetEngine = new RequestTargetEngine();
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            return false;
        }
    });

    /**
     * @param fragmentActivity
     * 可以管理生命周期
     */
    public RequestManager(FragmentActivity fragmentActivity) {
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (null == fragment){
            fragment = new FragmentActivityFragmentManager(requestTargetEngine);//Fragment生命周期与RequestTargetEngine关联
            supportFragmentManager.beginTransaction().add(fragment,FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }
        //在这里根据Tag直接通过fragmentManager.findFragmentByTag取fragment返回值是null,因为上述操作会通过Handler发送消息执行,这里还没消费掉
        //发送一次Handler,保证能正常取到上面的fragment
        mHandler.sendEmptyMessage(NEXT_HAND_MSG);
    }

    /**
     * @param activity
     * 可以管理生命周期
     */
    public RequestManager(Activity activity) {
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
        if (null == fragment){
            fragment = new ActivityFragmentManager(requestTargetEngine);//Fragment生命周期与RequestTargetEngine关联
            fragmentManager.beginTransaction().add(fragment,ACTIVITY_NAME).commitAllowingStateLoss();
        }
        //在这里根据Tag直接通过fragmentManager.findFragmentByTag取fragment返回值是null,因为上述操作会通过Handler发送消息执行,这里还没消费掉
        //发送一次Handler,保证能正常取到上面的fragment
        mHandler.sendEmptyMessage(NEXT_HAND_MSG);
    }

    /**
     * @param context 无法管理生命周期
     */
    public RequestManager(Context context) {
        this.requestManagerContext = context;
    }

    /**
     * @param path 图片路径
     * @return
     */
    public RequestTargetEngine load(String path) {
        mHandler.removeMessages(NEXT_HAND_MSG);
        requestTargetEngine.loadValueInitAction(path, requestManagerContext);
        return requestTargetEngine;
    }
}
