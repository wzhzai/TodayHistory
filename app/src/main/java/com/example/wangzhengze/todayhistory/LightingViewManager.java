package com.example.wangzhengze.todayhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by WANGZHENGZE on 2015/10/28.
 */
public abstract class LightingViewManager {
    protected Context mContext;

    protected LayoutInflater mLayoutInflater;

    protected LoadingDialogManager mLoadingDialogManager;

    public LightingViewManager(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mLoadingDialogManager = new LoadingDialogManager(context);
    }

    public abstract View createView(int layoutId);

    public abstract void refreshView();

    protected void animationViewIn(View view) {
        view.animate().alpha(1f).translationX(0).setDuration(300).setInterpolator(new AccelerateInterpolator()).start();
    }

    protected View inflaterView(int layoutId) {
        return mLayoutInflater.inflate(layoutId, null);
    }
}
