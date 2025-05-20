package com.zosh.request;

import com.zosh.model.Address;
import com.zosh.model.ContactInformation;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class CreateRestaurantRequest {
    private Long id;
    private String name;
    private String descriptions;
    private String cuisineType;
    private Address address;
    private ContactInformation contactInformation;
    private String openingHours;  // Fixed spelling
    private List<String> images;
}
