package com.example.wangzhengze.todayhistory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wangzhengze.todayhistory.bean.LightingBean;
import com.example.wangzhengze.todayhistory.sqlite.DBManager;
import com.example.wangzhengze.todayhistory.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WANGZHENGZE on 2015/10/22.
 */
public class LightingAllViewManager {

    private static final String TAG = "LightingAllViewManager";

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private LightingAllAdapter mAllAdapter;

    private Map<String, List<LightingBean>> mAllLightingBeanMap = new HashMap<>();

    private LoadingDialogManager mLoadingDialogManager;

    private ExpandableListView mExpandableListView;

    public LightingAllViewManager(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mLoadingDialogManager = new LoadingDialogManager(context);
    }

    public View createAllView(int layoutId) {
        View view = inflaterView(layoutId);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.all_exp_list);
        firstAllLighting();
        return view;
    }

    private void firstAllLighting() {
        mLoadingDialogManager.show();
        rxLoadAllLighting().subscribe(map -> {
            mAllLightingBeanMap.clear();
            mAllLightingBeanMap.putAll(map);
            mAllAdapter = new LightingAllAdapter(Arrays.asList(mContext.getResources().getStringArray(R.array.lighting_main_group)), mAllLightingBeanMap);
            mExpandableListView.setAlpha(0);
            mExpandableListView.setTranslationX(Utils.getScreenMetrics(mContext).widthPixels);
            mExpandableListView.setAdapter(mAllAdapter);
            mExpandableListView.expandGroup(0);
            mLoadingDialogManager.dismiss();
            animationViewIn(mExpandableListView);
        });
    }

    private Observable<Map<String, List<LightingBean>>> rxLoadAllLighting() {
        return Observable
                .just(loadLightingBeanMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void refreshAllLighting() {
        mLoadingDialogManager.show();
        Observable
                .just(loadLightingBeanMap())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(map -> {
                    mAllLightingBeanMap.clear();
                    mAllLightingBeanMap.putAll(map);
                    mAllAdapter.notifyDataSetChanged();
                    mLoadingDialogManager.dismiss();
                });
    }

    private void animationViewIn(View view) {
        view.animate().alpha(1f).translationX(0).setDuration(300).setInterpolator(new AccelerateInterpolator()).start();
    }

    public View createSortView(int layoutId) {
        return inflaterView(layoutId);
    }

    public View createCalenderView(int layoutId) {
        return inflaterView(layoutId);
    }

    private View inflaterView(int layoutId) {
        return mLayoutInflater.inflate(layoutId, null);
    }

    private Map<String, List<LightingBean>> loadLightingBeanMap() {
        Map<String, List<LightingBean>> lightingBeanMap = new HashMap<>();
        String[] group = mContext.getResources().getStringArray(R.array.lighting_main_group);
        for (int i = 0; i < group.length; i++) {
            String s = group[i];
            if (i == 0) {
                lightingBeanMap.put(s, DBManager.getInstance(mContext).getHintList());
            } else {
                lightingBeanMap.put(s, new ArrayList<>());
            }
        }
        return lightingBeanMap;
    }

    private class LightingAllAdapter extends BaseExpandableListAdapter {

        List<String> mGroup;

        Map<String, List<LightingBean>> mContent;

        public LightingAllAdapter(List<String> group, Map<String, List<LightingBean>> content) {
            mGroup = group;
            mContent = content;
        }

        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mContent.get(getGroup(groupPosition)).size();
        }

        @Override
        public String getGroup(int groupPosition) {
            return mGroup.get(groupPosition);
        }

        @Override
        public LightingBean getChild(int groupPosition, int childPosition) {
            return mContent.get(getGroup(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.group_light_all, parent, false);
            }
            TextView tvName = (TextView) convertView.findViewById(R.id.group_name);
            tvName.setText(getGroup(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.child_light_all, parent, false);
            }
            LightingBean child = getChild(groupPosition, childPosition);

            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, CreateLightingActivity.class);
                intent.putExtra("mode", 1);
                intent.putExtra("bean", child);
                mContext.startActivity(intent);
            });

            TextView tvTitle = (TextView) convertView.findViewById(R.id.all_child_content);
            tvTitle.setText(child.title);
            TextView tvTime = (TextView) convertView.findViewById(R.id.all_child_time);
            if (child.reminderTime == 0) {
                tvTime.setVisibility(View.GONE);
            } else {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(String.valueOf(child.reminderTime));
            }

            View document = convertView.findViewById(R.id.all_to_document);
            ImageView ivCheckBox = (ImageView) convertView.findViewById(R.id.all_cb);
            if (child.status == 0) {
                ivCheckBox.setImageResource(R.mipmap.icon_checkbox_nor);
                tvTitle.getPaint().setFlags(0);
                tvTitle.setTextColor(mContext.getResources().getColor(android.R.color.black));
                document.setVisibility(View.INVISIBLE);
            } else {
                ivCheckBox.setImageResource(R.mipmap.icon_checkbox_pres);
                tvTitle.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.lighting_inactive_color));
                document.setVisibility(View.VISIBLE);
            }
            ivCheckBox.setOnClickListener(v -> {
                if (child.status == 0) {
                    ivCheckBox.setImageResource(R.mipmap.icon_checkbox_pres);
                    child.status = 1;
                } else {
                    ivCheckBox.setImageResource(R.mipmap.icon_checkbox_nor);
                    child.status = 0;
                }
                DBManager.getInstance(mContext).updateHintStatus(child.id, child.status);
                notifyDataSetChanged();
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
