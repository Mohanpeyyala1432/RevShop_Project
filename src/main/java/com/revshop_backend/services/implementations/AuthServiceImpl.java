package com.revshop_backend.services.implementations;

import com.revshop_backend.dto.LoginResponse;
import com.revshop_backend.dto.RegisterRequest;
import com.revshop_backend.model.Address;
import com.revshop_backend.model.Role;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.AddressRepository;
import com.revshop_backend.repository.UserRepository;
import com.revshop_backend.services.interfaces.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.revshop_backend.security.JwtUtil;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AddressRepository addressRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.jwtUtil = jwtUtil;
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

        // Save Address
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setUser(savedUser);

        addressRepository.save(address);

        return "User registered successfully!";
    }
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
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
}