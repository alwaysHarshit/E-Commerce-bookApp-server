package org.booknest.cartservice.repo;

import org.booknest.cartservice.entity.Cart;
import org.booknest.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {

    Optional<Cart> findByCartId(Long cartId);

    Optional<Cart> findByUserId(Long userId);

}
