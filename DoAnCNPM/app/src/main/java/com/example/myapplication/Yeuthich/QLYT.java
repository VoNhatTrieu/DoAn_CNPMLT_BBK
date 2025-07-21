package com.example.myapplication.Yeuthich;

import com.example.myapplication.SanPham;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QLYT {
    private static QLYT instance;
    private List<SanPham> wishlistItems;
    private FirebaseFirestore db;
    private String userId;

    private QLYT() {
        wishlistItems = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadWishlistFromFirestore();
        }
    }

    public static QLYT getInstance() {
        if (instance == null) {
            instance = new QLYT();
        }
        return instance;
    }

    private void loadWishlistFromFirestore() {
        db.collection("favorites").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    wishlistItems.clear();
                    List<Map<String, Object>> list = (List<Map<String, Object>>) documentSnapshot.get("sanPhams");
                    if (list != null) {
                        for (Map<String, Object> item : list) {
                            String ten = (String) item.get("ten");
                            Long gia = getLong(item.get("gia"));
                            Long anh = getLong(item.get("anh"));
                            String cateri = (String) item.get("cateri");
                            String imageUrl = (String) item.get("imageUrl");
                            String mota = (String) item.get("mota");
                            Long soLuong = getLong(item.get("soLuong"));

                            SanPham sp = new SanPham(ten, gia.intValue(), anh.intValue(), cateri, imageUrl, mota, soLuong.intValue());
                            wishlistItems.add(sp);
                        }
                    }
                });
    }

    private void saveWishlistToFirestore() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> spList = new ArrayList<>();

        for (SanPham sp : wishlistItems) {
            Map<String, Object> item = new HashMap<>();
            item.put("ten", sp.getTen());
            item.put("gia", sp.getGia());
            item.put("anh", sp.getAnh());
            item.put("cateri", sp.getCateri());
            item.put("imageUrl", sp.getImageUrl());
            item.put("mota", sp.getMota());
            item.put("soLuong", sp.getSoLuong());
            item.put("timestamp", sp.getTimestamp());
            spList.add(item);
        }

        data.put("sanPhams", spList);
        db.collection("favorites").document(userId)
                .set(data, SetOptions.merge());
    }

    private Long getLong(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        return 0L;
    }

    public void addToWishlist(SanPham sanPham) {
        if (!isInWishlist(sanPham)) {
            wishlistItems.add(sanPham);
            saveWishlistToFirestore();
        }
    }

    public void removeFromWishlist(SanPham sanPham) {
        wishlistItems.removeIf(item -> item.getTen().equals(sanPham.getTen()));
        saveWishlistToFirestore();
    }

    public boolean isInWishlist(SanPham sanPham) {
        return wishlistItems.stream().anyMatch(item -> item.getTen().equals(sanPham.getTen()));
    }

    public List<SanPham> getWishlistItems() {
        return new ArrayList<>(wishlistItems);
    }

    public int getWishlistCount() {
        return wishlistItems.size();
    }

    public void clearWishlist() {
        wishlistItems.clear();
        saveWishlistToFirestore();
    }
}
