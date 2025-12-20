package com.jeeva.calorietrackerbackend.service;

import com.jeeva.calorietrackerbackend.controller.PaymentController;
import com.jeeva.calorietrackerbackend.model.Payment;
import com.jeeva.calorietrackerbackend.model.PaymentStatus;
import com.jeeva.calorietrackerbackend.model.User;
import com.jeeva.calorietrackerbackend.repository.PaymentRepository;

import com.jeeva.calorietrackerbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentController.class);


    private final UserService userService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public PaymentService(UserService userService) {
        this.userService = userService;
    }

    public void createOrder(String orderId, Long amount, String currency) {

        String userMail = SecurityContextHolder.getContext().getAuthentication().getName();


        User user = userRepository.findByEmail(userMail)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", userMail);
                    return new RuntimeException("User not found");
                });

        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setOrderId(orderId);
        payment.setCurrency(currency);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setUser(user);
        paymentRepository.save(payment);
    }

    @Transactional
    public void markSuccess(String orderId, String paymentId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() ->
                new IllegalStateException("Payment not found for orderId=" + orderId)
        );
        // Idempotency guard
        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            return; // already processed
        }

        // Do not override cancelled payments
        if (payment.getPaymentStatus() == PaymentStatus.CANCELED) {
            return;
        }
        payment.setPaymentId(paymentId);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        User user = payment.getUser();
        user.setIsPrimeUser("true");
        userService.activateSubscription(user);
    }

    @Transactional
    public void markCancel(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() ->
                new IllegalStateException("Payment not found for orderId=" + orderId)
        );
        // Idempotency guard
        if (payment.getPaymentStatus() == PaymentStatus.CANCELED) {
            return; // already cancelled
        }

        // Do not cancel completed payments
        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
            return;
        }
        payment.setPaymentStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }
}
