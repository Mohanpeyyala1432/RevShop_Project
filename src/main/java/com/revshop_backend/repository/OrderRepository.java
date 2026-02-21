package com.revshop_backend.repository;

import com.revshop_backend.model.Order;
import com.revshop_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByOrderIdDesc(User user);

    @Query("""
        SELECT COUNT(oi) > 0
        FROM Order o
        JOIN o.orderItems oi
        WHERE o.user = :user
        AND oi.product.productId = :productId
    """)
    boolean hasUserPurchasedProduct(@Param("user") User user,
                                    @Param("productId") Long productId);
}