package com.revshop_backend.services.implementations;


import com.revshop_backend.dto.ReviewDTO;
import com.revshop_backend.exception.ProductNotFoundException;
import com.revshop_backend.model.OrderStatus;
import com.revshop_backend.model.Product;
import com.revshop_backend.model.Review;
import com.revshop_backend.model.User;
import com.revshop_backend.repository.OrderRepository;
import com.revshop_backend.repository.ProductRepository;
import com.revshop_backend.repository.ReviewRepository;
import com.revshop_backend.security.AuthUtil;
import com.revshop_backend.services.interfaces.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {


}
