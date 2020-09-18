package com.frizzle.glide.pool;

import android.graphics.Bitmap;

/**
 * author: LWJ
 * date: 2020/9/17$
 * description
 */
public interface BitmapPool {
    void put (Bitmap bitmap);

    Bitmap get (int width, int height , Bitmap.Config config);
}
