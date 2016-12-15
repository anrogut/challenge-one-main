package com.gft.challenge.rest;

import com.gft.challenge.rx.SubscriptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ObserverController {

    private final SubscriptionHandler subscriptionHandler;

    @Autowired
    public ObserverController(SubscriptionHandler subscriptionHandler) {
        this.subscriptionHandler = subscriptionHandler;
    }

    @GetMapping("/connect")
    public ResponseEntity<String> connect(@Value("${observable.path}") String path) throws IOException {
        String endpointId = subscriptionHandler.toString();
        subscriptionHandler.observeDirectory(path,endpointId);
        return ResponseEntity.ok().body(endpointId);
    }

    @GetMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat() {
        return ResponseEntity.ok().build();
    }
}
