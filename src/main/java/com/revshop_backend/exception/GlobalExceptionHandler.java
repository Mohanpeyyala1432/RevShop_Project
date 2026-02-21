package com.revshop_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // invalid credentials (LOGIN MODULE)
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", ex.getMessage()));
    }

    // DTO Validation Errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Manual validation errors from service layer
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Generic runtime fallback
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Product module exceptions
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProduct(ProductNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    //  Category module exception
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> handleCategory(CategoryNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    //  User module exception
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUser(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Cart module exceptions
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<String> handleCartItem(CartItemNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<String> handleQuantity(InvalidQuantityException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedCartAccessException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedCartAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<String> handleCart(CartNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<String> handleEmptyCart(EmptyCartException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleStock(InsufficientStockException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<String> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NoOrdersFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoOrders(NoOrdersFoundException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}