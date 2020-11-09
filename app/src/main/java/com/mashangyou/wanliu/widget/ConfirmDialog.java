package com.mashangyou.wanliu.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.mashangyou.wanliu.R;

import androidx.annotation.NonNull;

/**
 * Created by Administrator on 2020/4/2.
 * Des:
 */
public class ConfirmDialog extends Dialog {

    private OnConfirmClickListener onConfirmListener;
    private TextView tv_message;
    private TextView btn_confirm;

    public ConfirmDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_custom);
        btn_confirm = findViewById(R.id.btn_confirm);
        TextView btn_cancel = findViewById(R.id.btn_cancel);
        tv_message = findViewById(R.id.tv_message);


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onConfirmListener!=null)
                    onConfirmListener.onConfirmClickListener();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void setMessage(String message){
        tv_message.setText(message);
    }
    public void setRightButtonText(String text){
        btn_confirm.setText(text);
    }
    public interface OnConfirmClickListener {
        void onConfirmClickListener();
    }

    public void setOnConfirmListener(OnConfirmClickListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }
}
