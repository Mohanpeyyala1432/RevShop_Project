package com.revshop_backend.services.interfaces;

import com.revshop_backend.model.Wishlist;

import java.util.List;

public interface WishlistService {

    void addToWishlist(Long productId);

    void removeFromWishlist(Long productId);

    List<Wishlist> getWishlist();
}