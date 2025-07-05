package com.example.myapplication.admin;




public class QLUSER {

    public String uid;
    public String email;
    public String name;
    public String phoneNumber;
    public String role;
    public boolean isEmailVerified;
    public long createdAt;
    public QLUSER() {
        // Required for Firebase
    }
    public QLUSER(String uid, String email, String name, String phoneNumber, String role, boolean isEmailVerified, long createdAt) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isEmailVerified = isEmailVerified;
        this.createdAt = createdAt;
    }
}



