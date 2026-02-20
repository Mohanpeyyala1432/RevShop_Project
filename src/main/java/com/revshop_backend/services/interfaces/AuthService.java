package com.revshop_backend.services.interfaces;

import com.revshop_backend.dto.RegisterRequest;
import com.revshop_backend.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(String email, String password);

    String register(RegisterRequest request);

    String forgotPassword(String email);

    String verifyOtp(String email, String otp);

    String resetPassword(String email, String newPassword);
}
