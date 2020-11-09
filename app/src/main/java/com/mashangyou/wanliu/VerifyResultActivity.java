package com.mashangyou.wanliu;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.mashangyou.scan.ScannerActivity;
import com.mashangyou.wanliu.api.Contant;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.content.ContextCompat;
import butterknife.BindView;

/**
 * Created by Administrator on 2020/9/15.
 * Des:
 */
public class VerifyResultActivity extends BaseActivity{
    @BindView(R.id.title)
    ConstraintLayout title;
    @BindView(R.id.group_fail_message)
    Group groupFailMessage;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_again)
    Button btnAgain;
    @BindView(R.id.iv_bg)
    ImageView ivBg;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_verify_result;
    }

    @Override
    protected void initToobar() {
        TextView tvTitle = title.findViewById(R.id.tv_title);
        ImageView ivBack = title.findViewById(R.id.iv_back);
        ivBack.setImageResource(R.drawable.back_black);
        tvTitle.setTextColor(ContextCompat.getColor(this,R.color.black3));
        tvTitle.setText(R.string.verify_result_1);
        title.setOnClickListener(view -> finish());
    }

    @Override
    protected void initView() {
        btnBack.setOnClickListener(view -> finish());
        btnAgain.setOnClickListener(view -> {
            ActivityUtils.startActivity(ScannerActivity.class);
            finish();
        });
    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            boolean isSuccess = bundle.getBoolean(Contant.VERIFY_RESULT);
            if (!isSuccess){
                groupFailMessage.setVisibility(View.VISIBLE);
                ivBg.setImageResource(R.drawable.verify_fail);
                tvMessage.setText(getString(R.string.verify_result_3));
                setTextFail(tvMessage);
            }else{
                setTextSuccess(tvMessage);
            }
        }
    }

    private void setTextSuccess(TextView textView) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, textView.getPaint().getTextSize()* textView.getText().length(), 0, Color.parseColor("#66D2A2"), Color.parseColor("#4CAAB8"), Shader.TileMode.CLAMP);
        textView.getPaint().setShader(mLinearGradient);
        textView.invalidate();
    }
    private void setTextFail(TextView textView) {
        LinearGradient mLinearGradient = new LinearGradient(0, 0, textView.getPaint().getTextSize()* textView.getText().length(), 0, Color.parseColor("#F78D45"), Color.parseColor("#E72C68"), Shader.TileMode.CLAMP);
        textView.getPaint().setShader(mLinearGradient);
        textView.invalidate();
    }
}
