package com.mashangyou.wanliu.socket;

import com.easysocket.entity.basemsg.SuperSender;

/**
 * Created by Administrator on 2020/9/17.
 * Des:
 */
public class ClientHeartBeat extends SuperSender {
    private String msgId;
    private String from;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "ClientHeartBeat{" +
                "msgId='" + msgId + '\'' +
                ", from='" + from + '\'' +
                '}';
    }

}
