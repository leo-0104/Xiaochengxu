package com.demo.huyaxiaochengxu.entity;

import java.io.Serializable;

public class Gift implements Serializable {
    private String id;
    private String name;
    private String src;
    private double price;

    public Gift() {
    }

    public Gift(String id, String name, String src, double price) {
        this.id = id;
        this.name = name;
        this.src = src;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
