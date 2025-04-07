package com.ecommerce.ft_ecom.service;

import com.ecommerce.ft_ecom.dtos.ProductDTO;
import com.ecommerce.ft_ecom.dtos.ProductResponse;
import com.ecommerce.ft_ecom.exceptions.APIException;
import com.ecommerce.ft_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.ft_ecom.model.Category;
import com.ecommerce.ft_ecom.model.Product;
import com.ecommerce.ft_ecom.repository.CategoryRepository;
import com.ecommerce.ft_ecom.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

    @Value("${project.images}")
    private String path;

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product product: products){
            if (product.getProductName().equalsIgnoreCase(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if (isProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setCategory(category);
            product.setImage(product.getImage());
            double specialPrice = product.getPrice() - (product.getPrice() * 0.01 * product.getDiscount());
            product.setSpecialPrice(specialPrice);
            Product product1 = productRepository.save(product);
            return modelMapper.map(product1, ProductDTO.class);
        } else {
            throw new APIException("Product is already exists!!");
        }
    }

    @Override
    public ProductResponse getProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> product2 = productRepository.findAll(pageable);
        List<Product> product = product2.getContent();
        if (product.isEmpty()) throw new APIException("Product not exists");
        List<ProductDTO> productDTO = product.stream()
                .map(product1 -> modelMapper.map(product1, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setResponse(productDTO);
        productResponse.setPageNumber(product2.getNumber());
        productResponse.setTotalPages(product2.getTotalPages());
        productResponse.setTotalElements(product2.getTotalElements());
        productResponse.setPageSize(product2.getSize());
        productResponse.setLastPage(product2.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse productsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> product2 = productRepository.findByCategoryOrderByPriceAsc(category, pageable);
        List<Product> product = product2.getContent();
        if (product.isEmpty()) throw new APIException("Product not exists");
        List<ProductDTO> productDTO = product.stream()
                .map(product1 -> modelMapper.map(product1, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setResponse(productDTO);
        productResponse.setPageNumber(product2.getNumber());
        productResponse.setTotalPages(product2.getTotalPages());
        productResponse.setTotalElements(product2.getTotalElements());
        productResponse.setPageSize(product2.getSize());
        productResponse.setLastPage(product2.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product product1 = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        Product product = modelMapper.map(productDTO, Product.class);
          double specialPrice = product.getPrice() - (product.getPrice()*0.01* product.getDiscount());
          product1.setProductName(product.getProductName());
          product1.setDescription(product.getDescription());
          product1.setQuantity(product.getQuantity());
          product1.setPrice(product.getPrice());
          product1.setSpecialPrice(specialPrice);
          product1.setDiscount(product.getDiscount());
          Product product2 = productRepository.save(product1);
        return modelMapper.map(product2, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.delete(product);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductResponse productsByKeyWord(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> product2 = productRepository.findByProductNameLikeIgnoreCase("%"+keyword+"%", pageable);
        List<Product> products = product2.getContent();
        if (products.isEmpty()) throw new APIException("Product not exists");
        List<ProductDTO> productDTO = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setResponse(productDTO);
        productResponse.setPageNumber(product2.getNumber());
        productResponse.setTotalPages(product2.getTotalPages());
        productResponse.setTotalElements(product2.getTotalElements());
        productResponse.setPageSize(product2.getSize());
        productResponse.setLastPage(product2.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProductImage(MultipartFile image, Long productId) throws IOException {
        Product product1 = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        String image1 = fileService.uploadImage(path, image);
        product1.setImage(image1);
        Product product2 = productRepository.save(product1);
        return modelMapper.map(product2, ProductDTO.class);
    }
}