package com.zosh.repository;

import com.zosh.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartReponsitory  extends JpaRepository<Cart,Long> {

    public Cart findByCustomerId(Long userId);


}
