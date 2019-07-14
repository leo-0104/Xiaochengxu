package com.demo.huyaxiaochengxu.entity;

import com.demo.huyaxiaochengxu.util.DateUtil;

import java.util.Date;

//特效事件实体类
public class EffectEvent {
    private int id;
    private String uid;
    private int prizeId;
    private int prizeNum;
    private int effectId;
    private String effectText;
    private long addTime = DateUtil.getCurrentTime();
    private int status;
    private String groupId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(int prizeId) {
        this.prizeId = prizeId;
    }

    public int getPrizeNum() {
        return prizeNum;
    }

    public void setPrizeNum(int prizeNum) {
        this.prizeNum = prizeNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getEffectId() {
        return effectId;
    }

    public void setEffectId(int effectId) {
        this.effectId = effectId;
    }

    public String getEffectText() {
        return effectText;
    }

    public void setEffectText(String effectText) {
        this.effectText = effectText;
    }


    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
