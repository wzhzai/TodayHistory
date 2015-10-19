package com.example.wangzhengze.todayhistory.fragment;


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
import android.widget.EditText;
import android.widget.TextView;

import com.example.wangzhengze.todayhistory.LoadingDialogManager;
import com.example.wangzhengze.todayhistory.R;

import java.io.File;

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

    private String mParam1;
    private String mParam2;
    private TextView mTvResult;
    private LoadingDialogManager mLoadingDialogManager;
    private Subscriber<File> mSubscriber;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowFileWithCondition.
     */
    // TODO: Rename and change types and number of parameters
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
    }

    class MySubscriber extends Subscriber<File> {

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onNext(File file) {
            mTvResult.append("name = " + file + ", \n\n");
        }
    }

    private void initAllViews(View view) {
        EditText etEnter = (EditText) view.findViewById(R.id.show_file_et);
        mTvResult = (TextView) view.findViewById(R.id.show_file_result_tv);
        etEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSubscriber != null) {
                    mSubscriber.unsubscribe();
                }
                mSubscriber = new MySubscriber();
                String string = s.toString();
                Log.e(TAG, "editable = " + s);
                mTvResult.setText("");
                if (!TextUtils.isEmpty(string)) {
                    printAllFileNameStartWithM(s.toString(), mSubscriber);
                }
            }
        });
    }

    private void printAllFileNameStartWithM(String string, Subscriber<File> subscriber) {
        Observable<File> observable = Observable.from(Environment.getExternalStorageDirectory().listFiles());
        observable
                .subscribeOn(Schedulers.io())
                .flatMap(new FlatFileList())
                .filter(file -> file.getName().toLowerCase().startsWith(string))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    class FlatFileList implements Func1<File, Observable<File>> {

        @Override
        public Observable<File> call(File file) {
            if (checkIsDirectory(file)) {
                return Observable.from(file.listFiles()).flatMap(new FlatFileList());
            }
            return Observable.just(file);
        }
    }

    private boolean checkIsDirectory(File file) {
        return file.isDirectory();
    }
}
