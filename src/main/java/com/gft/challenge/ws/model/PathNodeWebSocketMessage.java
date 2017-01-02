package com.gft.challenge.ws.model;

import com.gft.challenge.tree.Node;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class PathNodeWebSocketMessage implements WebSocketMessage<Node<Path>> {

    private WebSocketMessageType messageType;
    private String description;
    private Node<Path> payload;

    public PathNodeWebSocketMessage(){
        //for jackson
    }

    private PathNodeWebSocketMessage(WebSocketMessageType type, String description, Node<Path> payload) {
        this.messageType = type;
        this.description = description;
        this.payload = payload;
    }

    @NotNull
    public static PathNodeWebSocketMessage withErrorMessage(@NotNull String message) {
        return new PathNodeWebSocketMessage(WebSocketMessageType.ERROR, message, null);
    }

    @NotNull
    public static PathNodeWebSocketMessage from(@NotNull Node<Path> payload) {
        return new PathNodeWebSocketMessage(WebSocketMessageType.DEFAULT, null, payload);
    }

    @NotNull
    public static PathNodeWebSocketMessage withCompleteMessage(@NotNull String message) {
        return new PathNodeWebSocketMessage(WebSocketMessageType.COMPLETE, message, null);
    }

    @Override
    public WebSocketMessageType getMessageType() {
        return messageType;
    }
    @Override
    public Node<Path> getPayload() {
        return payload;
    }
    @Override
    public String getDescription() {
        return description;
    }
}
