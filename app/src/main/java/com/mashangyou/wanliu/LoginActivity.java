package com.mashangyou.wanliu;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mashangyou.wanliu.api.Contant;
import com.mashangyou.wanliu.api.DefaultObserver;
import com.mashangyou.wanliu.api.RetrofitManager;
import com.mashangyou.wanliu.bean.req.LoginReq;
import com.mashangyou.wanliu.bean.req.RequestBody;
import com.mashangyou.wanliu.bean.res.LoginRes;
import com.mashangyou.wanliu.bean.res.ResponseBody;

import java.util.HashMap;
import java.util.List;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2020/9/9.
 * Des:
 */
public class LoginActivity extends BaseActivity{
    @BindView(R.id.et_user)
    EditText etUser;
    @BindView(R.id.et_pass_word)
    EditText etPassWord;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.cb_show)
    CheckBox cbShow;
    private String user;
    private String passWord;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initToobar() {

    }



    @Override
    protected void initView() {
        initSpan();

        cbShow.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                etPassWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                etPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            etPassWord.setSelection(etPassWord.getText().toString().length());
        });

        btnLogin.setOnClickListener(view -> {
            user = etUser.getText().toString().trim();
            passWord = etPassWord.getText().toString().trim();
            if (TextUtils.isEmpty(user)){
                ToastUtils.showShort(getString(R.string.login_2));
                return;
            }
            if (TextUtils.isEmpty(passWord)){
                ToastUtils.showShort(getString(R.string.login_3));
                return;
            }
            login();
//            ActivityUtils.startActivity(MainActivity.class);
//            finish();
        });


    }

    private void login() {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("username",user);
        hashMap.put("password",passWord);
        RetrofitManager.getApi()
                .login(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<LoginRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<LoginRes> response) {
                        hideLoading();
                        SPUtils.getInstance().put(Contant.ACCESS_TOKEN, response.getData().getToken());
                        SPUtils.getInstance().put(Contant.USER_NAME, user);
                        SPUtils.getInstance().put(Contant.PASS_WORD, passWord);
                        ActivityUtils.startActivity(MainActivity.class);
                        finish();
                    }

                    @Override
                    public void onFail(ResponseBody<LoginRes> response) {
                        hideLoading();
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
        etUser.setText(SPUtils.getInstance().getString(Contant.USER_NAME));
        etPassWord.setText(SPUtils.getInstance().getString(Contant.PASS_WORD));
        if (!TextUtils.isEmpty(etUser.getText().toString())){
            etUser.setSelection(etUser.getText().toString().length());
        }
    }

    private void initSpan() {
        String left = getString(R.string.login_6);
        String right = getString(R.string.login_7);
        SpannableString spannableString = new SpannableString( left+right);
        spannableString.setSpan(new UnderlineSpan(),left.length(),left.length()+right.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.blue)),left.length(),left.length()+right.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPhone.setText(spannableString);
    }
}
