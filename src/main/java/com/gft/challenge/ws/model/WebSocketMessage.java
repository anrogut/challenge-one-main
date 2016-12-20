package com.gft.challenge.ws.model;

public interface WebSocketMessage<T> {

    WebSocketMessageType getMessageType();

    T getPayload();

    String getDescription();

}
