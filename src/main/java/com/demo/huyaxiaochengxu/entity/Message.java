package com.demo.huyaxiaochengxu.entity;

import com.demo.huyaxiaochengxu.service.GiftScheduleManager;

public class Message {
    private String groupId;  //组别id
    private int taskId;   //挑战id
    private String deviceName;   //设备id
    private String action;      //操作了类型 start-->设备开始接受指令，on-->打开设备开关,on-off--->开关设备,off--->关闭设备开关，stop--->设置停止接受指令
    private int duration;   //特效执行的时间
    private int count;    //触发的次数


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
