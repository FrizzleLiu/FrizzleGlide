package com.frizzle.glide;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

public class Glide {

    RequestManagerRetriever retriver;

    public Glide(RequestManagerRetriever retriver) {
        this.retriver = retriver;
    }

    public static RequestManager with(FragmentActivity fragmentActivity) {
        return getRetriever(fragmentActivity).get(fragmentActivity);
    }

    public static RequestManager with(Activity activity) {
        return getRetriever(activity).get(activity);
    }

    public static RequestManager with(Context mContext) {
        return getRetriever(mContext).get(mContext);
    }

    /**
     * RequestManager有我们的RequestManagerRetriver去创建的
     * @return
     */
    public static RequestManagerRetriever getRetriever(Context context) {
        return Glide.get(context).getRetriver();
    }

    // Glide 是 new出来的 -- 转变
    public static Glide get(Context context) {
        return new GlideBuilder().build();
    }

    /**
     * RequestManagerRetriver
     * @return
     */
    public RequestManagerRetriever getRetriver() {
        return retriver;
    }
}
