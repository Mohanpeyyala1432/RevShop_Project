package com.revshop_backend.services.interfaces;

import com.revshop_backend.model.Seller;

public interface SellerService {
    Seller getSellerById(Long sellerId);
}
