package com.ecommerce.ft_ecom.service;

import com.ecommerce.ft_ecom.dtos.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProducts(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart(String email, Long cartId);

    @Transactional
    CartDTO updateTheUserCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long productId, Long cartId);

    void updateProductInCarts(Long cartID, Long productId);
}
