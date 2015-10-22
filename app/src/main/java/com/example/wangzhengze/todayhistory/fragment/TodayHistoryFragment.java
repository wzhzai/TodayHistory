package com.example.wangzhengze.todayhistory.fragment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wangzhengze.todayhistory.LoadingDialogManager;
import com.example.wangzhengze.todayhistory.R;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.QueryMap;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodayHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayHistoryFragment extends BaseFragment {

    private static final String TAG = "TodayHistoryFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ListView mListView;
    private List<HistoryBean.DataBean> mDataBeans = new ArrayList<>();
    private int mDefaultYear = 1949;
    private int mDefaultMonth = 10;
    private int mDefaultDay = 1;
    private TextView mTvDate;
    private MyAdapter mAdapter;


    public static TodayHistoryFragment newInstance(String param1, String param2) {
        TodayHistoryFragment fragment = new TodayHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TodayHistoryFragment() {
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
        return inflater.inflate(R.layout.fragment_today_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAllViews(view);
    }

    private void updateDate() {
        mTvDate.setText(mDefaultMonth + "月" + mDefaultDay + "日");
    }

    private void initAllViews(View view) {
        mLoadingDialogManager = new LoadingDialogManager(mContext);
        mListView = (ListView) view.findViewById(R.id.history_list);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);

        mTvDate = (TextView) view.findViewById(R.id.date_show);
        updateDate();

        View chooseDate = view.findViewById(R.id.choose_date);
        chooseDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(mContext, (view1, year, monthOfYear, dayOfMonth) -> {
                mDefaultDay = dayOfMonth;
                mDefaultMonth = monthOfYear + 1;
                mDefaultYear = year;
                updateDate();
            }, mDefaultYear, mDefaultMonth - 1, mDefaultDay);
            ((ViewGroup) (((ViewGroup) dialog.getDatePicker().getChildAt(0)).getChildAt(0)))
                    .getChildAt(0).setVisibility(View.GONE);
            dialog.show();
        });

        View load = view.findViewById(R.id.load_history);
        load.setOnClickListener(v -> {
            mLoadingDialogManager.show();
            loadNetData();
        });
    }

    private void loadNetData() {
        //初始化Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://apis.baidu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        TodayHistoryService service = retrofit.create(TodayHistoryService.class);
        //设置属性
        Map<String, String> map = new HashMap<>();
        map.put("month", String.valueOf(mDefaultMonth));
        map.put("day", String.valueOf(mDefaultDay));
        map.put("appkey", "1307ee261de8bbcf83830de89caae73f");

        Observable<HistoryBean> result2 = service.getData(map);

        //RxAndroid
        result2.subscribeOn(Schedulers.io())
                .onErrorResumeNext(Observable::error)
                .doOnNext(historyBean -> Log.e(TAG, "doOnNext thread = " + Thread.currentThread().getName()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HistoryBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoadingDialogManager.dismiss();
                        Log.e(TAG, "e = " + e.toString());
                    }

                    @Override
                    public void onNext(HistoryBean historyBean) {
                        Log.e(TAG, "onNext thread = " + Thread.currentThread().getName());
                        mDataBeans.clear();
                        mDataBeans.addAll(historyBean.data);
                        mAdapter.notifyDataSetChanged();
                        mListView.setSelection(0);
                        mLoadingDialogManager.dismiss();
                    }
                });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public String getFragmentTitle() {
        return "Today History";
    }

    public interface TodayHistoryService {
        @Headers("apikey: 142ac2bc6840094a4766e8c80189273f")
        @GET("/netpopo/todayhistory/todayhistory")
        Observable<HistoryBean> getData(@QueryMap Map<String, String> options);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDataBeans.size();
        }

        @Override
        public HistoryBean.DataBean getItem(int position) {
            return mDataBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_today_history, parent, false);
            }
            HistoryBean.DataBean dataBean = getItem(position);
            TextView tvDate = (TextView) convertView.findViewById(R.id.date_show);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
            TextView tvContent = (TextView) convertView.findViewById(R.id.content);
            tvDate.setText(dataBean.year + "年" + dataBean.month + "月" + dataBean.day + "日");
            tvTitle.setText(dataBean.name);
            tvContent.setText(Html.fromHtml(dataBean.content));

            return convertView;
        }
    }

    static class HistoryBean {
        String error;
        String msg;
        List<DataBean> data;

        static class DataBean {
            String id;
            String name;
            String year;
            String month;
            String day;
            String content;

            @Override
            public String toString() {
                return "DataBean{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", year='" + year + '\'' +
                        ", month='" + month + '\'' +
                        ", day='" + day + '\'' +
                        ", content='" + content + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "HistoryBean{" +
                    "error='" + error + '\'' +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}
