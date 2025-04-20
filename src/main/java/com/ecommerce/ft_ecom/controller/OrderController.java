package com.ecommerce.ft_ecom.controller;

import com.ecommerce.ft_ecom.dtos.OrderDTO;
import com.ecommerce.ft_ecom.dtos.OrderRequestDTO;
import com.ecommerce.ft_ecom.model.Order;
import com.ecommerce.ft_ecom.service.OrderService;
import com.ecommerce.ft_ecom.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    AuthUtil authUtil;

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders/payment/{paymentMethod}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable String paymentMethod,
                                               @RequestBody OrderRequestDTO orderRequestDTO){
        String email = authUtil.loggedInEmail();
        OrderDTO orderDTO = orderService.placeOrder(
                email,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage(),
                orderRequestDTO.getPgName()
        );
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }
}
