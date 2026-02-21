package com.revshop_backend.services.implementations;

import com.revshop_backend.model.Cart;
import com.revshop_backend.model.CartItem;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.CartItemRepository;
import com.revshop_backend.repository.CartRepository;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.UserRepository;
import com.revshop_backend.services.interfaces.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    // Add product to cart
    @Override
    public void addToCart(Long productId, Integer quantity) {
        log.info("Add to Cart Started | ProductId: {} | Quantity: {}", productId, quantity);

        User user = getLoggedInUser();

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalAmount(0.0);
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cartItemRepository.save(cartItem);

        recalculateCartTotal(cart);
        log.info("Product {} added/updated in cart for user {}. Quantity: {}",
                product.getProductName(), user.getEmail(), cartItem.getQuantity());
    }

    // Get all items in cart
    @Override
    public List<CartItem> getCartItems(User user) {
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setTotalAmount(0.0);
            return cartRepository.save(newCart);
        });

        return cartItemRepository.findByCart(cart);
    }

    // Get currently logged-in user
    @Override
    public User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Update quantity of a cart item
    @Override
    public void updateCartItemQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem not found"));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        recalculateCartTotal(cartItem.getCart());
        log.info("CartItem {} quantity updated to {}", cartItemId, quantity);
    }

    // Delete a cart item
    @Override
    public void deleteCartItem(Long cartItemId) {
        User user = getLoggedInUser();

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot delete this item");
        }

        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);

        recalculateCartTotal(cart);
        log.info("CartItem {} deleted from cart", cartItemId);
    }

    // Recalculate total amount of cart
    private void recalculateCartTotal(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCart(cart);
        double total = items.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(total);
        cartRepository.save(cart);
        log.info("Cart total recalculated: {}", total);
    }
}