package com.example.myapplication;

import java.io.Serializable;

public class xemchitiet implements Serializable {
    private float danhgia;
    private String binhluan;
    private String tensp;

    public xemchitiet(float rating, String comment, String userName) {
        this.danhgia = rating;
        this.binhluan = comment;
        this.tensp = userName;
    }

    public float getRating() { return danhgia; }
    public String getComment() { return binhluan; }
    public String getUserName() { return tensp; }
}