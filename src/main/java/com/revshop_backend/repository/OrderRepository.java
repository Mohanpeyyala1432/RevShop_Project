package com.revshop_backend.repository;

import com.revshop_backend.model.Order;
import com.revshop_backend.model.OrderStatus;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByOrderIdDesc(User user);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM Order o JOIN o.orderItems oi " +
            "WHERE o.user = :user AND oi.product = :product AND o.status = :status")
    boolean hasUserPurchasedProduct(@Param("user") User user,
                                    @Param("product") Product product,
                                    @Param("status") OrderStatus status);
}
