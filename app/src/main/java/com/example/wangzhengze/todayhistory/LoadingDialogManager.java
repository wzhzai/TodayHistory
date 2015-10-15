package com.example.wangzhengze.todayhistory;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by WANGZHENGZE on 2015/10/15.
 */
public class LoadingDialogManager {
    private Context mContext;
    private ProgressDialog mDialog;

    public LoadingDialogManager(Context context) {
        mContext = context;
        mDialog = new ProgressDialog(mContext);
    }

    public void show() {
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
}
