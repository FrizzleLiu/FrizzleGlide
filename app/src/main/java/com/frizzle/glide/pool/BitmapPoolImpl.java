package com.frizzle.glide.pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.util.TreeMap;

/**
 * author: LWJ
 * date: 2020/9/17$
 * description
 */
public class BitmapPoolImpl extends LruCache<Integer,Bitmap> implements BitmapPool{
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapPoolImpl(int maxSize) {
        super(maxSize);
    }

    // 为了筛选出 合适的 Bitmap 容器
    private TreeMap<Integer, String> treeMap = new TreeMap<>();

    @Override
    public void put(Bitmap bitmap) {
        if (!bitmap.isMutable()){
            Log.e("Frizzle","条件1 可变不满足,不存入...");
            return;
        }

        int bitmapSize = getBitmapSize(bitmap);
        if (maxSize() <= bitmapSize){
            Log.e("Frizzle","条件2 大小不满足,不存入...");
            return;
        }

        //存入LruCache
        put(bitmapSize,bitmap);

        // 存入 筛选 容器
        treeMap.put(bitmapSize, null); // 10000
    }

    /**
     * @param bitmap 计算bitmap大小
     * @return
     */
    public int getBitmapSize(Bitmap bitmap){
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
     * @param width
     * @param height
     * @param config
     * @return
     * 获取可以复用的Bitmap
     */
    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        /**
         * ALPHA_8  理论上 实际上Android自动做处理的 只有透明度 8位  1个字节
         * w*h*1
         *
         * RGB_565  理论上 实际上Android自动做处理的  R red红色 5， G绿色 6， B蓝色 5   16位 2个字节 没有透明度
         * w*h*2
         *
         * ARGB_4444 理论上 实际上Android自动做处理 A透明度 4位  R red红色4位   16位 2个字节
         *
         * 质量最高的：
         * ARGB_8888 理论上 实际上Android自动做处理  A 8位 1个字节  ，R 8位 1个字节， G 8位 1个字节， B 8位 1个字节
         *
         * 常用的 ARGB_8888  RGB_565
         */
        // 这里只考虑常用的 4==ARGB_8888  2==RGB_565
        int getSize = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);

        Integer key = treeMap.ceilingKey(getSize); // 可以查找到容器里面 和getSize一样大的，也可以 比getSize还要大的
        // 如果treeMap 还没有put，那么一定是 null
        if (key == null) {
            return null; // 没有找到合适的 可以复用的 key
        }

        // key == 10000     getSize==12000

        // 查找容器取出来的key，必须小于 计算出来的 (getSize * 2 ： )
        // if (key <= (getSize * 2)) {
        Bitmap remove = remove(key);// 复用池 如果要取出来，肯定要移除，不想给其他地方用了
        Log.d("Frizzle", "get: 从复用池 里面获取了Bitmap...");
        return remove;
    }

    // 元素大小
    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return getBitmapSize(value);
    }

    // 元素被移除
    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }
}
