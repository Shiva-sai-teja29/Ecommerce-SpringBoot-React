package com.ecommerce.ft_ecom.service;

import com.ecommerce.ft_ecom.dtos.CategoryDTO;
import com.ecommerce.ft_ecom.dtos.CategoryResponse;


public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBY, String sortOrder);
    CategoryDTO createCategory(CategoryDTO category);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
