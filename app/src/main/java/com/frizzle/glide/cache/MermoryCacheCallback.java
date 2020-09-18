package com.frizzle.glide.cache;

import com.frizzle.glide.resource.Key;
import com.frizzle.glide.resource.Value;

/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 */
public interface MermoryCacheCallback {
    /**
     * @param key
     * @param oldValue
     * 内存缓存中有元素被移除
     */
    void entryRemovedMermoryCache(String key, Value oldValue);
}
