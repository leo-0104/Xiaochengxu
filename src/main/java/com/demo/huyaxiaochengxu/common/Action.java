package com.demo.huyaxiaochengxu.common;

//设置指令操作
public enum Action{
    START("start"),
    ON("on"),
    ON_OFF("on-off"),
    OFF("off"),
    STOP("stop");

    Action(String action){
        this.action = action;
    }
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
