package com.mashangyou.scan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by Administrator on 2016/11/28.
 * 扫码Activity
 */
public class ScannerActivity extends AppCompatActivity implements OnDecodeCompletionListener {

    ScannerView cScannerView;
    ImageView iv_back;
    public static final String SCANRES = "scan_res";
    public static final String MESSAGE_ACTION = "com.mashangyou.wanliu.barCode";
    public static final String MESSAGE_BARCODE = "com.mashangyou.wanliu.barCodeMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        cScannerView = findViewById(R.id.scanner_view);
        iv_back = findViewById(R.id.iv_back);
        cScannerView.setOnDecodeListener(this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        cScannerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cScannerView.onPause();
    }

    @Override
    public void onDecodeCompletion(String code) {

        if (!TextUtils.isEmpty(code)) {
            Intent intent = new Intent();
            intent.setAction(MESSAGE_ACTION);
            intent.putExtra(MESSAGE_BARCODE, code);
            sendBroadcast(intent);
            finish();
        } else {
            Toast.makeText(this, "解析错误", Toast.LENGTH_SHORT).show();
        }
    }
}
