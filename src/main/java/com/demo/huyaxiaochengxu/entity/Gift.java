package com.demo.huyaxiaochengxu.entity;

import java.io.Serializable;

public class Gift implements Serializable {
    private String id;
    private String name;
    private String src;
    private double prize;

    public Gift() {
    }

    public Gift(String id, String name, String src, double prize) {
        this.id = id;
        this.name = name;
        this.src = src;
        this.prize = prize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize) {
        this.prize = prize;
    }
}
