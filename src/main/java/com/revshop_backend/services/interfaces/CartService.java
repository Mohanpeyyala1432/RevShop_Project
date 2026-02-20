package com.revshop_backend.services.interfaces;

import com.revshop_backend.model.CartItem;
import com.revshop_backend.model.User;

import java.util.List;

public interface CartService {

    void addToCart(Long productId, Integer quantity);

    List<CartItem> getCartItems(User user);

    User getLoggedInUser();

    void updateCartItemQuantity(Long cartItemId, Integer quantity);

    void deleteCartItem(Long cartItemId);

}
