package com.example.wangzhengze.todayhistory.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wangzhengze.todayhistory.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FileManagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileManagerFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FileManagerFragment";

    private String mParam1;
    private String mParam2;

    private ListView mListView;
    private String mParentPath;

    private int mLastPosition;

    private List<Integer> mLastPositionList = new ArrayList<>();

    public static FileManagerFragment newInstance(String param1, String param2) {
        FileManagerFragment fragment = new FileManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FileManagerFragment() {
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
        mListView = (ListView) view.findViewById(R.id.file_list);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                mLastPosition = firstVisibleItem;
            }
        });
    }

    private boolean checkIsDirectory(File file) {
        return file.isDirectory();
    }

    @Override
    public boolean onBackPressed() {
        if (TextUtils.isEmpty(mParentPath) || checkIsRoot(mParentPath)) {
            return false;
        }
        String backParentPath = mParentPath;
        mParentPath = mParentPath.substring(0, mParentPath.lastIndexOf(File.separator));
        FileBean parentFileBean = createParentFileBean(mParentPath);
        loadFolder(backParentPath, parentFileBean, true);
        return true;
    }

    @Override
    public String getFragmentTitle() {
        return "File Manager";
    }

    class FileBean implements Comparable<FileBean> {
        String name = "";
        boolean isFolder = false;
        String modifyTime = "";
        String path = "";
        String fatherPath = "";
        boolean isBack = false;

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

            if (fileBean.isBack) {
                viewHolder.cbFile.setVisibility(View.GONE);
            } else {
                viewHolder.cbFile.setVisibility(View.VISIBLE);
            }

            convertView.setOnClickListener(v -> {
                if (fileBean.isFolder) {
                    mParentPath = fileBean.path.substring(0, fileBean.path.lastIndexOf(File.separator));
                    FileBean parentFileBean = null;
                    if (!checkIsRoot(mParentPath)) {
                        parentFileBean = createParentFileBean(mParentPath);
                    }
                    boolean isBack = fileBean.isBack;
                    if (!isBack) {
                        mLastPositionList.add(0, mLastPosition);
                    }
                    loadFolder(fileBean.path, parentFileBean, isBack);
                } else {
                    Toast.makeText(mContext, "this is file!", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }
    }

    @NonNull
    private FileBean createParentFileBean(String parentPath) {
        FileBean parentFileBean;
        parentFileBean = new FileBean();
        parentFileBean.name = "..";
        parentFileBean.path = parentPath;
        parentFileBean.isFolder = true;
        parentFileBean.isBack = true;
        return parentFileBean;
    }

    private boolean checkIsRoot(String path) {
        return path.equals(Environment.getExternalStorageDirectory().getParent());
    }

    private void loadFolder(String path) {
        loadFolder(path, null, false);
    }

    private void loadFolder(String path, FileBean parentFileBean, boolean isBack) {
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
                    if (isBack) {
                        mListView.setSelection(mLastPositionList.get(0));
                        mLastPositionList.remove(0);
                    }
                    mLoadingDialogManager.dismiss();
                });
    }

    private String parseTimeToString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss", Locale.getDefault());
        return format.format(time);
    }
}
