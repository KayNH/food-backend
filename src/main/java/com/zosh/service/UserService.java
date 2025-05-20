package com.zosh.service;

import com.zosh.model.User;

import java.util.List;

public interface UserService {
    public User findUserByJwtToken(String jwt) throws  Exception;

    public User findUserByEmail(String email) throws  Exception;

    public List<User> findAllUsers();

    public List<User> getPenddingRestaurantOwner();

    void updatePassword(User user, String newPassword);

    void sendPasswordResetEmail(User user);

}
