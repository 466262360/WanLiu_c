package com.mashangyou.wanliu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.wanliu.api.Contant;
import com.mashangyou.wanliu.api.DefaultObserver;
import com.mashangyou.wanliu.api.RetrofitManager;
import com.mashangyou.wanliu.bean.res.ResponseBody;
import com.mashangyou.wanliu.widget.ConfirmDialog;

import java.util.HashMap;

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class MineActivity extends BaseActivity {
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.title)
    ConstraintLayout title;
    @BindView(R.id.cl_pass_word)
    ConstraintLayout clPassWord;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initToobar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        TextView tvTitle = title.findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.mine_2));
        title.setOnClickListener(view -> finish());
    }

    @Override
    protected void initView() {
        clPassWord.setOnClickListener(view -> ActivityUtils.startActivity(PassWordActivity.class));
        btnExit.setOnClickListener(view -> {
            ConfirmDialog confirmDialog = new ConfirmDialog(this);
            confirmDialog.setOnConfirmListener(new ConfirmDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClickListener() {
                    confirmDialog.dismiss();
                    quit();
                }
            });
            confirmDialog.show();
        });
    }

    @Override
    protected void initData() {

    }

    private void quit() {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .quit(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        hideLoading();
                        SPUtils.getInstance().put(Contant.ACCESS_TOKEN, "");
                        SPUtils.getInstance().put(Contant.USER_NAME, "");
                        SPUtils.getInstance().put(Contant.PASS_WORD, "");
                        ActivityUtils.startActivity(LoginActivity.class);
                        ActivityUtils.finishAllActivities();
                    }

                    @Override
                    public void onFail(ResponseBody response) {
                        hideLoading();
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                    }
                });
    }
}
