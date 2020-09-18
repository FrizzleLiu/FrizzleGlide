package com.frizzle.glide.resource;

import android.graphics.Bitmap;
import android.util.Log;

import com.frizzle.glide.Tool;

/**
 * author: LWJ
 * date: 2020/9/15$
 * description
 */
public class Value {
    private static final String TAG = Value.class.getName();
    private static Value value;

    private Value() {
    }

    public static Value getInstance() {
        if (value == null) {
            synchronized (Value.class) {
                if (value == null) {
                    value = new Value();
                }
            }
        }
        return value;
    }


    private Bitmap mBitmap;
    //实用计数
    private int count;
    // 监听
    private ValueCallback callback;

    private String key;

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ValueCallback getCallback() {
        return callback;
    }

    public void setCallback(ValueCallback callback) {
        this.callback = callback;
    }


    //每使用一次就+1
    public void useAction() {
        Tool.checkNotEmpty(mBitmap);
        if (mBitmap.isRecycled()) {//被回收
            return;
        }
        count++;
    }

    public void nonAction() {
        count--;
        if (count <= 0 && callback != null) {
            //回调告诉外界不在使用
            callback.valueNonUseListener(key, this);
        }
    }

    /**
     * TODO 释放
     */
    public void recycleBitmap() {
        if (count > 0) {
            Log.d(TAG, "recycleBitmap: 引用计数大于0，证明还在使用中，不能去释放...");
            return;
        }

        if (mBitmap.isRecycled()) { // 被回收了
            Log.d(TAG, "recycleBitmap: mBitmap.isRecycled() 已经被释放了...");
            return;
        }

        mBitmap.recycle();

        value = null;

        System.gc();
    }
}
