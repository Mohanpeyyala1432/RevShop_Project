package com.revshop_backend.services;

import com.revshop_backend.model.Seller;
import com.revshop_backend.repository.SellerRepository;
import com.revshop_backend.services.implementations.SellerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellerServiceImplTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerServiceImpl sellerService;

    private Seller seller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        seller = new Seller();
        seller.setSellerId(1L);        // Correct case
        seller.setSellerName("Test Seller"); // Correct case
        seller.setEmail("test@seller.com");  // Correct case
    }

    @Test
    void testGetSellerByIdFound() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        Seller result = sellerService.getSellerById(1L);

        assertEquals("Test Seller", result.getSellerName());
        assertEquals("test@seller.com", result.getEmail());
    }

    @Test
    void testGetSellerByIdNotFound() {
        when(sellerRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> sellerService.getSellerById(2L));

        assertTrue(exception.getMessage().contains("Seller not found"));
    }
}