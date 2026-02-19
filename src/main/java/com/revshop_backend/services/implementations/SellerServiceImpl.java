package com.revshop_backend.services.implementations;

import com.revshop_backend.model.Seller;
import com.revshop_backend.repository.SellerRepository;
import com.revshop_backend.services.interfaces.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    private static final Logger logger = LoggerFactory.getLogger(SellerServiceImpl.class);

    @Override
    public Seller getSellerById(Long sellerId) {
        logger.info("Fetching seller with ID: {}", sellerId);

        return sellerRepository.findById(sellerId)
                .orElseThrow(() -> {
                    logger.error("Seller not found with ID: {}", sellerId);
                    return new RuntimeException("Seller not found");
                });
    }
}