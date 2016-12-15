package com.gft.challenge.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import rx.Subscription;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;

@Component
@SessionScope
public class SubscriptionHandler implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionHandler.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileSystem fileSystem;

    private FileEventReactiveStream fileEventReactiveStream;

    private List<Subscription> subscriptions = new ArrayList<>();

    @Autowired
    public SubscriptionHandler(SimpMessagingTemplate simpMessagingTemplate, FileSystem fileSystem, FileEventReactiveStream fileEventReactiveStream) throws IOException {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.fileSystem = fileSystem;
        this.fileEventReactiveStream = fileEventReactiveStream;
        LOG.info("Successfully created handler: {}", this);
    }

    public Subscription observeDirectory(String path, String endpointId) throws IOException {
        Subscription subscription = fileEventReactiveStream.getEventStream(fileSystem.getPath(path))
                .subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, endpointId));
        subscriptions.add(subscription);
        return subscription;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    @Override
    public void close() throws Exception {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();
        LOG.info("Successfully unsubscribed from reactive stream: {}", this);
    }
}
