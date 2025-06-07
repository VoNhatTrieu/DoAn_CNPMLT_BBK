package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class ghmanager {
    private static ghmanager instance;
    private List<SanPham> cartItems;

    private ghmanager() {
        cartItems = new ArrayList<>();
    }

    public static ghmanager getInstance() {
        if (instance == null) {
            instance = new ghmanager();
        }
        return instance;
    }

    public List<SanPham> getCartItems() {
        return cartItems;
    }

    public void updateCartItem(SanPham item) {
        // Tìm và cập nhật sản phẩm trong giỏ hàng
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getTen().equals(item.getTen())) {
                cartItems.set(i, item); // Cập nhật sản phẩm
                break;
            }
        }
    }

    public void addToCart(SanPham item) {
        cartItems.add(item);
    }
}