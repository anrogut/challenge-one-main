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
    public ResponseEntity<Void> connect(@Value("${observable.path}") String path) throws IOException {
        subscriptionHandler.observeDirectory(path);
        return ResponseEntity.ok().build();
    }
}
