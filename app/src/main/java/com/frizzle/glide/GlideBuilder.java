package com.frizzle.glide;

public class GlideBuilder {

    /**
     * 创建Glide
     * @return
     */
    public Glide build() {
        RequestManagerRetriever requestManagerRetriver = new RequestManagerRetriever();
        Glide glide = new Glide(requestManagerRetriver);
        return glide;
    }
}
