package com.example.wangzhengze.todayhistory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by WANGZHENGZE on 2015/10/26.
 */
public class LightingBaseActivity extends AppCompatActivity {

    protected Context mContext;

    private TextView mTvTitle;

    private View.OnClickListener mOnBackClickListener = v -> finish();

    private TextView mTvRight;

    private View.OnClickListener mOnRightClickListener;

    private boolean mIsRightEnable = true;

    public boolean getIsRightEnable() {
        return mIsRightEnable;
    }

    public void setOnRightClickListener(View.OnClickListener onRightClickListener) {
        mOnRightClickListener = onRightClickListener;
        getTvRight().setOnClickListener(onRightClickListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mContext = this;
    }

    protected void setNarTitle(int res) {
        setNarTitle(getString(res));
    }

    protected void setNarTitle(CharSequence text) {
        getTvTitle().setText(text);
    }

    private TextView getTvTitle() {
        if (mTvTitle == null) {
            mTvTitle = (TextView) findViewById(R.id.title);
            mTvTitle.setOnClickListener(mOnBackClickListener);
            View ivBack = findViewById(R.id.back);
            ivBack.setOnClickListener(mOnBackClickListener);
        }
        return mTvTitle;
    }

    protected void setNarRightTitle(int res) {
        setNarRightTitle(getString(res));
    }

    private void setNarRightTitle(CharSequence text) {
        getTvRight().setText(text);
    }

    private TextView getTvRight() {
        if (mTvRight == null) {
            mTvRight = (TextView) findViewById(R.id.right_button);
        }

        return mTvRight;
    }

    protected void setRightEnable(boolean isEnable) {
        if (isEnable == getIsRightEnable()) {
            return;
        }

        mIsRightEnable = isEnable;

        if (isEnable) {
            getTvRight().setAlpha(1f);
            getTvRight().setOnClickListener(mOnRightClickListener);
        } else {
            getTvRight().setAlpha(0.3f);
            getTvRight().setOnClickListener(null);
        }
    }
}
