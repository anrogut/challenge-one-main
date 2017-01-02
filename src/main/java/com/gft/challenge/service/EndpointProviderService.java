package com.gft.challenge.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EndpointProviderService {

    private final Map<String, Integer> sessionEndpoints = new ConcurrentHashMap<>();
    private final AtomicInteger endPointNumber;

    public EndpointProviderService() {
        endPointNumber = new AtomicInteger(1);
    }

    public Optional<Integer> getEndpoint(@NotNull String sessionId) {
        return Optional.ofNullable(sessionEndpoints.get(sessionId));
    }

    public void addEndpoint(String sessionId) {
        sessionEndpoints.put(sessionId, endPointNumber.getAndIncrement());
    }

    public int removeEndpoint(String sessionId) {
        return sessionEndpoints.remove(sessionId);
    }

}
