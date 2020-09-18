package com.frizzle.glide.load;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.frizzle.glide.Tool;
import com.frizzle.glide.resource.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * author: LWJ
 * date: 2020/9/16$
 * description
 */
public class LoadDataManager implements ILoadData, Runnable {
    private ResponseListener responseListener;
    private String path;
    private Context context;

    @Override
    public Value loadResource(String path, ResponseListener responseListener, Context context) {
        this.path = path;
        this.responseListener = responseListener;
        this.context = context;
        Uri uri = Uri.parse(path);
        //通过网络加载
        if ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) {
            new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                    60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()).execute(this);
        }
        //加载
        //...
        //加载本地图片等
        return null;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            int responseCode = httpURLConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode){

                /*BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;*/


                /*BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true; // 只那图片的周围信息，内置会只获取图片的一部分而已，值获取高宽的信息 outW，outH
                BitmapFactory.decodeStream(inputStream, null, options);
                int w = options.outWidth;
                int h = options.outHeight;*/

                int w = 1920;
                int h = 1080;

                // 不需要使用复用池，拿去图片内存
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                //   既然是外部网络加载图片，就不需要用复用池 Bitmap bitmapPoolResult = bitmapPool.get(w, h, Bitmap.Config.RGB_565);
                //   options2.inBitmap = bitmapPoolResult; // 如果我们这里拿到的是null，就不复用
                options2.inMutable = true;
                options2.inPreferredConfig = Bitmap.Config.RGB_565;
                options2.inJustDecodeBounds = false;
                // inSampleSize:是采样率，当inSampleSize为2时，一个2000 1000的图片，将被缩小为1000 500， 采样率为1 代表和原图宽高最接近
                options2.inSampleSize = Tool.sampleBitmapSize(options2, w, h);
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options2); // 真正的加载

                // final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Value value = Value.getInstance();
                        value.setmBitmap(bitmap);
                        if (null!=responseListener){
                            responseListener.responseSuccess(value);
                        }
                    }
                });
            }else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (null!=responseListener){
                            responseListener.responseFail(new Exception("网络请求失败"));
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
