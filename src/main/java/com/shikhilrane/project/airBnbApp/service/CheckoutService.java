package com.shikhilrane.project.airBnbApp.service;

import com.shikhilrane.project.airBnbApp.entity.Booking;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
