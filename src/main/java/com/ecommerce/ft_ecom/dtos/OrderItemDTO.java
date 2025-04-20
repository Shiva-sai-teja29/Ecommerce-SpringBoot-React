package com.ecommerce.ft_ecom.dtos;

import com.ecommerce.ft_ecom.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;
    private ProductDTO productDTO;
    private Integer quantity;
    private double discount;
    private double orderProductPrice;
}
