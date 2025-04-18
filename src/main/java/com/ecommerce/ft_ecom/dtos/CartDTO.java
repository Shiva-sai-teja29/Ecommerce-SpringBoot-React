package com.ecommerce.ft_ecom.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {
    private Long cartID;
    private Double totalPrice = 0.0;
    private List<ProductDTO> products;
}
