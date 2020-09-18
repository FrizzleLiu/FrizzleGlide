package com.frizzle.glide.cache;

import com.frizzle.glide.Tool;
import com.frizzle.glide.resource.Value;
import com.frizzle.glide.resource.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * author: LWJ
 * date: 2020/9/15$
 * description三级缓存-活动缓存
 * 采用弱引用
 */
public class ActiveCache {

    // 容器
    private Map<String, WeakReference<Value>> mapList = new HashMap<>();
    private ReferenceQueue<Value> queue; // 目的：为了监听这个弱引用 是否被回收了
    private boolean isCloseThread;
    private Thread thread;
    private boolean isUserRemove;
    private ValueCallback valueCallback;

    public ActiveCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    /**
     * @param key
     * @param value 添加活动缓存
     */
    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);
        //绑定value的监听
        value.setCallback(valueCallback);
        mapList.put(key, new CustomWeakReference(value, getQueue(), key));
    }

    /**
     * @param key
     * @return 获取
     */
    public Value get(String key) {
        WeakReference<Value> valueWeakReference = mapList.get(key);
        if (valueWeakReference != null) {
            return valueWeakReference.get();
        }
        return null;
    }

    /**
     * @param key
     * @return 手动移除
     */
    public Value remove(String key) {
        isUserRemove = true;
        WeakReference<Value> valueWeakReference = mapList.remove(key);
        //移除完毕
        isUserRemove = false;
        if (valueWeakReference != null) {
            return valueWeakReference.get();
        }
        return null;
    }


    public class CustomWeakReference extends WeakReference<Value> {
        private String key;

        public CustomWeakReference(Value referent, ReferenceQueue<? super Value> q, String key) {
            super(referent, q);
            this.key = key;
        }
    }

    public void closeThread() {
        isCloseThread = true;
//        if (null != thread) {
//            thread.interrupt();//中断线程
//            try {
//                thread.join(TimeUnit.SECONDS.toMillis(5));//平稳关闭
//                if (thread.isAlive()) {
//                    throw new IllegalStateException("活动缓存中,线程关闭失败");
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        mapList.clear();
        System.gc();
    }

    /**
     * @return 为了监听弱引用被回收, 并且期望只处理GC的回收移除
     */
    private ReferenceQueue<Value> getQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isCloseThread) {
                        try {
                            if (!isUserRemove) {
                                //queue.remove()是阻塞式方法,只有弱引用被回收才会继续往下走,所以这里的循环是安全的
                                Reference<? extends Value> remove = queue.remove();//如果被回收了会执行该方法
                                CustomWeakReference weakReference = (CustomWeakReference) remove;
                                //移除容器中的元素,只处理GC回收 不处理用户手动回收
                                if (mapList != null && !mapList.isEmpty()) {
                                    mapList.remove(weakReference.key);
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            thread.start();
        }
        return queue;
    }
}
