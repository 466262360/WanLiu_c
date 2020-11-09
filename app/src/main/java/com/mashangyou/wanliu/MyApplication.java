package com.mashangyou.wanliu;

import android.app.Application;

import com.easysocket.EasySocket;
import com.easysocket.config.DefaultMessageProtocol;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.SocketAddress;
import com.mashangyou.wanliu.util.AppUncaughtExceptionHandler;

/**
 * Created by Administrator on 2020/9/17.
 * Des:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //initEasySocket();
    }

    private void initEasySocket() {
        // socket配置
        EasySocketOptions options = new EasySocketOptions.Builder()
                .setSocketAddress(new SocketAddress("192.168.1.102", 9999)) // 主机地址
               // .setCallbackIdKeyFactory(new CallbackIdKeyFactoryImpl())
                // 最好定义一个消息协议，方便解决 socket黏包、分包的问题
                 //.setReaderProtocol(new DefaultMessageProtocol()) // 默认的消息协议
                .build();

        // 初始化EasySocket
        EasySocket connection = EasySocket.getInstance()
                .options(options) // 项目配置
                .createConnection();// 创建一个socket连接
// 捕捉异常
        AppUncaughtExceptionHandler crashHandler = AppUncaughtExceptionHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
