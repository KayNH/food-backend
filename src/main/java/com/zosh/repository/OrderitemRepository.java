package com.zosh.repository;

import com.zosh.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderitemRepository extends JpaRepository<OrderItem,Long> {

}
