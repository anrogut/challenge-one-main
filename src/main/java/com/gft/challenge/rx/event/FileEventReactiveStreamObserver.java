package com.gft.challenge.rx.event;

import com.gft.challenge.rx.event.model.FileEvent;
import com.gft.challenge.ws.model.EventWebSocketMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observer;

public class FileEventReactiveStreamObserver implements Observer<FileEvent> {

    private static final String TOPIC_EVENT = "/topic/event/";
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final int endpointId;

    public FileEventReactiveStreamObserver(SimpMessagingTemplate simpMessagingTemplate, int endpointId) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.endpointId = endpointId;
    }

    @Override
    public void onCompleted() {
        simpMessagingTemplate
                .convertAndSend(TOPIC_EVENT + endpointId, EventWebSocketMessage.withCompleteMessage("Done"));
    }

    @Override
    public void onError(Throwable e) {
        simpMessagingTemplate
                .convertAndSend(TOPIC_EVENT + endpointId, EventWebSocketMessage
                        .withErrorMessage(e.getMessage() == null ? "Error" : e.getMessage()));
    }

    @Override
    public void onNext(FileEvent event) {
        simpMessagingTemplate
                .convertAndSend(TOPIC_EVENT + endpointId, EventWebSocketMessage.from(event));
    }
}
