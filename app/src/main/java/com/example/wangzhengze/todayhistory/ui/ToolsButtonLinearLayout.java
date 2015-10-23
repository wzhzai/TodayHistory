package com.example.wangzhengze.todayhistory.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wangzhengze.todayhistory.R;

/**
 * Created by WANGZHENGZE on 2015/10/22.
 */
public class ToolsButtonLinearLayout extends LinearLayout {

    private MenuBuilder mMenu;

    private MenuInflater mMenuInflater;

    private OnToolItemClickListener mOnToolItemClickListener;

    public OnToolItemClickListener getOnToolItemClickListener() {
        return mOnToolItemClickListener;
    }

    public void setOnToolItemClickListener(OnToolItemClickListener onToolItemClickListener) {
        mOnToolItemClickListener = onToolItemClickListener;
    }

    public MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new MenuInflater(getContext());
        }
        return mMenuInflater;
    }

    public ToolsButtonLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public ToolsButtonLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithAttrs(context, attrs);
    }

    public ToolsButtonLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWithAttrs(context, attrs);
    }


    private void init(Context context) {
        if (mMenu != null) {
            for (int i = 0; i < mMenu.getVisibleItems().size(); i++) {
                MenuItemImpl menuItem = mMenu.getVisibleItems().get(i);
                ToolsItemTextView tvItem = new ToolsItemTextView(context);
                tvItem.initialize(menuItem);
                addView(tvItem);
                final int position = i;
                tvItem.setOnClickListener(v -> {
                    if (menuItem.isChecked()) {
                        return;
                    }
                    refreshItemState(position);
                    if (mOnToolItemClickListener != null) {
                        mOnToolItemClickListener.onItemClick(menuItem.getItemId());
                    }
                });
            }
        }
    }

    private void refreshItemState(int position) {
        for (int i = 0; i < mMenu.getVisibleItems().size(); i++) {
            MenuItemImpl menuItem = mMenu.getVisibleItems().get(i);
            if (i == position) {
                menuItem.setChecked(true);
            } else {
                menuItem.setChecked(false);
            }
            ToolsItemTextView tvItem = (ToolsItemTextView) getChildAt(i);
            tvItem.initialize(menuItem);
        }
    }


    private void initWithAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToolsButtonLinearLayout);
        int res = typedArray.getResourceId(R.styleable.ToolsButtonLinearLayout_tools_menu, -1);
        typedArray.recycle();

        if (res > 0) {
            mMenu = new MenuBuilder(context);
            getMenuInflater().inflate(res, mMenu);
        }

        init(context);
    }

    public interface OnToolItemClickListener {
        void onItemClick(int id);
    }

}
