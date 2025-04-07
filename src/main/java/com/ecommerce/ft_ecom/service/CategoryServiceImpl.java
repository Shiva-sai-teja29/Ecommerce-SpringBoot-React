package com.ecommerce.ft_ecom.service;

import com.ecommerce.ft_ecom.dtos.CategoryDTO;
import com.ecommerce.ft_ecom.dtos.CategoryResponse;
import com.ecommerce.ft_ecom.exceptions.APIException;
import com.ecommerce.ft_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.ft_ecom.model.Category;
import com.ecommerce.ft_ecom.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBY, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBY).ascending()
                :Sort.by(sortBY).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> category = categoryRepository.findAll(pageable);
        if (category.isEmpty()) throw new APIException("Categories not exists");
        List<CategoryDTO> categoryDTO = category.stream()
                .map(category1 -> modelMapper.map(category1, CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTO);
        categoryResponse.setPageNumber(category.getNumber());
        categoryResponse.setPageSize(category.getSize());
        categoryResponse.setTotalPages(category.getTotalPages());
        categoryResponse.setTotalElements(category.getTotalElements());
        categoryResponse.setLastPage(category.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFromDB = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (categoryFromDB != null) throw new APIException("Category with name "+categoryDTO.getCategoryName()+" is already exists");
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category category1 = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        category1.setCategoryName(category.getCategoryName());
        Category savedCategory = categoryRepository.save(category1);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
