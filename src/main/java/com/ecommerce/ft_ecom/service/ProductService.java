package com.ecommerce.ft_ecom.service;


import com.ecommerce.ft_ecom.dtos.CategoryDTO;
import com.ecommerce.ft_ecom.dtos.ProductDTO;
import com.ecommerce.ft_ecom.dtos.ProductResponse;
import com.ecommerce.ft_ecom.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO saveProduct(ProductDTO productDTO, Long categoryId);

    ProductResponse getProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse productsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    ProductDTO deleteProduct(Long productId);

    ProductResponse productsByKeyWord(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductDTO updateProductImage(MultipartFile image, Long productId) throws IOException;
}
