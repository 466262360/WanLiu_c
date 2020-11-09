package com.mashangyou.wanliu.adapter;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mashangyou.wanliu.R;
import com.mashangyou.wanliu.bean.req.VerifyReq;
import com.mashangyou.wanliu.bean.res.ConsumeRes;
import com.mashangyou.wanliu.bean.res.VerifyRes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class OrderAdapter extends BaseQuickAdapter<VerifyRes.Orders, BaseViewHolder> {

    private SimpleDateFormat format;

    public OrderAdapter(int layoutResId, @Nullable List<VerifyRes.Orders> data) {
        super(layoutResId, data);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }



    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, VerifyRes.Orders consumeRes) {
        baseViewHolder.setText(R.id.tv_order_id, getContext().getString(R.string.code_result_13) + consumeRes.getOrderId())
                .setText(R.id.tv_num, getContext().getString(R.string.code_result_14) + consumeRes.getPeoples())
                .setText(R.id.tv_count, getContext().getString(R.string.code_result_15) + consumeRes.getCaves())
                .setText(R.id.tv_time, getContext().getString(R.string.code_result_18) + getDate(consumeRes.getPlayTime()));
        if (consumeRes.isSel()) {
            baseViewHolder.setBackgroundResource(R.id.iv_bottom, R.drawable.item_order_bottom_sel)
                    .setBackgroundResource(R.id.iv_sel, R.drawable.cb_sel_true)
                    .setBackgroundResource(R.id.iv_bg, R.drawable.item_order_bg_true);
        } else {
            baseViewHolder.setBackgroundResource(R.id.iv_bottom, R.drawable.item_order_bottom)
                    .setBackgroundResource(R.id.iv_sel, R.drawable.cb_sel_false)
                    .setBackgroundResource(R.id.iv_bg, R.drawable.item_order_bg);
        }

        getDate(consumeRes.getPlayTime());
    }

    private String getDate(String playTime) {
        Date date = TimeUtils.string2Date(playTime);
        return  format.format(date);


    }
}
