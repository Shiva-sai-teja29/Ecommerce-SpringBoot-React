package com.ecommerce.ft_ecom.service;

import com.ecommerce.ft_ecom.dtos.OrderDTO;
import jakarta.transaction.Transactional;

public interface OrderService {

    @Transactional
    OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgPaymentId, String pgStatus, String pgResponseMessage, String pgName);
}
