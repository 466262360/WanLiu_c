package com.mashangyou.wanliu.socket;

import okhttp3.WebSocket;
import okio.ByteString;

/**
 * Created by Administrator on 2020/9/11.
 * Des:
 */
interface IWsManager {
    WebSocket getWebSocket();

    void startConnect();

    void stopConnect();

    boolean isWsConnected();

    int getCurrentStatus();

    void setCurrentStatus(int currentStatus);

    boolean sendMessage(String msg);

    boolean sendMessage(ByteString byteString);
}
