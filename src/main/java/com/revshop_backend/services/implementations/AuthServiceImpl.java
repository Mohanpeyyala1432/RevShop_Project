package com.revshop_backend.services.implementations;
import org.springframework.transaction.annotation.Transactional;

import com.revshop_backend.dto.LoginResponse;
import com.revshop_backend.dto.RegisterRequest;
import com.revshop_backend.exception.InvalidCredentialsException;
import com.revshop_backend.model.Address;
import com.revshop_backend.model.OTP;
import com.revshop_backend.model.Role;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.AddressRepository;
import com.revshop_backend.repository.PasswordOtpRepository;
import com.revshop_backend.repository.UserRepository;
import com.revshop_backend.security.JwtUtil;
import com.revshop_backend.services.interfaces.AuthService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger =
            LogManager.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final JwtUtil jwtUtil;
    private final PasswordOtpRepository passwordOtpRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AddressRepository addressRepository,
                           JwtUtil jwtUtil,
                           PasswordOtpRepository passwordOtpRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.jwtUtil = jwtUtil;
        this.passwordOtpRepository = passwordOtpRepository;
    }

    @Override
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already registered!";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setBusinessName(request.getBusinessName());
        user.setRole(Role.valueOf(request.getRole()));

        User savedUser = userRepository.save(user);

        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setUser(savedUser);

        addressRepository.save(address);

        return "User registered successfully!";
    }


    @Override
    public LoginResponse login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }


    @Override
    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        OTP passwordOtp = new OTP();
        passwordOtp.setEmail(email);
        passwordOtp.setOtp(otp);
        passwordOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));

        passwordOtpRepository.save(passwordOtp);

        logger.info("OTP for {} is {}", email, otp);

        return "OTP sent successfully";
    }

    @Override
    public String verifyOtp(String email, String otp) {

        OTP storedOtp = passwordOtpRepository
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (!storedOtp.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (storedOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        passwordOtpRepository.delete(storedOtp);

        return "OTP verified successfully";
    }

    @Override
    public String resetPassword(String email, String newPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete OTP after successful reset
        passwordOtpRepository.deleteByEmail(email);

        logger.info("Password reset successfully for {}", email);

        return "Password reset successfully";
    }
}
