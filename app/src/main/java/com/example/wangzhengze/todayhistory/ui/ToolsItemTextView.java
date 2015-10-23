package com.example.wangzhengze.todayhistory.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wangzhengze.todayhistory.R;

/**
 * Created by WANGZHENGZE on 2015/10/22.
 */
public class ToolsItemTextView extends TextView {
    public ToolsItemTextView(Context context) {
        super(context);
    }

    public ToolsItemTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolsItemTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initialize(MenuItemImpl menuItem) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        setPadding(getTextViewPadding(16), getTextViewPadding(7), getTextViewPadding(16), getTextViewPadding(7));
        setGravity(Gravity.CENTER);
        setText(menuItem.getTitle());
        Drawable icon = menuItem.getIcon();
        setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        setTextColor(getResources().getColor(R.color.lighting_active_color));
        if (!menuItem.isChecked()) {
            setAlpha(0.5f);
        } else {
            setAlpha(1f);
        }
    }

    private int getTextViewPadding(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
