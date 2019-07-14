package com.demo.huyaxiaochengxu.entity;

import java.util.List;

//挑战进度实体类
public class Schedule {
    private int id;                   //挑战ID
    private int status;               //挑战状态码0	送礼尚未完成，1	送礼已完成，挑战尚未完成  2	送礼已完成，挑战已完成
    private int total;                 //所需礼物总数
    private int count;                 //已送礼物数量
    private String scale;                 //挑战进度
    private boolean finished;          //挑战是否已完成
    private Gift gift;                 //礼物信息
    private Event effect;               //特效信息
    private List<Assist> assistList;   //助攻者信息

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getScale() {
        return scale;
    }

    public void setScale() {
        //已送礼物数量 >= 所需礼物总数
        if (count >= total){
           this.scale = "1";
        }else{
           this.scale = String.format("%.2f",(float)count / (float)total);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Gift getGift() {
        return gift;
    }

    public void setGift(Gift gift) {
        this.gift = gift;
    }

    public Event getEffect() {
        return effect;
    }

    public void setEffect(Event effect) {
        this.effect = effect;
    }

    public List<Assist> getAssistList() {
        return assistList;
    }

    public void setAssistList(List<Assist> assistList) {
        this.assistList = assistList;
    }
}
