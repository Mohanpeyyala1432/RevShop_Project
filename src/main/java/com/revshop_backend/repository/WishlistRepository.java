package com.revshop_backend.repository;

import com.revshop_backend.model.Product;
import com.revshop_backend.model.User;
import com.revshop_backend.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUser(User user);

    Optional<Wishlist> findByUserAndProduct(User user, Product product);
}