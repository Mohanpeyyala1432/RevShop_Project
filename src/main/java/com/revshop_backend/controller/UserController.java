package com.revshop_backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/buyer/dashboard")
    public String buyerAccess() {
        return "Buyer Dashboard Accessed";
    }

    @GetMapping("/seller/dashboard")
    public String sellerAccess() {
        return "Seller Dashboard Accessed";
    }
}
