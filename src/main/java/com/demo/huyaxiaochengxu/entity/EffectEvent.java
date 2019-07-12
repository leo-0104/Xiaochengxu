package com.demo.huyaxiaochengxu.entity;

import java.util.Date;

//特效事件实体类
public class EffectEvent {
    private int id;
    private long uid;
    private int prizeId;
    private int prizeNum;
    private int effectId;
    private String effectText;
    private long addTime = new Date().getTime();
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
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
}
