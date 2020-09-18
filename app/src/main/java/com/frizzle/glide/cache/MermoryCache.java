package com.frizzle.glide.cache;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.frizzle.glide.resource.Key;
import com.frizzle.glide.resource.Value;

/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 */
public class MermoryCache extends LruCache<String, Value> {

    private MermoryCacheCallback mermoryCacheCallback;
    private boolean manualRemove; //是否是手动移除

    public void setMermoryCacheCallback(MermoryCacheCallback mermoryCacheCallback) {
        this.mermoryCacheCallback = mermoryCacheCallback;
    }

    /**
     * 手动移除
     */
    public Value manualRemove(String key) {
        manualRemove = true;
        Value value = remove(key);
        manualRemove = false;
        return value;
    }

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     *                存入元素的最大值
     */
    public MermoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Value value) {
        Bitmap bitmap = value.getmBitmap();
        //这两种方式获取bitmap大小的效果相同,没有什么区别
//        int size = bitmap.getRowBytes() * bitmap.getHeight();
//        int size = bitmap.getByteCount();
        //这种方式获取bitmap大小,在不考虑内存复用的情况下与上面两种相同
        //当考虑内存复用时,上述两种方式获取的大小是bitmap的真实大小
        //该方式获取的是复用内存的整体大小,可能会大于bitmap的真实大小
//        int size = bitmap.getAllocationByteCount();
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getByteCount();
    }

    /**
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue 1.重复的key会被移除
     *                 2.最近最少使用的元素被移除
     */
    @Override
    protected void entryRemoved(boolean evicted, String key, Value oldValue, Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (mermoryCacheCallback != null && !manualRemove) {//手动移除不执行,被动移除执行
            mermoryCacheCallback.entryRemovedMermoryCache(key, oldValue);
        }
    }
}
