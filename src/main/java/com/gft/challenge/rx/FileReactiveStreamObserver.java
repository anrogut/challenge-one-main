package com.gft.challenge.rx;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observer;

import java.nio.file.WatchEvent;

public class FileReactiveStreamObserver implements Observer<WatchEvent<?>> {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public FileReactiveStreamObserver(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void onCompleted() {
        simpMessagingTemplate.convertAndSend("/topic/event", "done");
    }

    @Override
    public void onError(Throwable e) {
        simpMessagingTemplate.convertAndSend("/topic/event", e.getMessage());
    }

    @Override
    public void onNext(WatchEvent<?> event) {
        simpMessagingTemplate.convertAndSend("/topic/event", event.kind().toString() + " | " + event.context());
    }
}
