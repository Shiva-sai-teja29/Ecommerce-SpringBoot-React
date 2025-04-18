package com.ecommerce.ft_ecom.controller;

import com.ecommerce.ft_ecom.dtos.CartDTO;
import com.ecommerce.ft_ecom.model.Cart;
import com.ecommerce.ft_ecom.repository.CartRepository;
import com.ecommerce.ft_ecom.service.CartService;
import com.ecommerce.ft_ecom.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductsToCart(@PathVariable Long productId,
                                                     @PathVariable Integer quantity){
        CartDTO cartDTO = cartService.addProducts(productId, quantity);
        return new ResponseEntity<CartDTO>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> cartDTO = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTO, HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getUserCart(){
        String email = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(email);
        CartDTO cartDTO = cartService.getUserCart(email, cart.getCartId());
        return new ResponseEntity<>(cartDTO, HttpStatus.FOUND);
    }

    @PutMapping("/carts/products/{productId}/operation/{operation}")
    public ResponseEntity<CartDTO> updateCart(@PathVariable Long productId,
                                              @PathVariable String operation){
        CartDTO cartDTO = cartService.updateTheUserCart(productId,
                operation.equalsIgnoreCase("delete")? -1 : 1);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/products/{productId}/cart/{cartId}")
    public ResponseEntity<String> deleteProductCart(@PathVariable Long productId,
                                                        @PathVariable Long cartId){
        String status = cartService.deleteProductFromCart(productId, cartId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
