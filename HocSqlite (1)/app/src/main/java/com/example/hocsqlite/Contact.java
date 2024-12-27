package com.example.hocsqlite;

import java.io.Serializable;

public class Contact implements Serializable {
    private int ma;
    private String ten;
    private String dienthoai;

    public Contact(int ma, String ten, String dienthoai) {
        this.ma = ma;
        this.ten = ten;
        this.dienthoai = dienthoai;
    }

    public int getMa() { return ma; }
    public void setMa(int ma) { this.ma = ma; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getDienthoai() { return dienthoai; }
    public void setDienthoai(String dienthoai) { this.dienthoai = dienthoai; }

    @Override
    public String toString() {
        return ma + " - " + ten + " - " + dienthoai;
    }
}
