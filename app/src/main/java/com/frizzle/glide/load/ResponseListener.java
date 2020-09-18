package com.frizzle.glide.load;

import com.frizzle.glide.resource.Value;

/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 * 加载外部资源的回调
 */
public interface ResponseListener {
    void responseSuccess(Value value);

    void responseFail(Exception exception);
}
