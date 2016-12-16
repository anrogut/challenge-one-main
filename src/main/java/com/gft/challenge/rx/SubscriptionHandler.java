package com.gft.challenge.rx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import rx.Subscription;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.FileSystem;

@Component
@SessionScope
public class SubscriptionHandler implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionHandler.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileSystem fileSystem;

    private FileEventReactiveStream fileEventReactiveStream;

    private Subscription fileEventSubscription;

    @Autowired
    public SubscriptionHandler(SimpMessagingTemplate simpMessagingTemplate, FileSystem fileSystem, FileEventReactiveStream fileEventReactiveStream) throws IOException {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.fileSystem = fileSystem;
        this.fileEventReactiveStream = fileEventReactiveStream;
        LOG.info("Successfully created handler: {}", this);
    }

    public Subscription observeDirectory(@NotNull String path, @NotNull int endpointId) throws IOException {
        if (fileEventSubscription != null) {
            return fileEventSubscription;
        }
        fileEventSubscription = fileEventReactiveStream.getEventStream(fileSystem.getPath(path))
                .subscribe(new FileEventReactiveStreamObserver(simpMessagingTemplate, endpointId));
        return fileEventSubscription;
    }

    public Subscription getSubscription() {
        return fileEventSubscription;
    }

    @Override
    public void close() throws Exception {
        fileEventSubscription.unsubscribe();
        LOG.info("Successfully unsubscribed from reactive stream: {}", this);
    }
}
