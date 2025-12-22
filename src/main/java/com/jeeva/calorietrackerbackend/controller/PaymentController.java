package com.jeeva.calorietrackerbackend.controller;


import com.jeeva.calorietrackerbackend.dto.AuthResponse;
import com.jeeva.calorietrackerbackend.service.PaymentService;
import com.jeeva.calorietrackerbackend.service.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log =
            LoggerFactory.getLogger(PaymentController.class);

    private final RazorpayClient razorpayClient;

    private final PaymentService paymentService;

    private final UserService userService;

    @Value("${RAZORPAY_SECRET}")
    private String razorpaySecret;

    public PaymentController(
            @Value("${RAZORPAY_KEY}") String key,
            @Value("${RAZORPAY_SECRET}") String secret,
            PaymentService paymentService,
            UserService userService
    ) throws RazorpayException {
        this.razorpayClient = new RazorpayClient(key, secret);
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder() throws RazorpayException {

        log.info("Creating Razorpay order");

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", 19900); // paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "sub_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(orderRequest);

        String orderId = order.get("id");
        Long amount = ((Number) order.get("amount")).longValue();
        String currency = order.get("currency");

        log.info("Razorpay order created | orderId={} amount={} currency={}",
                orderId, amount, currency);

        // persist payment
        paymentService.createOrder(orderId, amount, currency);

        log.info("Payment record saved | orderId={}", orderId);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("amount", amount);
        response.put("currency", currency);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {

        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");

        log.info("Verifying payment | orderId={} paymentId={}",
                orderId, paymentId);

        String payload = orderId + "|" + paymentId;
        String expectedSignature = hmacSha256(payload, razorpaySecret);

        if (!expectedSignature.equals(signature)) {
            log.warn("Payment verification FAILED | orderId={}", orderId);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid signature");
        }

        log.info("Payment verified successfully | orderId={}", orderId);

        // TODO:
         AuthResponse authResponse = paymentService.markSuccessUser(orderId, paymentId);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelPayment(@RequestBody Map<String, String> data) {


        String orderId = data.get("orderId");
        log.info("Cancelling payment | orderId={}", orderId);
        paymentService.markCancel(orderId);
        log.info("Payment Cancelled successfully | orderId={}", orderId);

        return ResponseEntity.ok("Payment Cancelled");
    }



    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            return Hex.encodeHexString(mac.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Signature verification failed", e);
        }
    }
}
