package com.example.wangzhengze.todayhistory.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.wangzhengze.todayhistory.CreateLightingActivity;
import com.example.wangzhengze.todayhistory.LightingAllViewManager;
import com.example.wangzhengze.todayhistory.LightingCalendarView;
import com.example.wangzhengze.todayhistory.LightingSortViewManager;
import com.example.wangzhengze.todayhistory.LightingViewManager;
import com.example.wangzhengze.todayhistory.R;
import com.example.wangzhengze.todayhistory.ui.ToolsButtonLinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LightingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LightingFragment extends BaseFragment {

    private static final String TAG = "LightingFragment";

    private static final String ARG_PARAM1 = "param1";

    private static final String ARG_PARAM2 = "param2";

    private static final int PAGE_ALL = 0;

    private static final int PAGE_SORT = 1;

    private static final int PAGE_CALENDAR = 2;

    private String mParam1;

    private String mParam2;

    private LinearLayout mLlMain;

    private LightingViewManager mLightingViewManager;

    private FloatingActionButton mFloatingActionButton;

    private int mCurrentPage = -1;

    public static LightingFragment newInstance(String param1, String param2) {
        LightingFragment fragment = new LightingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LightingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lighting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLlMain = (LinearLayout) view.findViewById(R.id.light_main_view);

        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);

        mFloatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CreateLightingActivity.class);
            intent.putExtra("mode", 0);
            startActivity(intent);
        });

        ToolsButtonLinearLayout toolsButtonLinearLayout = (ToolsButtonLinearLayout) view.findViewById(R.id.user_menu);
        toolsButtonLinearLayout.setOnToolItemClickListener(id -> {
            View layout = null;
            switch (id) {
                case R.id.nav_all:
                    mLightingViewManager = new LightingAllViewManager(mContext);
                    layout = mLightingViewManager.createView(R.layout.view_lighting_all);
                    mCurrentPage = PAGE_ALL;
                    break;
                case R.id.nav_sort:
                    mLightingViewManager = new LightingSortViewManager(mContext);
                    layout = (mLightingViewManager.createView(R.layout.view_lighting_sort));
                    mCurrentPage = PAGE_SORT;
                    break;
                case R.id.nav_calendar:
                    mLightingViewManager = new LightingCalendarView(mContext);
                    layout = mLightingViewManager.createView(R.layout.view_lighting_calendar);
                    mCurrentPage = PAGE_CALENDAR;
                    break;
                default:
                    break;
            }
            replaceMainView(layout);
        });
    }



    private void replaceMainView(View view) {
        if (view == null) {
            return;
        }
        mLlMain.removeAllViews();
        mLlMain.addView(view);
    }

    private void loadDefaultView() {
        mLightingViewManager = new LightingAllViewManager(mContext);
        replaceMainView(mLightingViewManager.createView(R.layout.view_lighting_all));
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public String getFragmentTitle() {
        return "Lighting";
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (mCurrentPage) {
            case -1:
                loadDefaultView();
                break;
            default:
                mLightingViewManager.refreshView();
                break;
        }
    }
}
