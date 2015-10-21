package com.example.wangzhengze.todayhistory.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import com.example.wangzhengze.todayhistory.LoadingDialogManager;
import com.example.wangzhengze.todayhistory.interfaces.IFragmentAttachCallback;

/**
 * Created by WANGZHENGZE on 2015/10/21.
 */
public abstract class BaseFragment extends Fragment {
    protected LoadingDialogManager mLoadingDialogManager;

    protected Context mContext;

    protected IFragmentAttachCallback mIFragmentAttachCallback;

    public abstract boolean onBackPressed();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mLoadingDialogManager = new LoadingDialogManager(mContext);

        if (activity instanceof IFragmentAttachCallback) {
            mIFragmentAttachCallback = (IFragmentAttachCallback) activity;
        } else {
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mIFragmentAttachCallback.onFragmentAttach(this);
    }
}
