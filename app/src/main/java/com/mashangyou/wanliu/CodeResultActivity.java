package com.mashangyou.wanliu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.mashangyou.wanliu.adapter.OrderAdapter;
import com.mashangyou.wanliu.api.Contant;
import com.mashangyou.wanliu.api.DefaultObserver;
import com.mashangyou.wanliu.api.RetrofitManager;
import com.mashangyou.wanliu.bean.res.LoginRes;
import com.mashangyou.wanliu.bean.res.ResponseBody;
import com.mashangyou.wanliu.bean.res.VerifyRes;
import com.mashangyou.wanliu.util.SerializableMap;
import com.mashangyou.wanliu.widget.ConfirmDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.mashangyou.wanliu.api.Contant.SCAN_RESULT;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class CodeResultActivity extends BaseActivity {
    @BindView(R.id.title)
    ConstraintLayout title;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_check)
    Button btnCheck;
    @BindView(R.id.group_no_order)
    Group groupNoOrder;
    @BindView(R.id.group_list)
    Group groupList;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_id)
    TextView tvID;
    @BindView(R.id.tv_card)
    TextView tvCard;
    List<VerifyRes.Orders> ordersList = new ArrayList<>();
    private TextView tvTitle;
    private VerifyRes.Orders currentOrders;
    private VerifyRes userInfo;
    private String orderId;
    private SimpleDateFormat format;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_code_result;
    }

    @Override
    protected void initToobar() {
        tvTitle = title.findViewById(R.id.tv_title);
        title.setOnClickListener(view -> finish());
    }


    @Override
    protected void initView() {
        btnBack.setOnClickListener(view -> finish());
        btnCheck.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(orderId)&&currentOrders!=null) {
                ConfirmDialog confirmDialog = new ConfirmDialog(this);
                confirmDialog.setOnConfirmListener(new ConfirmDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClickListener() {
                        use();
                        confirmDialog.dismiss();
                    }
                });
                confirmDialog.show();
                confirmDialog.setMessage(getString(R.string.code_result_16));
                confirmDialog.setRightButtonText(getString(R.string.code_result_17));
            }else{
                ToastUtils.showShort(getString(R.string.code_result_19));
            }
        });
    }

    private void test() {
        sendPrint();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Contant.VERIFY_RESULT, true);
        ActivityUtils.startActivity(bundle, VerifyResultActivity.class);
        finish();
    }

    private void use() {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId", orderId);
        hashMap.put("token", SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .use(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        hideLoading();
                        sendPrint();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Contant.VERIFY_RESULT, true);
                        ActivityUtils.startActivity(bundle, VerifyResultActivity.class);
                        finish();
                    }

                    @Override
                    public void onFail(ResponseBody response) {
                        hideLoading();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Contant.VERIFY_RESULT, false);
                        ActivityUtils.startActivity(bundle, VerifyResultActivity.class);
                        finish();
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                    }
                });
    }

    private void sendPrint() {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Contant.PRINT_NAME, userInfo.getName());
        hashMap.put(Contant.PRINT_ID, userInfo.getTcode());
        hashMap.put(Contant.PRINT_MEMBER_NAME, userInfo.getMemberName());
        hashMap.put(Contant.PRINT_DATE, getDate(currentOrders.getPlayTime()));
        hashMap.put(Contant.PRINT_PEOPLE, currentOrders.getPeoples());
        hashMap.put(Contant.PRINT_CAVES, currentOrders.getCaves());
        hashMap.put(Contant.PRINT_ORDER, currentOrders.getOrderId());
        hashMap.put(Contant.PRINT_GOLFNAME, currentOrders.getGolfName());
        hashMap.put(Contant.PRINT_FREQUENCY, currentOrders.getFrequency());
        hashMap.put(Contant.PRINT_CURRENT_DATE, TimeUtils.getNowString(format));
        SerializableMap map = new SerializableMap();
        map.setMap(hashMap);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Contant.PRINT_MAP,map);
        Intent intent = new Intent();
        intent.setAction(Contant.PRINT_ACTION);
        intent.putExtra(Contant.PRINT_MAP,bundle);
        sendBroadcast(intent);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    private String getDate(String playTime) {
        Date date = TimeUtils.string2Date(playTime);
        return  format.format(date);


    }

    private void initRv() {
        OrderAdapter orderAdapter = new OrderAdapter(R.layout.item_order, ordersList);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(orderAdapter);
        orderAdapter.setOnItemClickListener((adapter, view, position) -> {
            for (VerifyRes.Orders item : ordersList) {
                item.setSel(false);
            }
            ordersList.get(position).setSel(true);
            currentOrders = ordersList.get(position);
            orderId = ordersList.get(position).getOrderId();
            orderAdapter.notifyDataSetChanged();
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void receive(VerifyRes data) {
        if (data != null) {
            userInfo=data;
            tvName.setText(getString(R.string.code_result_6) + userInfo.getName());
            tvID.setText(getString(R.string.code_result_7) + userInfo.getTcode());
            tvCard.setText(getString(R.string.code_result_8) + userInfo.getMemberName());
            Glide.with(this)
                    .load(userInfo.getImg())
                    .transform(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(11)))
                    .into(ivIcon);

            ordersList = userInfo.getOrders();
            if (ordersList != null && ordersList.size() > 0) {
                tvTitle.setText(getString(R.string.code_result_12));
                groupList.setVisibility(View.VISIBLE);
                initRv();
            } else {
                tvTitle.setText(getString(R.string.code_result_1));
                groupNoOrder.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
