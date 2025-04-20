package com.ecommerce.ft_ecom.service;

import com.ecommerce.ft_ecom.dtos.CartDTO;
import com.ecommerce.ft_ecom.dtos.ProductDTO;
import com.ecommerce.ft_ecom.exceptions.APIException;
import com.ecommerce.ft_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.ft_ecom.model.Cart;
import com.ecommerce.ft_ecom.model.CartItems;
import com.ecommerce.ft_ecom.model.Product;
import com.ecommerce.ft_ecom.repository.CartItemRepository;
import com.ecommerce.ft_ecom.repository.CartRepository;
import com.ecommerce.ft_ecom.repository.ProductRepository;
import com.ecommerce.ft_ecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartItemRepository cartItemsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProducts(Long productId, Integer quantity) {

        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productID", productId));

        CartItems cartItems = cartItemsRepository.findCartItemByProductIdAndCartID(productId, cart.getCartId());

        if (cartItems != null) throw new APIException("Product "+product.getProductName()+" already exists in cart");

        if (product.getQuantity() == 0) throw new APIException(product.getProductName()+" is not available");

        if (product.getQuantity()<quantity) throw new APIException("Please order the "+product.getProductName()+" less than or equal quantity "+product.getQuantity()+".");

        CartItems newCartItem = new CartItems();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemsRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItems> cartItems1 = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems1.stream().map(items ->{
            ProductDTO map = modelMapper.map(items.getProduct(), ProductDTO.class);
            map.setQuantity(items.getQuantity());
            return map;
        });
        //cartDTO.setProducts(productStream.collect(Collectors.toList()));
        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> cart = cartRepository.findAll();
        if (cart.isEmpty()) throw new APIException("All carts are empty");

        List<CartDTO> cartDTOS = cart.stream().map(cart1 ->{
            CartDTO cartDTO = modelMapper.map(cart1, CartDTO.class);
            cart1.getCartItems().forEach(c-> c.getProduct().setQuantity(c.getQuantity()));
            List<ProductDTO> productDTOS = cart1.getCartItems().stream()
                    .map(p-> modelMapper.map(p.getProduct(), ProductDTO.class)).toList();
            cartDTO.setProducts(productDTOS);
            return cartDTO;
        }).toList();

        return cartDTOS;
    }

    @Override
    public CartDTO getUserCart(String email, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(email, cartId);
        if (cart == null) throw new APIException("Cart Doesn't exists");
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p->modelMapper.map(p.getProduct(), ProductDTO.class)).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateTheUserCart(Long productId, Integer quantity) {

        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        Long id = userCart.getCartId();
        Cart cart = cartRepository.findById(id).orElseThrow(()-> new APIException("Cart Doesn't exists"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productID", productId));

        if (product.getQuantity() == 0) throw new APIException(product.getProductName()+" is not available");

        if (product.getQuantity()<quantity) throw new APIException("Please order the "+product.getProductName()+" less than or equal quantity "+product.getQuantity()+".");

        CartItems cartItems = cartItemsRepository.findCartItemByProductIdAndCartID(productId, cart.getCartId());

        if (cartItems == null) throw new APIException("Product not exists in cart");

        int newQuantity = cartItems.getQuantity() + quantity;

        if (newQuantity < 0) throw new APIException("The resulting quantity cannot be negative");

        if (newQuantity == 0){
            deleteProductFromCart(id, productId);
        } else {
            cartItems.setQuantity(cartItems.getQuantity() + quantity);
            cartItems.setProductPrice(product.getSpecialPrice());
            cartItems.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItems.getProductPrice()*quantity));
            cartRepository.save(cart);
        }
        CartItems cartItems1 = cartItemsRepository.save(cartItems);
        if (cartItems1.getQuantity() == 0) cartItemsRepository.deleteById(cartItems1.getCartItemId());

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItems> cartItems2 = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems2.stream().map(items ->{
            ProductDTO map = modelMapper.map(items.getProduct(), ProductDTO.class);
            map.setQuantity(items.getQuantity());
            return map;
        });
        //cartDTO.setProducts(productStream.collect(Collectors.toList()));
        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }
    @Transactional
    @Override
    public String deleteProductFromCart(Long productId, Long cartId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(()-> new APIException("Cart Doesn't exists"));

        CartItems cartItems = cartItemsRepository.findCartItemByProductIdAndCartID(productId, cart.getCartId());
        if (cartItems == null) throw new APIException("Product not exists in cart");

        cart.setTotalPrice(cart.getTotalPrice() - (cartItems.getProductPrice() * cartItems.getQuantity()));
        cartItemsRepository.deleteCartItemByProductIdAndCartId(productId, cartId);
        return "Product " + cartItems.getProduct().getProductName()+" removed from cart!";
    }

    @Override
    public void updateProductInCarts(Long cartID, Long productId) {
        Cart cart = cartRepository.findById(cartID)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartID));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItems cartItem = cartItemsRepository.findCartItemByProductIdAndCartID(productId, cartID);

        if (cartItem == null) throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");

        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice
                + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemsRepository.save(cartItem);
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if (userCart !=null){
            return userCart;
        }
        Cart cart = new Cart();
        cart.setUser(authUtil.loggedInUser());
        cart.setTotalPrice(0.00);
        return cartRepository.save(cart);
    }
}
