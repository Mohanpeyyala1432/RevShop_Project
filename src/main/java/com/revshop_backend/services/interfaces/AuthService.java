package com.revshop_backend.services.interfaces;

import com.revshop_backend.dto.RegisterRequest;
import com.revshop_backend.dto.LoginResponse;

public interface AuthService {

    String register(RegisterRequest request);

    LoginResponse login(String email, String password);
}
