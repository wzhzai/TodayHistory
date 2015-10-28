package com.example.wangzhengze.todayhistory;

import android.content.Context;
import android.view.View;

/**
 * Created by WANGZHENGZE on 2015/10/28.
 */
public class LightingCalendarView extends LightingViewManager {
    public LightingCalendarView(Context context) {
        super(context);
    }

    @Override
    public View createView(int layoutId) {
        return inflaterView(layoutId);
    }

    @Override
    public void refreshView() {

    }
}
