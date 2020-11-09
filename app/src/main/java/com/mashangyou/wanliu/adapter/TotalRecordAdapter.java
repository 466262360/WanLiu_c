package com.mashangyou.wanliu.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mashangyou.wanliu.R;
import com.mashangyou.wanliu.bean.res.ConsumeRes;
import com.mashangyou.wanliu.bean.res.SelectWriteRes;
import com.mashangyou.wanliu.bean.res.TotalRecordRes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class TotalRecordAdapter extends BaseQuickAdapter<SelectWriteRes.Record,BaseViewHolder> {

    public TotalRecordAdapter(int layoutResId, @Nullable List<SelectWriteRes.Record> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, SelectWriteRes.Record consumeRes) {
        baseViewHolder.setText(R.id.tv_date, consumeRes.getDate())
                .setText(R.id.tv_member_name, consumeRes.getMemberName())
                .setText(R.id.tv_frequ, consumeRes.getFrequ()+"");
    }
}
