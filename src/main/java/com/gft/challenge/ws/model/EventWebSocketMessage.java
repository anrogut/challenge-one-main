package com.gft.challenge.ws.model;

import com.gft.challenge.rx.event.model.FileEvent;
import org.jetbrains.annotations.NotNull;

public class EventWebSocketMessage implements WebSocketMessage<FileEvent> {

    private WebSocketMessageType messageType;
    private String description;
    private FileEvent payload;

    EventWebSocketMessage() {
        //for jackson
    }

    private EventWebSocketMessage(WebSocketMessageType type, String description, FileEvent payload) {
        this.messageType = type;
        this.description = description;
        this.payload = payload;
    }

    public static EventWebSocketMessage withErrorMessage(@NotNull String message) {
        return new EventWebSocketMessage(WebSocketMessageType.ERROR, message, null);
    }

    public static EventWebSocketMessage from(@NotNull FileEvent payload) {
        return new EventWebSocketMessage(WebSocketMessageType.DEFAULT, null, payload);
    }

    public static EventWebSocketMessage withCompleteMessage(@NotNull String message) {
        return new EventWebSocketMessage(WebSocketMessageType.COMPLETE, message, null);
    }

    @Override
    public WebSocketMessageType getMessageType() {
        return messageType;
    }

    @Override
    public FileEvent getPayload() {
        return payload;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
