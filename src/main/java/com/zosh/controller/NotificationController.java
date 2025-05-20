package com.zosh.controller;

import java.util.List;

import com.zosh.Exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.zosh.model.Notification;
import com.zosh.model.User;
import com.zosh.service.NotificationService;
import com.zosh.service.UserService;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private NotificationService notificationSerivce;
    @Autowired
    private UserService userService;

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> findUsersNotification(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user=userService.findUserByJwtToken(jwt);

        List<Notification> notifications=notificationSerivce.findUsersNotification(user.getId());
        return new ResponseEntity<List<Notification>>(notifications,HttpStatus.ACCEPTED);
    }

}

