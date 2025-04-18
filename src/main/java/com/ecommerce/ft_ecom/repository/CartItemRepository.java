package com.ecommerce.ft_ecom.repository;

import com.ecommerce.ft_ecom.model.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItems, Long> {

    @Query("SELECT ci FROM CartItems ci WHERE ci.product.id = ?1 AND ci.cart.id = ?2")
    CartItems findCartItemByProductIdAndCartID(Long productId, Long cartId);

    @Modifying
    @Query("DELETE FROM CartItems ci WHERE ci.product.id = ?1 AND ci.cart.id = ?2")
    void deleteCartItemByProductIdAndCartId(Long productId, Long cartId);
}
