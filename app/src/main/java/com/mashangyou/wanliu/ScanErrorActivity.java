package com.mashangyou.wanliu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mashangyou.wanliu.api.Contant;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import butterknife.BindView;

/**
 * Created by Administrator on 2020/9/15.
 * Des:
 */
public class ScanErrorActivity extends BaseActivity{
    @BindView(R.id.title)
    ConstraintLayout title;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.group_none)
    Group groupNone;
    @BindView(R.id.group_fail)
    Group groupFail;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_error;
    }

    @Override
    protected void initToobar() {
        TextView tvTitle = title.findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.code_result_1));
        title.setOnClickListener(view -> finish());
    }

    @Override
    protected void initView() {
        btnBack.setOnClickListener(view -> finish());
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        int errorCode = bundle != null ? bundle.getInt(Contant.SCAN_RESULT) :  10002;
        if (errorCode==10001){
            groupFail.setVisibility(View.VISIBLE);
        }else if(errorCode==10002){
            groupNone.setVisibility(View.VISIBLE);
        }
    }
}
