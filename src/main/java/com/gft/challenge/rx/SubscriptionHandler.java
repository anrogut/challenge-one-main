package com.gft.challenge.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;

@Component
@SessionScope
public class SubscriptionHandler {

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
    }

    public Subscription observeDirectory(String path) throws IOException {
        Subscription subscription = fileEventReactiveStream.getEventStream(fileSystem.getPath(path))
                .subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate));
        subscriptions.add(subscription);
        return subscription;
    }

    @PostConstruct
    public void postConstruct() throws IOException {
        LOG.info("Successfully created handler: {}", this);
    }

    @PreDestroy
    public void preDestroy() throws Exception {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();
        //fileEventReactiveStream.close();
        LOG.info("Successfully unsubscribed from reactive stream: {}", this);
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }
}
