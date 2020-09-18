package com.frizzle.glide.load;

import android.content.Context;

import com.frizzle.glide.resource.Value;

/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 * 加载外部资源
 */
public interface ILoadData {
    Value loadResource(String path, ResponseListener responseListener, Context context);
}
