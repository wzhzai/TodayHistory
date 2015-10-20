package com.example.wangzhengze.todayhistory.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangzhengze.todayhistory.LoadingDialogManager;
import com.example.wangzhengze.todayhistory.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowFileWithCondition#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowFileWithCondition extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ShowFileWithCondition";

    private Context mContext;
    private String mParam1;
    private String mParam2;
    private LoadingDialogManager mLoadingDialogManager;

    private ListView mListView;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    public static ShowFileWithCondition newInstance(String param1, String param2) {
        ShowFileWithCondition fragment = new ShowFileWithCondition();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ShowFileWithCondition() {
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
        return inflater.inflate(R.layout.fragment_show_file_with_condition, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAllViews(view);
        loadFolder(Environment.getExternalStorageDirectory().getPath());
    }


    private void initAllViews(View view) {
        mLoadingDialogManager = new LoadingDialogManager(mContext);
        mListView = (ListView) view.findViewById(R.id.file_list);
    }

    private boolean checkIsDirectory(File file) {
        return file.isDirectory();
    }

    class FileBean implements Comparable<FileBean> {
        String name = "";
        boolean isFolder;
        String modifyTime = "";
        String path = "";
        String fatherPath = "";

        @Override
        public int compareTo(FileBean another) {
            return name.charAt(0) > another.name.charAt(0) ? 1 : -1;
        }
    }

    class FileListAdapter extends BaseAdapter {

        private List<FileBean> mFileBeanList;

        public FileListAdapter(List<FileBean> fileBeanList) {
            mFileBeanList = fileBeanList;
        }

        class ViewHolder {
            TextView tvName;
            ImageView ivIcon;
            TextView tvInfo;
            CheckBox cbFile;
        }

        @Override
        public int getCount() {
            return mFileBeanList.size();
        }

        @Override
        public FileBean getItem(int position) {
            return mFileBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            FileBean fileBean = getItem(position);

            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_file_list, parent, false);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.file_name_tv);
                viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.file_info_tv);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.icon);
                viewHolder.cbFile = (CheckBox) convertView.findViewById(R.id.file_cb);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tvName.setText(fileBean.name);
            if (fileBean.isFolder) {
                viewHolder.ivIcon.setImageResource(R.mipmap.folder_icon);
            } else {
                viewHolder.ivIcon.setImageResource(R.mipmap.unknown_file_icon);
            }
            viewHolder.tvInfo.setText(fileBean.modifyTime);

            convertView.setOnClickListener(v -> {
                if (fileBean.isFolder) {
                    FileBean parentFileBean = new FileBean();
                    parentFileBean.name = "..";
                    parentFileBean.path = fileBean.fatherPath;
                    parentFileBean.isFolder = true;
                    loadFolder(fileBean.path, parentFileBean);
                } else {
                    Toast.makeText(mContext, "this is file!", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }

    private void loadFolder(String path) {
        loadFolder(path, null);
    }

    private void loadFolder(String path, FileBean parentFileBean) {
        mLoadingDialogManager.show();
        File file = new File(path);
        List<FileBean> fileFolderBeans = new ArrayList<>();
        List<FileBean> fileBeans = new ArrayList<>();
        Observable.from(file.listFiles())
                .subscribeOn(Schedulers.io())
                .doOnNext(eachFile -> {
                    FileBean fileBean = new FileBean();
                    fileBean.name = eachFile.getName();
                    fileBean.isFolder = checkIsDirectory(eachFile);
                    fileBean.modifyTime = parseTimeToString(eachFile.lastModified());
                    fileBean.path = eachFile.getAbsolutePath();
                    fileBean.fatherPath = eachFile.getParent();
                    if (fileBean.isFolder) {
                        fileFolderBeans.add(fileBean);
                    } else {
                        fileBeans.add(fileBean);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file1 -> {
                }, throwable -> {
                }, () -> {
                    Collections.sort(fileFolderBeans);
                    Collections.sort(fileBeans);
                    List<FileBean> allBeans = new ArrayList<>();
                    if (parentFileBean != null) {
                        allBeans.add(parentFileBean);
                    }
                    allBeans.addAll(fileFolderBeans);
                    allBeans.addAll(fileBeans);
                    mListView.setAdapter(new FileListAdapter(allBeans));
                    mLoadingDialogManager.dismiss();
                });
    }

    private String parseTimeToString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss", Locale.getDefault());
        return format.format(time);
    }
}
