package com.example.myapplication;

public class SanPham {
    private  String ten;
    private  int gia;
    private  int anh;
    private  String sp;
    private  String cateri;
    public  SanPham(String ten,int gia,int anh,String sp){
        this.anh=anh;
        this.gia=gia;
        this.ten=ten;
        this.sp=sp;
    }
    public  String getCateri(){return  cateri;}
    public  String getTen(){return  ten;}
    public  String getSp(){return  sp;}
    public  int getGia(){return  gia;}
    public  int getAnh(){return anh;}
}
