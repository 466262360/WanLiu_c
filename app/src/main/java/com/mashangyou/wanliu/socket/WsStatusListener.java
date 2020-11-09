package com.mashangyou.wanliu.socket;

import okhttp3.Response;
import okio.ByteString;

/**
 * Created by Administrator on 2020/9/11.
 * Des:
 */
public abstract class WsStatusListener {
    public void onOpen(Response response) {
    }

    public void onMessage(String text) {
    }

    public void onMessage(ByteString bytes) {
    }

    public void onReconnect() {

    }

    public void onClosing(int code, String reason) {
    }


    public void onClosed(int code, String reason) {
    }

    public void onFailure(Throwable t, Response response) {
    }
}
