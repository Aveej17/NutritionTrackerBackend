package com.jeeva.calorietrackerbackend.controller;

import com.jeeva.calorietrackerbackend.service.PaymentService;
import com.jeeva.calorietrackerbackend.service.UserService;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/payment")
public class RazorpayWebhookController {

    private static final Logger log =
            LoggerFactory.getLogger(RazorpayWebhookController.class);

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    private final PaymentService paymentService;


    public RazorpayWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        if (!verifySignature(payload, signature)) {
            log.warn("Invalid Razorpay webhook signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        log.info("Razorpay webhook received | event={}", eventType);

        switch (eventType) {

            case "payment.captured" -> handlePaymentCaptured(event);

            case "payment.failed" -> handlePaymentFailed(event);

            default -> log.info("Unhandled Razorpay event: {}", eventType);
        }

        return ResponseEntity.ok("Webhook processed");
    }

    private void handlePaymentCaptured(JSONObject event) {

        JSONObject payment = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String paymentId = payment.getString("id");
        String orderId = payment.getString("order_id");

        log.info("Payment captured | orderId={} paymentId={}", orderId, paymentId);

        paymentService.markSuccess(orderId, paymentId);
    }

    private void handlePaymentFailed(JSONObject event) {

        JSONObject payment = event
                .getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String paymentId = payment.getString("id");
        String orderId = payment.optString("order_id", null);

        log.warn("Payment failed | orderId={} paymentId={}", orderId, paymentId);

        if (orderId != null) {
            paymentService.markCancel(orderId);
        }
    }

    private boolean verifySignature(String payload, String actualSignature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            ));
            String expectedSignature =
                    Hex.encodeHexString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));

            return expectedSignature.equals(actualSignature);

        } catch (Exception e) {
            log.error("Webhook signature verification failed", e);
            return false;
        }
    }
}
