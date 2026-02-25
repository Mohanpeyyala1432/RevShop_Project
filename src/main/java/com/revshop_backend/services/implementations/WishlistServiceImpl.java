package com.revshop_backend.services.implementations;
import com.revshop_backend.exception.ResourceNotFoundException;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.User;
import com.revshop_backend.model.Wishlist;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.UserRepository;
import com.revshop_backend.repository.WishlistRepository;
import com.revshop_backend.services.interfaces.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void addToWishlist(Long productId) {

        User user = getLoggedInUser();

        log.info("User {} attempting to add product {} to wishlist",
                user.getEmail(), productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID {}", productId);
                    return new ResourceNotFoundException("Product not found");
                });

        wishlistRepository.findByUserAndProduct(user, product)
                .ifPresent(w -> {
                    log.warn("Product {} already exists in wishlist for user {}",
                            productId, user.getEmail());
                    throw new IllegalStateException("Product already in wishlist");
                });

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        wishlistRepository.save(wishlist);

        log.info("Product {} successfully added to wishlist for user {}",
                productId, user.getEmail());
    }

    @Override
    public void removeFromWishlist(Long productId) {

        User user = getLoggedInUser();

        log.info("User {} attempting to remove product {} from wishlist",
                user.getEmail(), productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID {}", productId);
                    return new ResourceNotFoundException("Product not found");
                });

        Wishlist wishlist = wishlistRepository
                .findByUserAndProduct(user, product)
                .orElseThrow(() -> {
                    log.error("Wishlist item not found for product {} and user {}",
                            productId, user.getEmail());
                    return new ResourceNotFoundException("Wishlist item not found");
                });

        wishlistRepository.delete(wishlist);

        log.info("Product {} successfully removed from wishlist for user {}",
                productId, user.getEmail());
    }

    @Override
    public List<Wishlist> getWishlist() {

        User user = getLoggedInUser();

        log.info("Fetching wishlist for user {}", user.getEmail());

        return wishlistRepository.findByUser(user);
    }

    private User getLoggedInUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (email == null || email.equals("anonymousUser")) {
            log.error("Unauthorized access attempt to wishlist");
            throw new IllegalStateException("User not authenticated");
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email {}", email);
                    return new ResourceNotFoundException("User not found");
                });
    }
}