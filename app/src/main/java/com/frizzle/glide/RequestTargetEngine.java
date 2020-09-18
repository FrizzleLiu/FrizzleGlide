package com.frizzle.glide;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.frizzle.glide.cache.ActiveCache;
import com.frizzle.glide.cache.MermoryCache;
import com.frizzle.glide.cache.MermoryCacheCallback;
import com.frizzle.glide.cache.disk.DiskLruCacheImpl;
import com.frizzle.glide.fragment.LifecycleCallback;
import com.frizzle.glide.load.LoadDataManager;
import com.frizzle.glide.load.ResponseListener;
import com.frizzle.glide.resource.Key;
import com.frizzle.glide.resource.Value;
import com.frizzle.glide.resource.ValueCallback;

/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 * 加载图片资源
 */
public class RequestTargetEngine implements LifecycleCallback, ValueCallback, MermoryCacheCallback, ResponseListener {

    private ActiveCache activeCache;
    private MermoryCache mermoryCache;
    private DiskLruCacheImpl diskLruCache;
    private final int MAX_SIZE = 1024 * 1024 * 60;
    private String path;
    private Context glideContext;
    private String key;
    private ImageView imageView;

    public RequestTargetEngine() {
        //初始化活动缓存
        if (null == activeCache) {
            activeCache = new ActiveCache(this);
        }

        //初始化内存缓存
        if (null == mermoryCache) {
            mermoryCache = new MermoryCache(MAX_SIZE);
            mermoryCache.setMermoryCacheCallback(this);
        }

        //初始化磁盘缓存
        if (null == diskLruCache) {
            diskLruCache = new DiskLruCacheImpl();
        }

    }

    @Override
    public void glideInitAction() {
        Log.e("Frizzle", "Glide  声明周期初始化...");
    }

    @Override
    public void glideStopAction() {
        Log.e("Frizzle", "Glide  声明周期停止中...");
    }

    @Override
    public void glideRecycleAction() {
        Log.e("Frizzle", "Glide  声明周期释放...");
        if (activeCache != null) {
            activeCache.closeThread();
        }
    }


    /**
     * RequestManager 传进来的值
     */
    public void loadValueInitAction(String path, Context glideContext) {
        this.path = path;
        this.glideContext = glideContext;
        key = new Key(path).getKey();
    }

    public void into(ImageView img) {
        this.imageView = img;
        Tool.checkNotEmpty(imageView);
        Tool.assertMainThread();//主线程
        Value value = cacheAction();
        if (value != null) {
            //使用完成,使用技术-1
            value.nonAction();
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    //活动缓存 -> 内存缓存 -> 磁盘缓存 ->网络
    private Value cacheAction() {
        Value value = activeCache.get(key);
        if (null != value) {
            Log.e("Frizzle", "活动缓存中取到资源");
            value.useAction();
            return value;
        }

        value = mermoryCache.get(key);
        if (null != value) {
            //将内存中的缓存移动到活动缓存中
            Log.e("Frizzle", "内存缓存中取到资源");
            mermoryCache.manualRemove(key);
            activeCache.put(key, value);
            value.useAction();
            return value;
        }

        value = diskLruCache.get(key);
        if (null != value) {
            Log.e("Frizzle", "磁盘缓存中取到资源");
            //将磁盘缓存中的元素加入到活动缓存中
            activeCache.put(key, value);
            //将磁盘缓存中的元素加入到内存缓存中
//            mermoryCache.put(key,value);
            value.useAction();
            return value;
        }

        //网络加载
        value = new LoadDataManager().loadResource(path, this, glideContext);
        if (null != value) {
            return value;
        }

        return null;
    }

    /**
     * @param key
     * @param value 活动缓存 监听回调 资源不再使用
     */
    @Override
    public void valueNonUseListener(String key, Value value) {
        //活动缓存 移动到 内存缓存中去
        if (key != null && value != null) {
            if (mermoryCache!=null){
                mermoryCache.put(key, value);
            }
        }
    }

    /**
     * @param key
     * @param oldValue 内存缓存回调
     */
    @Override
    public void entryRemovedMermoryCache(String key, Value oldValue) {

    }

    /**
     * @param value 网络请求成功回调
     */
    @Override
    public void responseSuccess(Value value) {
        if (value != null) {
            saveCache(key, value);
            imageView.setImageBitmap(value.getmBitmap());
        }
    }

    /**
     * @param exception 网络请求失败回调
     */
    @Override
    public void responseFail(Exception exception) {
        Log.e("Frizzle", "网络加载图片发生异常....");
    }

    public void saveCache(String key, Value value) {
        Log.e("Frizzle", "将网络图片加载到缓存....");
        value.setKey(key);
        if (diskLruCache != null) {
            diskLruCache.put(key, value);
        }
    }
}
