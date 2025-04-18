package com.ecommerce.ft_ecom.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemsDTO {
    private Long cartItemsId;
    private CartDTO cart;
    private ProductDTO product;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
