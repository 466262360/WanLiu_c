package com.mashangyou.wanliu.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Created by Administrator on 2020/9/11.
 * Des:
 */
public class WsService extends Service {
    public static final String MESSAGE_ACTION ="10000";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
