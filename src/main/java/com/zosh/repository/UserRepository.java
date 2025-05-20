package com.zosh.repository;

import com.zosh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    public  User findByEmail(String username);
  //  @Query("SELECT u FROM User u Where u.status='PENDING'")
    //public List<User> getPenddingRestaurantOwners();


}
