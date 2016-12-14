package com.gft.challenge.rx;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.Observer;

import java.nio.file.WatchEvent;

public class FileEventReactiveStreamObserver implements Observer<FileEvent> {

    private static final String TOPIC_EVENT = "/topic/event";
    private final SimpMessagingTemplate simpMessagingTemplate;

    public FileEventReactiveStreamObserver(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void onCompleted() {
        simpMessagingTemplate.convertAndSend(TOPIC_EVENT, "done");
    }

    @Override
    public void onError(Throwable e) {
        simpMessagingTemplate.convertAndSend(TOPIC_EVENT, e.getMessage());
    }

    @Override
    public void onNext(FileEvent event) {
        simpMessagingTemplate.convertAndSend(TOPIC_EVENT, event);
    }
}
