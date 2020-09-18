package com.frizzle.glide.resource;

import com.frizzle.glide.Tool;

/**
 * author: LWJ
 * date: 2020/9/15$
 * description
 */
public class Key {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    //加密字符串
    public Key(String key) {
        this.key = Tool.getSHA256StrJava(key);
    }
}
