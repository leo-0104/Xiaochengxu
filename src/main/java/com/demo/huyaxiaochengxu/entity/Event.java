package com.demo.huyaxiaochengxu.entity;

import java.io.Serializable;

public class Event implements Serializable {
    private int id;
    private int type;
    private String desc;
    private String urge;

    public Event() {
    }

    public Event(int id, int type, String desc, String urge) {
        this.id = id;
        this.type = type;
        this.desc = desc;
        this.urge = urge;
    }

    public int getId() {
        return id;
    }

    public Event setId(int id) {
        this.id = id;
        return this;
    }

    public int getType() {
        return type;
    }

    public Event setType(int type) {
        this.type = type;
        return this;
    }

    public String getDesc() {
        return desc;
    }

    public Event setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getUrge() {
        return urge;
    }

    public Event setUrge(String urge) {
        this.urge = urge;
        return this;
    }
}
