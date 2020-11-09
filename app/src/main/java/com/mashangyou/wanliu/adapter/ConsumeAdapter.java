package com.mashangyou.wanliu.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mashangyou.wanliu.R;
import com.mashangyou.wanliu.bean.res.ConsumeRes;
import com.mashangyou.wanliu.bean.res.CountWriteRes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by Administrator on 2020/9/10.
 * Des:
 */
public class ConsumeAdapter extends BaseQuickAdapter<CountWriteRes.Content, BaseViewHolder> {

    public ConsumeAdapter(int layoutResId, @Nullable List<CountWriteRes.Content> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, CountWriteRes.Content consumeRes) {
        baseViewHolder.setText(R.id.tv_member_name, consumeRes.getMemberName())
                .setText(R.id.tv_peoples, consumeRes.getPeoples()+"")
                .setText(R.id.tv_frequ, consumeRes.getFrequ()+"");
    }
}
