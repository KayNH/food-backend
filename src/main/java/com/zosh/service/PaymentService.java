package com.zosh.service;

import com.stripe.exception.StripeException;
import com.zosh.model.Order;
import com.zosh.response.PaymentResponse;

public interface PaymentService {
    public PaymentResponse createPaymentLink(Order order) throws StripeException;
}
