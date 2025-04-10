package com.ecommerce.ft_ecom.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponse {
    private String message;
    private boolean status;

    public ApiResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
    }
}
