package com.revshop_backend.dto;

import com.revshop_backend.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartResponseDTO {

    private List<CartItemDTO> items;
    private double totalAmount;

    public CartResponseDTO(){}

}
