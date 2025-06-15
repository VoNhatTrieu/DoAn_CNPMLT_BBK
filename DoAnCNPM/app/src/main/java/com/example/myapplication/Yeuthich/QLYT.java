package com.example.myapplication.Yeuthich;
import com.example.myapplication.SanPham;

import java.util.ArrayList;
import java.util.List;
public class QLYT {
    private static QLYT instance;
    private List<SanPham> wishlistItems;

    private QLYT() {
        wishlistItems = new ArrayList<>();
    }

    public static QLYT getInstance() {
        if (instance == null) {
            instance = new QLYT();
        }
        return instance;
    }

    public void addToWishlist(SanPham sanPham) {
        if (!isInWishlist(sanPham)) {
            wishlistItems.add(sanPham);
        }
    }

    public void removeFromWishlist(SanPham sanPham) {
        wishlistItems.removeIf(item -> item.getTen().equals(sanPham.getTen()));
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
    }

}
