package com.gft.challenge.rest;

import com.gft.challenge.rx.SubscriptionHandler;
import com.gft.challenge.service.EndpointProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
public class ObserverController {

    private static final String HEARTBEAT_MESSAGE = "Great! You're still here!";

    private final SubscriptionHandler subscriptionHandler;
    private final EndpointProviderService endpointProviderService;

    @Autowired
    public ObserverController(SubscriptionHandler subscriptionHandler, EndpointProviderService endpointProviderService) {
        this.subscriptionHandler = subscriptionHandler;
        this.endpointProviderService = endpointProviderService;
    }

    @GetMapping("/connect")
    public ResponseEntity<Integer> connect(@Value("${observable.path}") String path, HttpSession session) throws IOException {
        int endpointId = endpointProviderService.getEndpoint(session.getId())
                .orElseThrow(() -> new IllegalStateException("Something went wrong"));
        subscriptionHandler.observeDirectory(path, endpointId);
        return ResponseEntity.ok().body(endpointId);
    }

    @GetMapping("/structure")
    public ResponseEntity<Void> getDirectoryStructure(@Value("${observable.path}") String path, HttpSession session) throws IOException {
        int endpointId = endpointProviderService.getEndpoint(session.getId())
                .orElseThrow(() -> new IllegalStateException("Something went wrong"));
        subscriptionHandler.sendDirectoryStructure(path, endpointId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/heartbeat")
    public ResponseEntity<String> heartbeat() {
        return ResponseEntity.ok().body(HEARTBEAT_MESSAGE);
    }
}
