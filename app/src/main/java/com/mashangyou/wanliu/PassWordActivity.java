package com.mashangyou.wanliu;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.wanliu.api.Contant;
import com.mashangyou.wanliu.api.DefaultObserver;
import com.mashangyou.wanliu.api.RetrofitManager;
import com.mashangyou.wanliu.bean.res.PassWordRes;
import com.mashangyou.wanliu.bean.res.ResponseBody;

import java.util.HashMap;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class PassWordActivity extends BaseActivity{
    @BindView(R.id.et_old)
    EditText et_old;
    @BindView(R.id.et_new)
    EditText et_new;
    @BindView(R.id.et_again)
    EditText et_again;
    @BindView(R.id.btn_commit)
    Button btn_commit;
    @BindView(R.id.title)
    ConstraintLayout title;
    private String oldPass;
    private String newPass;
    private String againPass;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pass_word;
    }

    @Override
    protected void initToobar() {
        TextView tvTitle = title.findViewById(R.id.tv_title);
        ImageView iv_back = title.findViewById(R.id.iv_back);
        iv_back.setImageResource(R.drawable.back_black);
        tvTitle.setTextColor(ContextCompat.getColor(this,R.color.black3));
        tvTitle.setText(getString(R.string.pass_word_2));
        title.setOnClickListener(view -> finish());
    }


    @Override
    protected void initView() {
            btn_commit.setOnClickListener(view -> {
                oldPass = et_old.getText().toString().trim();
                newPass = et_new.getText().toString().trim();
                againPass = et_again.getText().toString().trim();
                if (TextUtils.isEmpty(oldPass)){
                    ToastUtils.showShort(getString(R.string.pass_word_3));
                    return;
                }
                if (TextUtils.isEmpty(newPass)){
                    ToastUtils.showShort(getString(R.string.pass_word_4));
                    return;
                }
                if (TextUtils.isEmpty(againPass)){
                    ToastUtils.showShort(getString(R.string.pass_word_5));
                    return;
                }
                if (!newPass.equals(againPass)){
                    ToastUtils.showShort(getString(R.string.pass_word_12));
                    return;
                }
                commit();
            });
    }

    private void commit() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("npwd",newPass);
        hashMap.put("opwd",oldPass);
        hashMap.put("token",SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .passWord(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<PassWordRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<PassWordRes> response) {
                        ToastUtils.showShort(getString(R.string.pass_word_13));
                        finish();
                    }

                    @Override
                    public void onFail(ResponseBody<PassWordRes> response) {
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                    }
                });

    }

    @Override
    protected void initData() {

    }
}
