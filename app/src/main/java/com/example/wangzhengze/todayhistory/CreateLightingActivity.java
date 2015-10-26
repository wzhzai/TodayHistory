package com.example.wangzhengze.todayhistory;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.wangzhengze.todayhistory.bean.LightingBean;
import com.example.wangzhengze.todayhistory.sqlite.DBManager;

public class CreateLightingActivity extends LightingBaseActivity {

    private static final String TAG = "CreateLightingActivity";

    private static final int MODE_NEW = 0;

    private int mMode;

    private LightingBean mLightingBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMode = getIntent().getIntExtra("mode", 0);


        setContentView(R.layout.activity_create_lighting);
        setNarRightTitle(R.string.save);

        if (mMode == MODE_NEW) {
            setNarTitle(R.string.create);
            setRightEnable(false);
        } else {
            setNarTitle(R.string.detail);
            setRightEnable(true);
            mLightingBean = (LightingBean) getIntent().getSerializableExtra("bean");
        }

        initAllViews();
    }

    private void initAllViews() {
        EditText editText = (EditText) findViewById(R.id.lighting_et);

        if (mMode != MODE_NEW) {
            editText.setText(mLightingBean.content);
            editText.setSelection(mLightingBean.content.length());
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mMode == MODE_NEW) {
                    if (TextUtils.isEmpty(s)) {
                        setRightEnable(false);
                    } else {
                        setRightEnable(true);
                    }
                }
            }
        });

        setOnRightClickListener(v -> {
            String title;
            if (editText.getText().toString().contains("\n")) {
                title = editText.getText().toString().substring(0, editText.getText().toString().indexOf('\n'));
            } else {
                title = editText.getText().toString();
            }
            LightingBean bean = new LightingBean();
            bean.title = title;
            bean.content = editText.getText().toString();
            if (mMode == MODE_NEW) {
                DBManager.getInstance(mContext).saveTask(bean);
            } else {
                bean.id = mLightingBean.id;
                DBManager.getInstance(mContext).updateTask(bean);
            }
            finish();
        });
    }

}
