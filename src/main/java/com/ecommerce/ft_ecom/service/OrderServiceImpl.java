package com.ecommerce.ft_ecom.service;

import com.ecommerce.ft_ecom.dtos.OrderDTO;
import com.ecommerce.ft_ecom.dtos.OrderItemDTO;
import com.ecommerce.ft_ecom.dtos.ProductDTO;
import com.ecommerce.ft_ecom.exceptions.APIException;
import com.ecommerce.ft_ecom.exceptions.ResourceNotFoundException;
import com.ecommerce.ft_ecom.model.*;
import com.ecommerce.ft_ecom.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ProductRepository productRepository;

    @Transactional
    @Override
    public OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgPaymentId, String pgStatus, String pgResponseMessage, String pgName) {

        Cart cart = cartRepository.findCartByEmail(email);
        if (cart == null) throw new ResourceNotFoundException("Cart", "EmailId", email);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address", "Address Id", addressId));

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("Order Placed");
        order.setAddress(address);
        order.setEmail(email);
        order.setTotalAmount(cart.getTotalPrice());

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        List<CartItems> cartItems = cart.getCartItems();
        if (cartItems == null) throw new APIException("Cart is Empty");

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItems cartItems1 : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setDiscount(cartItems1.getDiscount());
            orderItem.setQuantity(cartItems1.getQuantity());
            orderItem.setOrderProductPrice(cartItems1.getProductPrice());
            orderItem.setProduct(cartItems1.getProduct());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
        orderItems = orderItemRepository.saveAll(orderItems);

        cart.getCartItems().forEach(cartItems1 -> {
            int quantity = cartItems1.getQuantity();
            Product product = cartItems1.getProduct();

            product.setQuantity(product.getQuantity() - quantity);

            productRepository.save(product);

            cartService.deleteProductFromCart(cartItems1.getProduct().getProductId(), cart.getCartId());
        });

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);

        for (OrderItem item : orderItems) {
            OrderItemDTO orderItemDTO = modelMapper.map(item, OrderItemDTO.class);
            ProductDTO productDTOS = modelMapper.map(item.getProduct(), ProductDTO.class);
            orderItemDTO.setProductDTO(productDTOS);
            orderDTO.getOrderItems().add(orderItemDTO);
        }

        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
