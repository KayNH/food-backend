package com.zosh.request;

import com.zosh.model.Address;
import lombok.Data;

@Data
public class OrderRequest {

    private Long restaurantId;
    private Long addressId; // địa chỉ cũ (nếu có)
    private Address deliveryAddress; // địa chỉ mới (nếu có)
}
