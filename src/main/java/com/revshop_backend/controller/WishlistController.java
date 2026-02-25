package com.revshop_backend.controller;

import com.revshop_backend.model.Wishlist;
import com.revshop_backend.services.interfaces.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{productId}")
    public String addToWishlist(@PathVariable Long productId) {
        wishlistService.addToWishlist(productId);
        return "Product added to wishlist";
    }

    @DeleteMapping("/{productId}")
    public String removeFromWishlist(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(productId);
        return "Product removed from wishlist";
    }

    @GetMapping
    public List<Wishlist> getWishlist() {
        return wishlistService.getWishlist();
    }
}