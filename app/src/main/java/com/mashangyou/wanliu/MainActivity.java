package com.mashangyou.wanliu;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.easysocket.EasySocket;
import com.easysocket.connection.heartbeat.HeartManager;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.easysocket.utils.LogUtil;
import com.mashangyou.scan.ScannerActivity;
import com.mashangyou.wanliu.adapter.ConsumeAdapter;
import com.mashangyou.wanliu.adapter.TotalRecordAdapter;
import com.mashangyou.wanliu.api.Contant;
import com.mashangyou.wanliu.api.DefaultObserver;
import com.mashangyou.wanliu.api.RetrofitManager;
import com.mashangyou.wanliu.bean.res.CountWriteRes;
import com.mashangyou.wanliu.bean.res.ResponseBody;
import com.mashangyou.wanliu.bean.res.SelectWriteRes;
import com.mashangyou.wanliu.bean.res.VerifyRes;
import com.mashangyou.wanliu.socket.ClientHeartBeat;
import com.mashangyou.wanliu.util.MessageCenter;
import com.mashangyou.wanliu.util.MessageType;
import com.mashangyou.wanliu.util.SerializableMap;
import com.smartdevice.aidl.IZKCService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.mashangyou.wanliu.api.Contant.SCAN_RESULT;

public class MainActivity extends BaseActivity {
    @BindView(R.id.rv_consume)
    RecyclerView rvConsume;
    @BindView(R.id.rv_record)
    RecyclerView rvRecord;
    @BindView(R.id.iv_mine)
    ImageView ivMine;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.tv_mine)
    TextView tvMine;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.cl_mine)
    ConstraintLayout clMine;
    private boolean isExit;
    private static final int REQ = 10000;
    private List<CountWriteRes.Content> countWriteList;
    public static int module_flag = 0;
    public static int DEVICE_MODEL = 0;
    //线程运行标志 the running flag of thread
    private boolean runFlag = true;
    //打印机检测标志 the detect flag of printer
    private boolean detectFlag = false;
    private Handler mhanlder;
    DetectPrinterThread mDetectPrinterThread;
    public static IZKCService mIzkcService;
    private float PINTER_LINK_TIMEOUT_MAX = 30 * 1000L;
    private List<CountWriteRes.Content> currentMonthList=new ArrayList<>();
    private List<CountWriteRes.Content> sevendaysList=new ArrayList<>();
    private List<CountWriteRes.Content> yearsList=new ArrayList<>();
    private ConsumeAdapter consumeAdapter;
    private List<SelectWriteRes.Record> recordList;
    private TotalRecordAdapter totalRecordAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initToobar() {

    }

    @Override
    protected void initView() {
        clMine.setOnClickListener(view -> ActivityUtils.startActivity(MineActivity.class));
        ivScan.setOnClickListener(view -> ActivityUtils.startActivityForResult(this, ScannerActivity.class, REQ));
        rg.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_week:
                    countWriteList.clear();
                    countWriteList.addAll(sevendaysList);
                    consumeAdapter.setList(countWriteList);
                    break;
                case R.id.rb_month:
                    countWriteList.clear();
                    countWriteList.addAll(currentMonthList);
                    consumeAdapter.setList(countWriteList);
                    break;
                case R.id.rb_year:
                    countWriteList.clear();
                    countWriteList.addAll(yearsList);
                    consumeAdapter.setList(countWriteList);
                    break;
            }
        });
        countWriteList = new ArrayList<>();
        consumeAdapter = new ConsumeAdapter(R.layout.item_consume, countWriteList);
        View consumeTop = getLayoutInflater().inflate(R.layout.consume_rv_top, null);
        consumeAdapter.setHeaderView(consumeTop);
        rvConsume.setLayoutManager(new LinearLayoutManager(this));
        rvConsume.setAdapter(consumeAdapter);

        recordList = new ArrayList<>();
        totalRecordAdapter = new TotalRecordAdapter(R.layout.item_total_record, recordList);
        View totalRecordTop = getLayoutInflater().inflate(R.layout.total_record_rv_top, null);
        totalRecordAdapter.setHeaderView(totalRecordTop);
        rvRecord.setLayoutManager(new LinearLayoutManager(this));
        rvRecord.setAdapter(totalRecordAdapter);
    }

    @Override
    protected void initData() {
        Glide.with(this)
                .load(R.drawable.main_pic)
                .transform(new CenterCrop(), new RoundedCorners(ConvertUtils.dp2px(11)))
                .into(ivPic);
        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
        startHeartbeat();
        bindPrintService();
        // mServiceIntent = new Intent(this, WsService.class);
    }

    private void countWrite() {
        RetrofitManager.getApi()
                .countWrite(SPUtils.getInstance().getString(Contant.ACCESS_TOKEN))
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<CountWriteRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<CountWriteRes> response) {
                        currentMonthList = response.getData().getCurrentMonth();
                        sevendaysList = response.getData().getSevendays();
                        yearsList = response.getData().getThisYear();
                        countWriteList.clear();
                        countWriteList.addAll(currentMonthList);
                        consumeAdapter.setList(countWriteList);

                    }

                    @Override
                    public void onFail(ResponseBody<CountWriteRes> response) {
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
    }

    private void selectWrite() {
        RetrofitManager.getApi()
                .selectWrite(SPUtils.getInstance().getString(Contant.ACCESS_TOKEN))
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<SelectWriteRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<SelectWriteRes> response) {
                        recordList = response.getData().getRecord();
                        totalRecordAdapter.setList(recordList);
                    }

                    @Override
                    public void onFail(ResponseBody<SelectWriteRes> response) {
                        ToastUtils.showShort(response.getErrMsg());
                    }

                    @Override
                    public void onError(String s) {

                    }
                });
    }

    private void bindPrintService() {
        //com.zkc.aidl.all为远程服务的名称，不可更改
        //com.smartdevice.aidl为远程服务声明所在的包名，不可更改，
        // 对应的项目所导入的AIDL文件也应该在该包名下
        Intent intent = new Intent("com.zkc.aidl.all");
        intent.setPackage("com.smartdevice.aidl");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        detectFlag = true;
        countWrite();
        selectWrite();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    // 注册广播
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScannerActivity.MESSAGE_ACTION);
        intentFilter.addAction(Contant.PRINT_ACTION);
        registerReceiver(mReceiver, intentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 消息广播
            if (action.equals(ScannerActivity.MESSAGE_ACTION)) {
                String code = intent.getStringExtra(ScannerActivity.MESSAGE_BARCODE);
                verify(code);
            } else if (action.equals(Contant.PRINT_ACTION)) {
                Bundle bundle = intent.getBundleExtra(Contant.PRINT_MAP);
                SerializableMap map = (SerializableMap) bundle.get(Contant.PRINT_MAP);
                Map<String, String> printMap = map != null ? map.getMap() : null;
                print(printMap);
            }
        }
    };

    private void print(Map<String, String> printMap) {

        try {
            if (printMap == null) {
                ToastUtils.showLong("无打印内容");
                return;
            }
            if (mIzkcService==null||!mIzkcService.checkPrinterAvailable()) {
                ToastUtils.showLong("打印机不可用");
                return;
            }
            String printerStatus = mIzkcService.getPrinterStatus();
            LogUtils.d(printerStatus);
            if ("缺纸".equals(printerStatus)) {
                ToastUtils.showLong("打印机缺纸");
                return;
            }

            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("         订场消费凭证\n");
            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("会籍信息\n");
            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("会员姓名： " + printMap.get(Contant.PRINT_NAME) + "\n");
            mIzkcService.printUnicodeText("会员ID：   " + printMap.get(Contant.PRINT_ID) + "\n");
            mIzkcService.printUnicodeText("会籍卡：   " + printMap.get(Contant.PRINT_MEMBER_NAME) + "\n");
            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("订单信息\n");
            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("打球时间： " + printMap.get(Contant.PRINT_DATE) + "\n");
            mIzkcService.printUnicodeText("打球人数： " + printMap.get(Contant.PRINT_PEOPLE) + "\n");
            mIzkcService.printUnicodeText("洞数：     " + printMap.get(Contant.PRINT_CAVES) + "\n");
            mIzkcService.printUnicodeText("订单号：   " + printMap.get(Contant.PRINT_ORDER) + "\n");
            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("打印时间： " + printMap.get(Contant.PRINT_CURRENT_DATE) + "\n");
            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("会员签名：\n");
            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("__________________________\n");
            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("\n");
            mIzkcService.printUnicodeText("\n");
//            byte[] data = new byte[3];
//            data[0] = 0x1B;
//            data[1] = 0x33;
//            data[2] = 0x00;
//            mIzkcService.sendRAWData("print",data);
        } catch (RemoteException e) {
            ToastUtils.showShort("打印服务未连接...");
            e.printStackTrace();
        }
    }

    private void verify(String code) {
        showLoading();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("brcode", code);
        hashMap.put("token", SPUtils.getInstance().getString(Contant.ACCESS_TOKEN));
        RetrofitManager.getApi()
                .verify(hashMap)
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<ResponseBody<VerifyRes>>() {
                    @Override
                    public void onSuccess(ResponseBody<VerifyRes> response) {
                        hideLoading();
                        VerifyRes data = response.getData();
                        if (data != null) {
                            EventBus.getDefault().postSticky(data);
                            ActivityUtils.startActivity(CodeResultActivity.class);
                        }
                    }

                    @Override
                    public void onFail(ResponseBody<VerifyRes> response) {
                        hideLoading();
                        int errorCode = response.getCode();
                        if (errorCode == 1) {
                            ToastUtils.showShort("查询失败");
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putInt(SCAN_RESULT, errorCode);
                            ActivityUtils.startActivity(bundle, ScanErrorActivity.class);
                        }
                    }

                    @Override
                    public void onError(String s) {
                        hideLoading();
                    }
                });
    }

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIzkcService = null;
            Toast.makeText(MainActivity.this, getString(R.string.service_bind_fail), Toast.LENGTH_SHORT).show();
            //发送消息绑定失败 send message to notify bind fail
            sendEmptyMessage(MessageType.BaiscMessage.SEVICE_BIND_FAIL);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIzkcService = IZKCService.Stub.asInterface(service);
            if (mIzkcService != null) {
                LogUtils.d("绑定成功");
                try {
                    //获取产品型号 get product model
                    DEVICE_MODEL = mIzkcService.getDeviceModel();
                    //设置当前模块 set current function module
                    mIzkcService.setModuleFlag(module_flag);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                //发送消息绑定成功 send message to notify bind success
                sendEmptyMessage(MessageType.BaiscMessage.SEVICE_BIND_SUCCESS);
                mDetectPrinterThread = new DetectPrinterThread();
                mDetectPrinterThread.start();
            }
        }
    };


    protected void sendMessage(int what, Object obj) {
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        getHandler().sendMessage(message);
    }


    /**
     * handler
     */
    protected Handler getHandler() {
        if (mhanlder == null) {
            mhanlder = new Handler() {
                public void handleMessage(Message msg) {
                    handleStateMessage(msg);
                }
            };
        }
        return mhanlder;
    }

    protected void sendEmptyMessage(int what) {
        getHandler().sendEmptyMessage(what);
    }

    public void bindService() {
        //com.zkc.aidl.all为远程服务的名称，不可更改
        //com.smartdevice.aidl为远程服务声明所在的包名，不可更改，
        // 对应的项目所导入的AIDL文件也应该在该包名下
        Intent intent = new Intent("com.zkc.aidl.all");
        intent.setPackage("com.smartdevice.aidl");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    protected void handleStateMessage(Message message) {
        switch (message.what) {
            //服务绑定成功 service bind success
            case MessageType.BaiscMessage.SEVICE_BIND_SUCCESS:
//				Toast.makeText(this, getString(R.string.service_bind_success), Toast.LENGTH_SHORT).show();
                break;
            //服务绑定失败 service bind fail
            case MessageType.BaiscMessage.SEVICE_BIND_FAIL:
//				Toast.makeText(this, getString(R.string.service_bind_fail), Toast.LENGTH_SHORT).show();
                break;
            //打印机连接成功 printer link success
            case MessageType.BaiscMessage.DETECT_PRINTER_SUCCESS:
                String msg = (String) message.obj;
                //checkPrintStateAndDisplayPrinterInfo(msg);
                break;
            //打印机连接超时 printer link timeout
            case MessageType.BaiscMessage.PRINTER_LINK_TIMEOUT:
                Toast.makeText(this, getString(R.string.printer_link_timeout), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void checkPrintStateAndDisplayPrinterInfo(String msg) {

        String status;
        String aidlServiceVersion;
        try {
            mIzkcService.getPrinterStatus();
            status = mIzkcService.getPrinterStatus();
            aidlServiceVersion = mIzkcService.getServiceVersion();
            //打印自检信息
            //mIzkcService.printerSelfChecking();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class DetectPrinterThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (runFlag) {
                float start_time = SystemClock.currentThreadTimeMillis();
                float end_time = 0;
                float time_lapse = 0;
                if (detectFlag) {
                    //检测打印是否正常 detect if printer is normal
                    try {
                        if (mIzkcService != null) {
                            String printerSoftVersion = mIzkcService.getFirmwareVersion1();
                            if (TextUtils.isEmpty(printerSoftVersion)) {
                                mIzkcService.setModuleFlag(module_flag);
                                end_time = SystemClock.currentThreadTimeMillis();
                                time_lapse = end_time - start_time;
//								enableOrDisEnableKey(false);
                                if (time_lapse > PINTER_LINK_TIMEOUT_MAX) {
                                    detectFlag = false;
                                    //打印机连接超时 printer link timeout
                                    sendEmptyMessage(MessageType.BaiscMessage.PRINTER_LINK_TIMEOUT);
                                }
                            } else {
                                //打印机连接成功 printer link success
                                sendMessage(MessageType.BaiscMessage.DETECT_PRINTER_SUCCESS, printerSoftVersion);
                                detectFlag = false;
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
                SystemClock.sleep(1);
            }
        }
    }

    // 启动心跳检测功能
    private void startHeartbeat() {
        // 心跳实例
        ClientHeartBeat clientHeartBeat = new ClientHeartBeat();
        clientHeartBeat.setMsgId("heart_beat");
        clientHeartBeat.setFrom("client");
        EasySocket.getInstance().startHeartBeat(clientHeartBeat, new HeartManager.HeartbeatListener() {
            @Override
            public boolean isServerHeartbeat(OriginReadData originReadData) {
                String msg = originReadData.getBodyString();
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    if ("heart_beat".equals(jsonObject.getString("msgId"))) {
                        LogUtil.d("收到服务器心跳");
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    /**
     * socket行为监听
     */
    private ISocketActionListener socketActionListener = new SocketActionListener() {
        /**
         * socket连接成功
         * @param socketAddress
         */
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            super.onSocketConnSuccess(socketAddress);
            LogUtil.d("连接成功");
        }

        /**
         * socket连接失败
         * @param socketAddress
         * @param isReconnect 是否需要重连
         */
        @Override
        public void onSocketConnFail(SocketAddress socketAddress, Boolean isReconnect) {
            super.onSocketConnFail(socketAddress, isReconnect);
        }

        /**
         * socket断开连接
         * @param socketAddress
         * @param isReconnect 是否需要重连
         */
        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, Boolean isReconnect) {
            super.onSocketDisconnect(socketAddress, isReconnect);
        }

        /**
         * socket接收的数据
         * @param socketAddress
         * @param originReadData
         */
        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
            LogUtil.d("socket监听器收到数据=" + originReadData.getBodyString());

        }
    };

    @Override
    protected void onDestroy() {
        unbindService(mServiceConn);
        unregisterReceiver(mReceiver);
        MessageCenter.getInstance().removeHandler(getHandler());
        //禁止打印
        if (mIzkcService != null) try {
            mIzkcService.sendRAWData("printer", new byte[]{0x1E, 0x03, 0x00,
                    (byte) 0xBF, (byte) 0xD8, (byte) 0xD6, (byte) 0xC6});
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (mDetectPrinterThread != null) {
            runFlag = false;
            mDetectPrinterThread.interrupt();
            mDetectPrinterThread = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isExit) {
                finish();
            } else {
                ToastUtils.showShort("再按一次退出");
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
