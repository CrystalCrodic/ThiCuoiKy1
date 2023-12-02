package com.example.androidwebservice.model;

public class Sinhvien {
    private int maSV;
    private String tenSV;

    public Sinhvien() {
    }

    public Sinhvien(int maSV, String tenSV) {
        this.maSV = maSV;
        this.tenSV = tenSV;
    }

    public int getMaSV() {
        return maSV;
    }

    public void setMaSV(int maSV) {
        this.maSV = maSV;
    }

    public String getTenSV() {
        return tenSV;
    }

    public void setTenSV(String tenSV) {
        this.tenSV = tenSV;
    }

    @Override
    public String toString() {
        return "Mã: " + maSV + "\n"
                + "Tên: " + tenSV;
    }
}

